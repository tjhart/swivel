package com.tjh.swivel.controller;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkerResourceTest {

    public static final String LOCAL_PATH = "local/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    private WorkerResource workerResource;
    private RequestRouter mockRouter;
    private HttpUriRequestFactory mockFactory;
    private HttpServletRequest mockRequest;
    private HttpUriRequest mockUriRequest;

    @Before
    public void setUp() throws Exception {
        workerResource = new WorkerResource();
        mockRouter = mock(RequestRouter.class);
        mockFactory = mock(HttpUriRequestFactory.class);
        mockRequest = mock(HttpServletRequest.class);
        mockUriRequest = mock(HttpUriRequest.class);

        workerResource.setRouter(mockRouter);
        workerResource.setRequestFactory(mockFactory);

        when(mockFactory.createGetRequest(any(URI.class), any(HttpServletRequest.class))).thenReturn(mockUriRequest);
    }

    @Test
    public void getDelegatesToRequestFactory() throws URISyntaxException {
        workerResource.get(LOCAL_PATH, mockRequest);

        verify(mockFactory).createGetRequest(LOCAL_URI, mockRequest);
    }

    @Test
    public void getDelegatesToRouter() throws URISyntaxException {
        workerResource.get(LOCAL_PATH, mockRequest);

        verify(mockRouter).work(mockUriRequest);
    }
}
