package com.tjh.swivel.controller;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.io.File;
import java.io.IOException;

public class SwivelClosedListener extends SwivelListener implements ApplicationListener<ContextClosedEvent> {
    private static final Logger LOGGER = Logger.getLogger(SwivelClosedListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent ignored) {
        try {
            File saveFile = getSaveFile();
            LOGGER.debug("Saving configuration to " + saveFile.getPath());
            objectMapper.writeValue(saveFile, configuration.toMap());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
