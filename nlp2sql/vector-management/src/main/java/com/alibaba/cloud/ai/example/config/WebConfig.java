package com.alibaba.cloud.ai.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.SpringDataWebSettings;

@Configuration
public class WebConfig {

    @Bean
    @Primary
    public SpringDataWebSettings springDataWebSettings() {
        return new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO);
    }
}
