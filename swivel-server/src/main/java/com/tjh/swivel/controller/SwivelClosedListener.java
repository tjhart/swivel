package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;
import java.io.IOException;

public class SwivelClosedListener implements ApplicationListener<ContextClosedEvent> {
    public static final String FILE_KEY = "SWIVEL_CONFIG_FILE";
    private static final Logger LOGGER = Logger.getLogger(SwivelClosedListener.class);
    public static final String DEFAULT_FILE_NAME = "swivelConfig.json";

    private Configuration configuration;
    private ConfigurableEnvironment env = new StandardEnvironment();
    private ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ContextClosedEvent ignored) {
        LOGGER.debug("SwivelClosedListener.onApplicationEvent");
        try {
            objectMapper.writeValue(getSaveFile(), configuration.toMap());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public File getSaveFile() {
        String path = env.getProperty(FILE_KEY);
        if (path == null) {
            path = env.getProperty("java.io.tmpdir") + DEFAULT_FILE_NAME;
        }
        return new File(path);
    }

    //useful for testing
    void setEnv(ConfigurableEnvironment env) { this.env = env; }

    public void setConfiguration(Configuration configuration) { this.configuration = configuration; }

    public void setObjectMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }
}
