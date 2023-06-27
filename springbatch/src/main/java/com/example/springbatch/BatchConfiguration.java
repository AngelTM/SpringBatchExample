package com.example.springbatch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.springbatch.entities.Person;
import com.example.springbatch.steps.ItemDecompressStep;
import com.example.springbatch.steps.ItemProcessorStep;
import com.example.springbatch.steps.ItemReaderStep;
import com.example.springbatch.steps.ItemWriterStep;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

        @Bean
    public ExecutionContextSerializer customSerializer() {
        return new Jackson2ExecutionContextStringSerializer(Person.class.getName());
    }

    @Bean
    BatchConfigurer myBatchConfigurer(DataSource dataSource,
                                      ExecutionContextSerializer executionContextSerializer,
                                      PlatformTransactionManager transactionManager) {
        return new DefaultBatchConfigurer(dataSource) {
            @Override
            protected JobExplorer createJobExplorer() throws Exception {
                JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
                jobExplorerFactoryBean.setDataSource(dataSource);
                jobExplorerFactoryBean
                                .setSerializer(executionContextSerializer);
                jobExplorerFactoryBean.afterPropertiesSet();
                return jobExplorerFactoryBean.getObject();
            }

            @Override
            protected JobRepository createJobRepository() throws Exception {
                JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
                jobRepositoryFactoryBean.setDataSource(dataSource);
                jobRepositoryFactoryBean
                                .setSerializer(executionContextSerializer);
                jobRepositoryFactoryBean.setTransactionManager(transactionManager);
                jobRepositoryFactoryBean.afterPropertiesSet();
                return jobRepositoryFactoryBean.getObject();
            }
        };
    }

    @Bean
    @JobScope
    public ItemDecompressStep itemDecompressStep(){
        return new ItemDecompressStep();
    }
    
    @Bean
    @JobScope
    public ItemReaderStep itemReaderStep(){
        return new ItemReaderStep();
    }

    @Bean
    @JobScope
    public ItemProcessorStep itemProcessorStep(){
        return new ItemProcessorStep();
    }


    @Bean
    @JobScope
    public ItemWriterStep itemWriterStep(){
        return new ItemWriterStep();
    }

    @Bean
    public Step decompressFileStep(){
        return stepBuilderFactory.get("itemDecompressStep")
                                 .tasklet(itemDecompressStep())
                                 .build();
    }
    @Bean
    public Step readPersonStep() {
        return stepBuilderFactory.get("readPersonStep")
                .tasklet(itemReaderStep())
                .build();
    }

    @Bean
    public Step processPersonStep(){
        return stepBuilderFactory.get("processPerson")
                .tasklet(itemProcessorStep())
                .build();
    }

    @Bean
    public Step writePersonStep() {
        return stepBuilderFactory.get("writePersonStep")
                .tasklet(itemWriterStep())
                .build();
    }

    @Bean
    public Job readCSVJob() {
        return jobBuilderFactory.get("readCSVJob")
                .start(decompressFileStep())
                .next(readPersonStep())
                .next(processPersonStep())
                .next(writePersonStep())
                .build();
    }

}
