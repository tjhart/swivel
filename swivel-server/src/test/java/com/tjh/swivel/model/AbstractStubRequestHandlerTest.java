package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(testSubRequestHandler.toMap()).isEqualTo(expected);
    }

    class TestSubRequestHandler extends AbstractStubRequestHandler {

        public TestSubRequestHandler(Map<String, Object> stubDescription) {
            super(stubDescription, mockResponseFactory);
        }

        @Override
        public HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client) {
            return null;
        }
    }
}
