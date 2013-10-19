package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import vanderbilt.util.Maps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelConfigurerTest {

    public static final String SWIVEL_URL = "http://localhost:8080/swivel-server";
    public static final URI SOME_URI = URI.create("some/path");
    public static final int STUB_ID = 12345;
    public static final String EXPECTED_RESPONSE = new JSONObject(Maps.asMap("id", STUB_ID)).toString();
    public static final String SOME_PATH = "some/path";
    private SwivelConfigurer swivelConfigurer;
    private When mockWhen;
    private SwivelConfigurer swivelConfigurerSpy;
    private Stub mockStub;
    private HttpResponse mockHttpResponse;
    private HttpUriRequest mockHttpRequest;
    private HttpClient mockHttpClient;

    @Before
    public void setUp() throws Exception {
        mockWhen = mock(When.class);
        mockHttpClient = mock(HttpClient.class);
        mockStub = mock(Stub.class);
        mockHttpResponse = mock(HttpResponse.class);
        mockHttpRequest = mock(HttpUriRequest.class);

        swivelConfigurer = new SwivelConfigurer(SWIVEL_URL);

        swivelConfigurerSpy = spy(swivelConfigurer);

        doReturn(mockHttpClient)
                .when(swivelConfigurerSpy)
                .getClient();
        when(mockStub.toRequest(any(URL.class)))
                .thenReturn(mockHttpRequest);
        when(mockWhen.getUri())
                .thenReturn(SOME_URI);
        when(mockHttpClient.execute(any(HttpUriRequest.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity())
                .thenReturn(new StringEntity(EXPECTED_RESPONSE));
    }

    @Test
    public void constructorTakesUrl() throws MalformedURLException {
        assertThat(swivelConfigurer.swivelURL, equalTo(new URL(SWIVEL_URL)));
    }

    @Test
    public void whenReturnsNewStubConfiguration() {
        StubConfigurer stubConfigurer = swivelConfigurer.when(mockWhen);

        assertThat(stubConfigurer, equalTo(new StubConfigurer(swivelConfigurer, mockWhen)));
    }

    @Test
    public void configureGetsClient() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        verify(swivelConfigurerSpy).getClient();
    }

    @Test
    public void configureExecutesRequest() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        verify(mockStub).toRequest(new URL(SWIVEL_URL));
        verify(mockHttpClient).execute(mockHttpRequest);
    }

    @Test
    public void configureGetsResponseEntity() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        verify(mockHttpResponse).getEntity();
    }

    @Test
    public void configureReturnsStubID() throws IOException {
        assertThat(swivelConfigurerSpy.configure(mockStub), equalTo(STUB_ID));
    }

    @Test
    public void shuntReturnsShuntConfigurer() throws URISyntaxException {
        ShuntConfigurer shuntConfigurer = swivelConfigurer.shunt(SOME_PATH);

        assertThat(shuntConfigurer, equalTo(new ShuntConfigurer(swivelConfigurer, SOME_PATH)));
    }

    @Test
    public void deleteStubExecutesExpectedURL() throws IOException {
        swivelConfigurerSpy.deleteStub(SOME_URI, STUB_ID);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(mockHttpClient).execute(captor.capture());

        HttpUriRequest value = captor.getValue();

        assertThat(value, instanceOf(HttpDelete.class));
        assertThat(value.getURI(),
                equalTo(URI.create(SWIVEL_URL + "/rest/config/stub/" + SOME_URI.getPath() + "?id=" + STUB_ID)));
    }

    @Test
    public void deletePathExecutesExpectedURL() throws IOException {
        swivelConfigurerSpy.deletePath(SOME_URI);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(mockHttpClient).execute(captor.capture());

        HttpUriRequest value = captor.getValue();

        assertThat(value, instanceOf(HttpDelete.class));
        assertThat(value.getURI(),
                equalTo(URI.create(SWIVEL_URL + "/rest/config/path/" + SOME_URI.getPath())));
    }

    @Test
    public void resetExecutesExpectedURL() throws IOException {
        swivelConfigurerSpy.reset();

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(mockHttpClient).execute(captor.capture());

        HttpUriRequest value = captor.getValue();

        assertThat(value, instanceOf(HttpDelete.class));
        assertThat(value.getURI(), equalTo(URI.create(SWIVEL_URL + "/rest/config")));

    }
}
