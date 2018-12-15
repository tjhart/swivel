package com.tjh.swivel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjh.swivel.model.Configuration;

import java.io.File;

public class SwivelListener {
    protected Configuration configuration;
    protected ObjectMapper objectMapper;
    protected File saveFile;

    public void setConfiguration(Configuration configuration) { this.configuration = configuration; }

    public void setObjectMapper(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    public void setSaveFile(File saveFile) { this.saveFile = saveFile; }
}
