package com.tjh.swivel.controller;

import com.tjh.swivel.model.MatcherFactory;
import com.tjh.swivel.model.StubRequestHandler;

import java.net.URI;
import java.util.Map;

public class StubFactory {
    private MatcherFactory matcherFactory;

    public StubRequestHandler createStubFor(URI localURI, Map<String, Object> stubDescription) {
        return null;
    }

    public void setMatcherFactory(MatcherFactory matcherFactory) { this.matcherFactory = matcherFactory; }
}
