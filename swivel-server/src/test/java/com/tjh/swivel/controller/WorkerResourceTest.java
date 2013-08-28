package com.tjh.swivel.controller;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WorkerResourceTest {

    public static final String LOCAL_PATH = "local/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);

    @Test
    public void getDelegatesToRequestFactory() throws URISyntaxException {
        WorkerResource workerResource = new WorkerResource();
        RequestRouter mockRouter = mock(RequestRouter.class);
        HttpUriRequestFactory mockFactory = mock(HttpUriRequestFactory.class);

        workerResource.setRouter(mockRouter);
        workerResource.setRequestFactory(mockFactory);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        workerResource.get(LOCAL_PATH, mockRequest);

        verify(mockFactory).createGetRequest(LOCAL_URI, mockRequest);
    }
}
