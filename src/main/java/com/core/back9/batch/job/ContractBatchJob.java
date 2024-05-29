package com.core.back9.batch.job;

import com.core.back9.batch.tasklet.ContractTasklet;
import com.core.back9.batch.property.BatchProperty;
import com.core.back9.repository.ContractRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Getter
@Slf4j
public class ContractBatchJob extends DefaultBatchConfiguration implements BatchConfig {

    private final BatchProperty batchProperty;
    private final ContractRepository contractRepository;

    public ContractBatchJob(@Qualifier("contractBatchProperty") BatchProperty batchProperty, ContractRepository contractRepository) {
        this.batchProperty = batchProperty;
        this.contractRepository = contractRepository;
    }

    @Override
    public String getIdentifier() { // scheduler에서 알맞은 객체 value를 꺼내기 위한 식별자 지정
        return "contractBatchConfig";
    }

    @Bean("contract")
    @Override
    public Job createJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Job job = new JobBuilder(batchProperty.getJobName(), jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeStep(jobRepository, transactionManager))
                .build();
        return job;
    }

    @Override
    public Step executeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step step = new StepBuilder("testStep", jobRepository)
                .tasklet(new ContractTasklet(contractRepository), transactionManager) // 생성한 tasklet 부착
                .build();
        return step;
    }

}
