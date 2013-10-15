package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationTest {
    public static final URI LOCAL_URI = URI.create("some/path");
    public static final int STUB_HANDLER_ID = 456;
    private ShuntRequestHandler mockRequestHandler;
    private Configuration configuration;
    private StubRequestHandler mockStubHandler;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();

        mockRequestHandler = mock(ShuntRequestHandler.class);
        mockStubHandler = mock(StubRequestHandler.class);

        when(mockStubHandler.getId()).thenReturn(STUB_HANDLER_ID);
        when(mockStubHandler.matches(any(HttpUriRequest.class))).thenReturn(true);
    }

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {
        configuration.setShunt(LOCAL_URI, mockRequestHandler);

        assertThat((ShuntRequestHandler) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.SHUNT_NODE),
                equalTo(mockRequestHandler));
    }

    @Test
    public void deleteShuntRemovesHandler() {
        configuration.setShunt(LOCAL_URI, mockRequestHandler);

        configuration.deleteShunt(LOCAL_URI);

        assertThat(configuration.uriHandlers.get(LOCAL_URI.toString()).containsKey(Configuration.SHUNT_NODE),
                is(false));
    }

    @Test
    public void addStubPutsStubInListAtPath() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        assertThat(((List<StubRequestHandler>) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.STUB_NODE)).get(0),
                sameInstance(mockStubHandler));
    }

    @Test
    public void removeStubRemovesStubInListAtPath() {
        configuration.addStub(LOCAL_URI, mockStubHandler);

        configuration.removeStub(LOCAL_URI, STUB_HANDLER_ID);

        assertThat((List<StubRequestHandler>) configuration.uriHandlers
                .get(LOCAL_URI.getPath())
                .get(Configuration.STUB_NODE),
                not(hasItem(mockStubHandler)));
    }
}
