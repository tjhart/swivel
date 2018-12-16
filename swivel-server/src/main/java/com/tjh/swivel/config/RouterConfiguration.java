package com.tjh.swivel.config;

import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.model.ResponseFactory;
import com.tjh.swivel.model.StubRequestHandlerFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfiguration {

    @Bean
    public PoolingHttpClientConnectionManager clientConnectionManager(){
        return new PoolingHttpClientConnectionManager();
    }

    @Bean
    public ResponseFactory responseFactory(){
        return new ResponseFactory();
    }

    @Bean
    public StubRequestHandlerFactory stubRequestHandlerFactory(){
        return new StubRequestHandlerFactory(responseFactory());
    }

    @Bean
    public com.tjh.swivel.model.Configuration configuration(){
        return new com.tjh.swivel.model.Configuration(stubRequestHandlerFactory());
    }

    @Bean
    public RequestRouter requestRouter(){
        RequestRouter result = new RequestRouter();
        result.setConfiguration(configuration());
        result.setClientConnectionManager(clientConnectionManager());
        result.setResponseFactory(responseFactory());
        return result;
    }
}
