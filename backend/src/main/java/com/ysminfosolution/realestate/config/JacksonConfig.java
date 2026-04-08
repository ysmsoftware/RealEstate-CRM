package com.ysminfosolution.realestate.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ysminfosolution.realestate.util.StringTrimmerDeserializer;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Module stringTrimmerModule) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        objectMapper.registerModule(stringTrimmerModule);
        return objectMapper;
    }

    @Bean
    public Module stringTrimmerModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringTrimmerDeserializer());
        return module;
    }
}