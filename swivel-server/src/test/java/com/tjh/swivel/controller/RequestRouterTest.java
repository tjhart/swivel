package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class RequestRouterTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    private ShuntRequestHandler mockRequestHandler;
    private RequestRouter requestRouter;

    @Before
    public void setUp() throws Exception {
        requestRouter = new RequestRouter();

        mockRequestHandler = mock(ShuntRequestHandler.class);
    }

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {

        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        assertThat((ShuntRequestHandler) Maps.valueFor(requestRouter.shuntPaths, LOCAL_URI.getPath().split("/")),
                equalTo(mockRequestHandler));
    }

    @Test
    public void deleteShuntRemovesHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        requestRouter.deleteShunt(LOCAL_URI);

        assertThat(requestRouter.shuntPaths.keySet().isEmpty(), is(true));
    }
}
