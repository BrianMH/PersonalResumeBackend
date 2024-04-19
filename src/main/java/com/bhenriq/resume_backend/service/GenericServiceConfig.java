package com.bhenriq.resume_backend.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides some of the base objects that will be necessary throughout the service layer
 */
@Configuration
public class GenericServiceConfig {
    /**
     * Returns a model mapper with the default configuration set
     * @return a model mapper object
     */
    @Bean
    public ModelMapper converter() {
        ModelMapper relMapper = new ModelMapper();
        relMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        return relMapper;
    }
}
