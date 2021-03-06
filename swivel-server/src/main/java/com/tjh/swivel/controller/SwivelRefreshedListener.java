package com.tjh.swivel.controller;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;
import java.util.Map;

public class SwivelRefreshedListener extends SwivelListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = Logger.getLogger(SwivelRefreshedListener.class);

    @SuppressWarnings("unchecked")
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if(saveFile.exists()){
                LOGGER.debug("Reading configuration from " + saveFile.getPath());
                configuration.load(objectMapper.readValue(saveFile, Map.class));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
