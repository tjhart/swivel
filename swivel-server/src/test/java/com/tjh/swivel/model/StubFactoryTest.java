package com.tjh.swivel.model;

import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StubFactoryTest {

    public static final Map<String, String> WHEN_MAP = Collections.emptyMap();
    private static final Map<String, Object> THEN_MAP = Collections.emptyMap();
    public static final String DESCRIPTION = "description";
    public static final Map<String, Object> STATIC_DESCRIPTION = Maps.<String, Object>asConstantMap(
            StubFactory.DESCRIPTION_KEY, DESCRIPTION,
            StubFactory.WHEN_KEY, WHEN_MAP,
            StubFactory.THEN_KEY, THEN_MAP);
    private StubFactory stubFactory;
    private ResponseFactory mockResponseFactory;

    @Before
    public void setUp() throws Exception {
        stubFactory = new StubFactory();
        mockResponseFactory = mock(ResponseFactory.class);

        stubFactory.setResponseFactory(mockResponseFactory);
    }

    @Test
    public void createStubForDefersToResponseFactoryForStaticDescriptions() throws ScriptException {
        stubFactory.createStubFor(STATIC_DESCRIPTION);

        verify(mockResponseFactory).createResponse(THEN_MAP);
    }

    @Test
    public void createStubForCreatesStaticStubRequestHandlerForStaticDescriptions() throws ScriptException {
        assertThat(stubFactory.createStubFor(STATIC_DESCRIPTION),
                instanceOf(StaticStubRequestHandler.class));
    }

    @Test
    public void createStubForCreatesDynamicStubResponseHandlerForDynamicDescriptions() throws ScriptException {
        StubRequestHandler stubRequestHandler = stubFactory.createStubFor(Maps.<String, Object>asMap(
                StubFactory.DESCRIPTION_KEY, DESCRIPTION,
                StubFactory.WHEN_KEY, WHEN_MAP,
                StubFactory.THEN_KEY, Maps.asMap(StubFactory.SCRIPT_KEY, "(function(){})();")
        ));

        assertThat(stubRequestHandler, instanceOf(DynamicStubRequestHandler.class));
    }
}
