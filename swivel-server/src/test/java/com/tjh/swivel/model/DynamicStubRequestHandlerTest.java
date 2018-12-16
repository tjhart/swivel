package com.tjh.swivel.model;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class DynamicStubRequestHandlerTest {

    public static final String SOURCE_SCRIPT =
            "(function(){\n" +
                    "    return responseFactory.createResponse(200, 'OK');" +
                    "})();";
    public static final String DESCRIPTION = "description";

    private DynamicStubRequestHandler dynamicResponseHandler;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpUriRequest mockRequest;
    @Mock
    private ScriptWrapper mockScriptWrapper;
    @Mock
    private HttpClient mockHttpClient;
    @Mock
    private ResponseFactory mockResponseFactory;

    @Before
    public void setUp() throws ScriptException {
        dynamicResponseHandler = new DynamicStubRequestHandler(Map.of(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, Map.of(WhenMatcher.METHOD_KEY, "GET"),

                AbstractStubRequestHandler.THEN_KEY,
                Map.of(AbstractStubRequestHandler.SCRIPT_KEY, SOURCE_SCRIPT)
        ), mockResponseFactory);
    }

    @Test
    public void handleDefersToScriptWrapper() throws ScriptException {
        //REDTAG:TJH - fix this
        dynamicResponseHandler.scriptWrapper = mockScriptWrapper;

        dynamicResponseHandler.handle(mockRequest, URI.create("matched/uri"), mockHttpClient);

        ArgumentCaptor<Bindings> bindingsCaptor = ArgumentCaptor.forClass(Bindings.class);

        verify(dynamicResponseHandler.scriptWrapper).evalWith(bindingsCaptor.capture());
        Bindings value = bindingsCaptor.getValue();
        assertThat(value.get("request")).isEqualTo(mockRequest);
        assertThat(value.get("responseFactory")).isEqualTo(mockResponseFactory);
    }
}
