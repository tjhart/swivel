package com.tjh.swivel.controller;

import com.tjh.swivel.model.Configuration;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import java.io.File;

public class SwivelListener {
    public static final String FILE_KEY = "SWIVEL_CONFIG_FILE";
    public static final String DEFAULT_FILE_NAME = "swivelConfig.json";
    protected Configuration configuration;
    protected ObjectMapper objectMapper;
    private ConfigurableEnvironment env = new StandardEnvironment();

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
