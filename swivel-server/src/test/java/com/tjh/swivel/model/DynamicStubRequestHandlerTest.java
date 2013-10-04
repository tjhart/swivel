package com.tjh.swivel.model;

import com.tjh.swivel.utils.ScriptWrapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DynamicStubRequestHandlerTest {

    public static final String SOURCE_SCRIPT =
            "(function(){\n" +
                    "    return responseFactory.createResponse(200, 'OK');" +
                    "})();";
    public static final String DESCRIPTION = "description";
    private DynamicStubRequestHandler dynamicResponseHandler;
    private HttpUriRequest mockRequest;

    @Before
    public void setUp() throws Exception {
        WhenMatcher mockMatcher = mock(WhenMatcher.class);
        dynamicResponseHandler = new DynamicStubRequestHandler(DESCRIPTION, mockMatcher, SOURCE_SCRIPT);
        mockRequest = mock(HttpUriRequest.class);
    }

    @Test
    public void handleDefersToScriptWrapper() throws ScriptException {
        dynamicResponseHandler.scriptWrapper = mock(ScriptWrapper.class);

        dynamicResponseHandler.handle(mockRequest, URI.create("matched/uri"), mock(HttpClient.class));

        ArgumentCaptor<Bindings> bindingsCaptor = ArgumentCaptor.forClass(Bindings.class);

        verify(dynamicResponseHandler.scriptWrapper).evalWith(bindingsCaptor.capture());
        Bindings value = bindingsCaptor.getValue();
        assertThat((HttpUriRequest) value.get("request"), equalTo(mockRequest));
        assertThat(value.get("responseFactory"), instanceOf(ResponseFactory.class));
    }
}
