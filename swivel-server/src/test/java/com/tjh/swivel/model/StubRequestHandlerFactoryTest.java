package com.tjh.swivel.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class StubRequestHandlerFactoryTest {

    public static final String DESCRIPTION = "description";
    public static final Map<String, String> WHEN_MAP = Map.of(WhenMatcher.METHOD_KEY, "GET");
    private static final Map<String, Object> THEN_MAP = Collections.emptyMap();
    public static final Map<String, Object> STATIC_DESCRIPTION = Map.of(
            AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
            AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
            AbstractStubRequestHandler.THEN_KEY, THEN_MAP);

    private StubRequestHandlerFactory factory;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ResponseFactory mockResponseFactory;

    @Before
    public void before(){
        factory = new StubRequestHandlerFactory(mockResponseFactory);
    }

    @Test
    public void createStubForDefersToResponseFactoryForStaticDescriptions() throws ScriptException {
        factory.createStubFor(STATIC_DESCRIPTION);

        verify(mockResponseFactory).createResponse(THEN_MAP);
    }

    @Test
    public void createStubForCreatesStaticStubRequestHandlerForStaticDescriptions() throws ScriptException {

        assertThat(factory.createStubFor(STATIC_DESCRIPTION))
                .isInstanceOf(StaticStubRequestHandler.class);
    }

    @Test
    public void createStubForCreatesDynamicStubResponseHandlerForDynamicDescriptions() throws ScriptException {
        StubRequestHandler stubRequestHandler = factory.createStubFor(Map.of(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY,
                Map.of(AbstractStubRequestHandler.SCRIPT_KEY, "(function(){})();")
        ));

        assertThat(stubRequestHandler).isInstanceOf(DynamicStubRequestHandler.class);
    }
}
