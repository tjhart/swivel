package com.tjh.swivel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.swivel.controller.ConfigurationResource;
import com.tjh.swivel.controller.ConfigureShuntResource;
import com.tjh.swivel.controller.ConfigureStubResource;
import com.tjh.swivel.controller.HttpUriRequestFactory;
import com.tjh.swivel.controller.JerseyResponseFactory;
import com.tjh.swivel.controller.ProxyResource;
import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.controller.StubFileStorage;
import com.tjh.swivel.controller.SwivelClosedListener;
import com.tjh.swivel.controller.SwivelRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

@Import(RouterConfiguration.class)
@Configuration
public class ServerConfiguration {

    @Value("file:${com.tjh.swivel.storage.path:${java.io.tmpdir}}/swivelStorage")
    private org.springframework.core.io.Resource stubFileDir;
    @Value("file:${com.tjh.swivel.config.file:${java.io.tmpdir}}/swivelConfig.json")
    private org.springframework.core.io.Resource saveFile;

    @Resource
    private com.tjh.swivel.model.Configuration configuration;
    @Resource
    private RequestRouter requestRouter;

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer result = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        String tmpDir = System.getProperty("java.io.tmpdir");
        properties.setProperty("com.tjh.swivel.storage.path", tmpDir);
        properties.setProperty("com.tjh.swivel.config.file", tmpDir);
        result.setProperties(properties);
        return result;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public ConfigurationResource configurationResource(){
        ConfigurationResource result = new ConfigurationResource();
        result.setConfiguration(configuration);
        result.setObjectMapper(objectMapper());
        return result;
    }

    @Bean
    public ConfigureShuntResource configureShuntResource(){
        ConfigureShuntResource result = new ConfigureShuntResource();
        result.setConfiguration(configuration);
        return result;
    }

    @Bean
    public StubFileStorage stubFileStorage() throws IOException {
        StubFileStorage result = new StubFileStorage();
        result.setStubFileDir(stubFileDir.getFile());
        return result;
    }

    @Bean
    public ConfigureStubResource configureStubResource() throws IOException {
        ConfigureStubResource result = new ConfigureStubResource();
        result.setConfiguration(configuration);
        result.setObjectMapper(objectMapper());
        result.setStubFileStorage(stubFileStorage());
        return result;
    }

    @Bean
    public HttpUriRequestFactory requestFactory(){
        return new HttpUriRequestFactory();
    }

    @Bean
    public JerseyResponseFactory jerseyResponseFactory(){
        return new JerseyResponseFactory();
    }

    @Bean
    public ProxyResource proxyResource(){
        ProxyResource result = new ProxyResource();
        result.setRouter(requestRouter);
        result.setRequestFactory(requestFactory());
        result.setResponseFactory(jerseyResponseFactory());
        return result;
    }

    @Bean
    public SwivelClosedListener swivelClosedListener() throws IOException {
        SwivelClosedListener result = new SwivelClosedListener();
        result.setConfiguration(configuration);
        result.setObjectMapper(objectMapper());
        result.setSaveFile(saveFile.getFile());
        return result;
    }

    @Bean
    public SwivelRefreshedListener swivelRefreshedListener() throws IOException {
        SwivelRefreshedListener result = new SwivelRefreshedListener();
        result.setConfiguration(configuration);
        result.setObjectMapper(objectMapper());
        result.setSaveFile(saveFile.getFile());
        return result;
    }
}
