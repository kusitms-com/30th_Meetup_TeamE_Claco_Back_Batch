package com.curateme.clacobatchserver.batch;

import com.curateme.clacobatchserver.entity.ConcertEntity;
import com.curateme.clacobatchserver.service.KopisConcertApiReader;
import com.curateme.clacobatchserver.service.KopisDetailApiReader;
import com.curateme.clacobatchserver.service.KopisEntityWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConcertBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final KopisConcertApiReader kopisApiReader;
    private final KopisDetailApiReader kopisDetailApiReader;

    public ConcertBatch(JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager,
        KopisConcertApiReader kopisApiReader,
        KopisDetailApiReader kopisDetailApiReader) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.kopisApiReader = kopisApiReader;
        this.kopisDetailApiReader = kopisDetailApiReader;
    }

    @Bean
    public Job kopisJob(KopisEntityWriter writer){
        return new JobBuilder("kopisJob", jobRepository)
            .start(firstStep(writer))
            .next(secondStep())
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
    public Step thirdStep(){
        return null;
    }

}
