package com.bhenriq.resume_backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Configuration
@EnableAsync
@Slf4j
public class S3BucketAsyncConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // define configuration for the async queue
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);

        // and then set some metadata as necessary
        executor.setThreadNamePrefix("Thread-");
        executor.setRejectedExecutionHandler((runnable, exec) -> log.warn("Thread pool and queue are full."));
        executor.initialize();

        return executor;
    }

    /**
     * Creates the s3Client bean to be used in the program. Since it's the only source of async operations, it is created
     * in here in the async configuration
     * @return an s3Client bean
     */
    @Bean
    public S3Client s3Client() {
        return S3Client
                .builder()
                .region(Region.US_WEST_2)
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }
}
