package com.tjh.swivel.config;

import java.io.IOException;

public interface ConfigurationElement {
    /**
     * Configure the element represented by the builder on the server
     *
     * @return The ID of the configuration element
     * @throws IOException
     */
    int configure() throws IOException;
}
