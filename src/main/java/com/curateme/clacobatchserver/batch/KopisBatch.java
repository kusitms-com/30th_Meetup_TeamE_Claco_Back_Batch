package com.curateme.clacobatchserver.batch;

import com.curateme.clacobatchserver.entity.BeforeEntity;
import com.curateme.clacobatchserver.repository.AfterRepository;
import com.curateme.clacobatchserver.repository.BeforeRepository;
import com.curateme.clacobatchserver.service.KopisApiReader;
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
public class KopisBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final KopisApiReader kopisApiReader;
    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    public KopisBatch(JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository,
        AfterRepository afterRepository, KopisApiReader kopisApiReader) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
        this.afterRepository = afterRepository;
        this.kopisApiReader = kopisApiReader;
    }

    @Bean
    public Job kopisJob(KopisEntityWriter writer){
        return new JobBuilder("kopisJob", jobRepository)
            .start(firstStep(writer))
            .build();
    }

    @Bean
    public Step firstStep(KopisEntityWriter writer) {
        return new StepBuilder("firstStep", jobRepository)
            .<BeforeEntity, BeforeEntity>chunk(10, platformTransactionManager)
            .reader(kopisApiReader)
            .writer(writer)
            .build();
    }




}
