package com.tjh.swivel.controller;

import com.tjh.swivel.model.ShuntRequestHandler;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestRouterTest {

    public static final URI LOCAL_URI = URI.create("some/path");
    public static final URI DEEP_URI = URI.create("some/path/deep");
    public static final int STUB_HANDLER_ID = 456;
    private ShuntRequestHandler mockRequestHandler;
    private RequestRouter requestRouter;
    private HttpRequestBase mockRequestBase;
    private StubRequestHandler mockStubHandler;

    @Before
    public void setUp() throws Exception {
        requestRouter = new RequestRouter();
        mockRequestBase = mock(HttpRequestBase.class);
        mockRequestHandler = mock(ShuntRequestHandler.class);
        mockStubHandler = mock(StubRequestHandler.class);

        when(mockRequestBase.getURI()).thenReturn(LOCAL_URI);
        when(mockStubHandler.getId()).thenReturn(STUB_HANDLER_ID);
        when(mockStubHandler.matches(any(HttpUriRequest.class))).thenReturn(true);
    }

    @Test
    public void setShuntPutsRequestHandlerAtExpectedPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        assertThat(requestRouter.shuntPaths.get(LOCAL_URI.getPath()), equalTo(mockRequestHandler));
    }

    @Test
    public void deleteShuntRemovesHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        requestRouter.deleteShunt(LOCAL_URI);

        assertThat(requestRouter.shuntPaths.keySet().isEmpty(), is(true));
    }

    @Test
    public void shuntDelegatesToShuntRequestHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);

        requestRouter.shunt(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test
    public void shuntFindsHandlerOnShallowerPath() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);

        requestRouter.shunt(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), any(URI.class), any(HttpClient.class));
    }

    @Test(expected = RuntimeException.class)
    public void shuntThrowsOnUnknownURI() {
        requestRouter.shunt(mockRequestBase);
    }

    @Test
    public void shuntRemovesMatchedPathFromURISentToHandler() {
        requestRouter.setShunt(LOCAL_URI, mockRequestHandler);
        when(mockRequestBase.getURI()).thenReturn(DEEP_URI);

        requestRouter.shunt(mockRequestBase);

        verify(mockRequestHandler).handle(eq(mockRequestBase), eq(LOCAL_URI), any(HttpClient.class));
    }

    @Test
    public void addStubPutsStubInListAtPath() {
        requestRouter.addStub(LOCAL_URI, mockStubHandler);

        assertThat(requestRouter.stubPaths.get(LOCAL_URI.getPath()).get(0), sameInstance(mockStubHandler));
    }

    @Test
    public void removeStubRemovesStubInListAtPath() {
        requestRouter.addStub(LOCAL_URI, mockStubHandler);

        requestRouter.removeStub(LOCAL_URI, STUB_HANDLER_ID);

        assertThat(requestRouter.stubPaths.get(LOCAL_URI.getPath()), not(hasItem(mockStubHandler)));
    }

    @Test
    public void workDelegatesToStub() {
        RequestRouter requestRouterSpy = spy(requestRouter);

        doReturn(null)
                .when(requestRouterSpy)
                .shunt(mockRequestBase);

        requestRouterSpy.work(mockRequestBase);

        verify(requestRouterSpy).stub(mockRequestBase);
    }

    @Test
    public void workDelegatesToShuntIfStubReturnsNull() {
        RequestRouter requestRouterSpy = spy(requestRouter);

        doReturn(null)
                .when(requestRouterSpy)
                .stub(mockRequestBase);
        doReturn(null)
                .when(requestRouterSpy)
                .shunt(mockRequestBase);

        requestRouterSpy.work(mockRequestBase);

        verify(requestRouterSpy).shunt(mockRequestBase);
    }

    @Test
    public void workDoesNotShuntIfStubReturns() {
        RequestRouter requestRouterSpy = spy(requestRouter);

        HttpResponse mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse)
                .when(requestRouterSpy)
                .stub(mockRequestBase);

        requestRouterSpy.work(mockRequestBase);

        verify(requestRouterSpy, never()).shunt(mockRequestBase);
    }

    @Test
    public void stubDelegatesToStubRequestHandler() {
        requestRouter.addStub(LOCAL_URI, mockStubHandler);

        requestRouter.stub(mockRequestBase);

        verify(mockStubHandler).matches(mockRequestBase);
    }
}
