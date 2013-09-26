package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;

import java.net.URI;

public class SwivelConfigurer {
    protected final String swivelURI;
    private When when;

    public SwivelConfigurer(String swivelURI) {
        this.swivelURI = swivelURI;
    }

    public SwivelConfigurer when(When when) {
        setWhen(when);
        return this;
    }

    public void setWhen(When when) {
        URI uri = when.getUri();
        if (uri == null || uri.getPath().length() == 0) {
            throw new IllegalArgumentException("Swivel stubs must be related to a URI:" + when);
        }
        this.when = when;
    }

    public When getWhen() { return when; }
}
