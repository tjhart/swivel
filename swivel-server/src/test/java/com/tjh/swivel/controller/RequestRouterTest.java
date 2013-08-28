package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class RequestRouterTest {

    public static final URI LOCAL_PATH = URI.create("some/path");

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {
        ShuntRequestHandler mockRequestHandler = mock(ShuntRequestHandler.class);
        RequestRouter requestRouter = new RequestRouter();

        requestRouter.setShunt(LOCAL_PATH, mockRequestHandler);

        assertThat((ShuntRequestHandler) Maps.valueFor(requestRouter.shuntPaths, LOCAL_PATH.getPath().split("/")),
                equalTo(mockRequestHandler));
    }
}
