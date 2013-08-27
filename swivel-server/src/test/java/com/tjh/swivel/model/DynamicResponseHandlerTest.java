package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.tjh.swivel.utils.ScriptWrapper;

import javax.script.Bindings;
import javax.script.ScriptException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DynamicResponseHandlerTest {

    public static final String SOURCE_SCRIPT =
            "(function(){\n" +
                    "    return responseFactory.createResponse(200, 'OK');" +
                    "})();";
    private DynamicResponseHandler dynamicResponseHandler;
    private HttpUriRequest mockRequest;

    @Before
    public void setUp() throws Exception {
        dynamicResponseHandler = new DynamicResponseHandler(SOURCE_SCRIPT);
        mockRequest = mock(HttpUriRequest.class);
    }

    @Test
    public void handleDefersToScriptWrapper() throws ScriptException {
        dynamicResponseHandler.scriptWrapper = mock(ScriptWrapper.class);

        dynamicResponseHandler.handle(mockRequest);

        ArgumentCaptor<Bindings> bindingsCaptor = ArgumentCaptor.forClass(Bindings.class);

        verify(dynamicResponseHandler.scriptWrapper).evalWith(bindingsCaptor.capture());
        Bindings value = bindingsCaptor.getValue();
        assertThat((HttpUriRequest) value.get("request"), equalTo(mockRequest));
        assertThat(value.get("responseFactory"), instanceOf(ResponseFactory.class));
    }
}
