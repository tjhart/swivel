package com.tjh.swivel.controller;

import com.tjh.swivel.model.MatcherFactory;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class StubFactoryTest {

    public static final URI LOCAL_URI = URI.create("some/url");
    public static final Map<String,Object> STUB_DESCRIPTION = Collections.emptyMap();

    @Test
    public void createStubForDefersToMatcherFactory(){
        StubFactory stubFactory = new StubFactory();
        MatcherFactory mockMatcherFactory = mock(MatcherFactory.class);

        stubFactory.setMatcherFactory(mockMatcherFactory);

        stubFactory.createStubFor(LOCAL_URI, STUB_DESCRIPTION);
    }

}
