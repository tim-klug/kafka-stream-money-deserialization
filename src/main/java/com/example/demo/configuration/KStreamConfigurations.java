package com.example.demo.configuration;

import com.example.demo.processor.StreamBindings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.zalando.jackson.datatype.money.MoneyModule;

@Configuration
@EnableBinding(StreamBindings.class)
public class KStreamConfigurations {

  @Bean
  RecordMessageConverter messageConverter(ObjectMapper objectMapper) {
    objectMapper.registerModule(new MoneyModule());
    return new StringJsonMessageConverter(objectMapper);
  }
}
