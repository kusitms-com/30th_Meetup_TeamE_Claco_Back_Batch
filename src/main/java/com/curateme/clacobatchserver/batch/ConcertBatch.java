package com.curateme.clacobatchserver.batch;

import com.curateme.clacobatchserver.config.s3.S3Service;
import com.curateme.clacobatchserver.entity.ConcertEntity;
import com.curateme.clacobatchserver.repository.ConcertRepository;
import com.curateme.clacobatchserver.service.ConcertCategoryExtractor;
import com.curateme.clacobatchserver.service.KopisConcertApiReader;
import com.curateme.clacobatchserver.service.KopisDetailApiReader;
import com.curateme.clacobatchserver.service.KopisEntityWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConcertBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ConcertRepository concertRepository;

    private final KopisConcertApiReader kopisApiReader;
    private final KopisDetailApiReader kopisDetailApiReader;
    private final ConcertCategoryExtractor concertCategoryExtractor;
    private final S3Service s3Service;

    public ConcertBatch(JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager, ConcertRepository concertRepository,
        KopisConcertApiReader kopisApiReader,
        KopisDetailApiReader kopisDetailApiReader,
        ConcertCategoryExtractor concertCategoryExtractor,
        S3Service s3Service) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.concertRepository = concertRepository;
        this.kopisApiReader = kopisApiReader;
        this.kopisDetailApiReader = kopisDetailApiReader;
        this.concertCategoryExtractor = concertCategoryExtractor;
        this.s3Service = s3Service;
    }

    @Bean
    public Job kopisJob(KopisEntityWriter writer){
        return new JobBuilder("kopisJob", jobRepository)
            .start(fourthStep())
            .build();
    }

    // 1. Kopis에서 해당 기간에 대한 공연 정보 가져 오기
    @Bean
    public Step firstStep(KopisEntityWriter writer) {
        return new StepBuilder("firstStep", jobRepository)
            .<ConcertEntity, ConcertEntity>chunk(10, platformTransactionManager)
            .reader(kopisApiReader)
            .writer(writer)
            .build();
    }

    // 2. Step1 에서 가져온 공연에 대해 상세 내역 가져 오기
    @Bean
    public Step secondStep() {
        return new StepBuilder("secondStep", jobRepository)
            .tasklet(kopisDetailApiReader, platformTransactionManager)
            .build();
    }

    // 3. 공연 포스터 이미지를 통해서 Flask 서버로 부터 카테고리 추출
    @Bean
    public Step thirdStep() {
        return new StepBuilder("thirdStep", jobRepository)
            .tasklet(concertCategoryExtractorTasklet(), platformTransactionManager)
            .build();
    }

    // 4. 추출한 카테고리 값을 CSV 파일에 저장
    @Bean
    public Step fourthStep() {
        return new StepBuilder("fourthStep", jobRepository)
            .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {

                String folderPath = "datasets";
                String fileName = "concerts.csv";
                String localFilePath = s3Service.downloadCsvFile(folderPath, fileName);

                // 다운로드된 파일 경로 출력 및 파일 존재 여부 확인
                System.out.println("다운로드된 파일 경로: " + localFilePath);
                File downloadedFile = new File(localFilePath);
                if (!downloadedFile.exists()) {
                    System.err.println("파일이 존재하지 않습니다: " + localFilePath);
                    return RepeatStatus.FINISHED;
                }

                List<String> columns = Arrays.asList("concertId", "grand", "delicate", "classical", "modern",
                    "lyrical", "dynamic", "romantic", "tragic", "familiar", "novel");

                // CSV 형식으로 변환할 데이터를 저장
                List<ConcertEntity> concerts = concertRepository.getAllConcertsWithCategories();
                StringJoiner csvContent = new StringJoiner("\n");

                csvContent.add(String.join(",", columns));

                for (ConcertEntity concert : concerts) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("concertId", concert.getMt20id());

                    // 모든 카테고리를 0으로 초기화한 뒤, 실제 데이터를 넣기
                    for (String column : columns.subList(1, columns.size())) {
                        row.put(column, 0.0);
                    }

                    // Categories에서 값을 가져와 CSV에 맞게 추가
                    if (concert.getCategories() != null) {
                        for (Entry<String, Double> entry : concert.getCategories().entrySet()) {
                            // 카테고리가 컬럼에 존재하는지 확인
                            if (columns.contains(entry.getKey())) {
                                row.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }

                    StringJoiner rowContent = new StringJoiner(",");
                    for (String column : columns) {
                        rowContent.add(String.valueOf(row.get(column)));
                    }
                    csvContent.add(rowContent.toString());
                }

                File tempFile = null;
                try {
                    tempFile = File.createTempFile("concerts_", ".csv");

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                        writer.write(csvContent.toString());
                    }

                    // S3에 파일 업로드
                    s3Service.updateAndUploadCsvFile(folderPath, fileName, tempFile.getAbsolutePath());

                } catch (IOException e) {
                    System.err.println("파일 쓰기 오류: " + e.getMessage());
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }

                return RepeatStatus.FINISHED;
            }, platformTransactionManager).build();
    }







    @Bean
    public Tasklet concertCategoryExtractorTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                return concertCategoryExtractor.execute(contribution, chunkContext);
            }
        };
    }
}
