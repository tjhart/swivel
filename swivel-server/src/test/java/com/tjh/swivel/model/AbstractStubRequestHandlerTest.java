package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.script.ScriptException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class AbstractStubRequestHandlerTest {
    public static final String DESCRIPTION = "description";
    public static final Map<String, String> WHEN_MAP = Map.of(WhenMatcher.METHOD_KEY, "GET");
    private static final Map<String, Object> THEN_MAP = Collections.emptyMap();
    public static final Map<String, Object> STATIC_DESCRIPTION = Map.of(
            AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
            AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
            AbstractStubRequestHandler.THEN_KEY, THEN_MAP);

    private TestSubRequestHandler testSubRequestHandler;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ResponseFactory mockResponseFactory;
    @Mock
    private WhenMatcher mockMatcher;
    @Mock
    private HttpUriRequest mockRequest;

    @Before
    public void setUp() {
        testSubRequestHandler = new TestSubRequestHandler(STATIC_DESCRIPTION);

        AbstractStubRequestHandler.setResponseFactory(mockResponseFactory);
    }

    @AfterClass
    public static void afterClass() {
        AbstractStubRequestHandler.setResponseFactory(new ResponseFactory());
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
        StubRequestHandler stubRequestHandler = AbstractStubRequestHandler.createStubFor(Map.of(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY,
                Map.of(AbstractStubRequestHandler.SCRIPT_KEY, "(function(){})();")
        ));

        assertThat(stubRequestHandler, instanceOf(DynamicStubRequestHandler.class));
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresDescription() {
        new TestSubRequestHandler(Map.of(
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY, Collections.emptyMap())
        );
    }

    @Test
    public void matchesDefersToMatcher() {
        testSubRequestHandler.matcher = mockMatcher;
        testSubRequestHandler.matches(mockRequest);

        verify(mockMatcher).matches(mockRequest);
    }

    @Test
    public void toMapReturnsExpected() {
        Map<String, Object> expected = new HashMap<>(STATIC_DESCRIPTION);
        expected.putAll(Map.of(AbstractStubRequestHandler.ID_KEY, testSubRequestHandler.getId()));
        assertThat(testSubRequestHandler.toMap(), equalTo(expected));
    }

    class TestSubRequestHandler extends AbstractStubRequestHandler {

        public TestSubRequestHandler(Map<String, Object> stubDescription) {
            super(stubDescription);
        }

        @Override
        public HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client) {
            return null;
        }
    }
}
