package com.tjh.swivel.controller;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class StubFileStorage {
    private static Logger LOGGER = Logger.getLogger(StubFileStorage.class);
    private File stubFileDir;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected File createFile(InputStream formFile) throws IOException {
        File result = new File(stubFileDir, UUID.randomUUID().toString());
        result.setWritable(true);
        result.createNewFile();
        LOGGER.debug("Creating stub storage file " + result.getPath());

        FileOutputStream destination = new FileOutputStream(result);
        try {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = formFile.read(buffer)) != -1) {
                destination.write(buffer, 0, read);
            }
            return result;
        } finally {
            destination.close();
        }
    }

    public void setStubFileDir(File stubFileDir) {
        this.stubFileDir = stubFileDir;
        if (!stubFileDir.exists()) {
            if (!stubFileDir.mkdir()) {
                throw new RuntimeException("Can't create directory " + stubFileDir.getPath());
            }
        }
    }
}
