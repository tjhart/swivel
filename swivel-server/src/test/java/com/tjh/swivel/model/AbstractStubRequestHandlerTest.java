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

public class AbstractStubRequestHandlerTest {
    public static final Map<String, String> WHEN_MAP = Collections.emptyMap();
    private static final Map<String, Object> THEN_MAP = Collections.emptyMap();
    public static final String DESCRIPTION = "description";
    public static final Map<String, Object> STATIC_DESCRIPTION = Maps.<String, Object>asConstantMap(
            AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
            AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
            AbstractStubRequestHandler.THEN_KEY, THEN_MAP);
    private ResponseFactory mockResponseFactory;

    @Before
    public void setUp() throws Exception {
        mockResponseFactory = mock(ResponseFactory.class);

        AbstractStubRequestHandler.setResponseFactory(mockResponseFactory);
    }

    @Test
    public void createStubForDefersToResponseFactoryForStaticDescriptions() throws ScriptException {
        AbstractStubRequestHandler.createStubFor(STATIC_DESCRIPTION);

        verify(mockResponseFactory).createResponse(THEN_MAP);
    }

    @Test
    public void createStubForCreatesStaticStubRequestHandlerForStaticDescriptions() throws ScriptException {
        assertThat(AbstractStubRequestHandler.createStubFor(STATIC_DESCRIPTION),
                instanceOf(StaticStubRequestHandler.class));
    }

    @Test
    public void createStubForCreatesDynamicStubResponseHandlerForDynamicDescriptions() throws ScriptException {
        StubRequestHandler stubRequestHandler = AbstractStubRequestHandler.createStubFor(Maps.<String, Object>asMap(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY, Maps.asMap(AbstractStubRequestHandler.SCRIPT_KEY, "(function(){})();")
        ));

        assertThat(stubRequestHandler, instanceOf(DynamicStubRequestHandler.class));
    }
}
