package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import vanderbilt.util.Maps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SwivelConfigurerTest {

    public static final String SWIVEL_URL = "http://localhost:8080/swivel-server-1.0-SNAPSHOT";
    public static final URI SOME_URI = URI.create("some/path");
    public static final JSONObject EXPECTED_JSON = new JSONObject(Maps.asMap("key", "val"));
    public static final int STUB_ID = 12345;
    public static final String EXPECTED_RESPONSE = new JSONObject(Maps.asMap("id", STUB_ID)).toString();
    private SwivelConfigurer swivelConfigurer;
    private When mockWhen;
    private HttpClient mockHttpClient;
    private SwivelConfigurer swivelConfigurerSpy;
    private Stub mockStub;
    private HttpResponse mockHttpResponse;

    @Before
    public void setUp() throws Exception {
        mockWhen = mock(When.class);
        mockHttpClient = mock(HttpClient.class);
        mockStub = mock(Stub.class);
        mockHttpResponse = mock(HttpResponse.class);

        swivelConfigurer = new SwivelConfigurer(SWIVEL_URL);

        swivelConfigurerSpy = spy(swivelConfigurer);

        doReturn(mockHttpClient)
                .when(swivelConfigurerSpy)
                .getClient();
        when(mockWhen.getUri())
                .thenReturn(SOME_URI);
        when(mockStub.getURI())
                .thenReturn(SOME_URI);
        when(mockStub.toJSON())
                .thenReturn(EXPECTED_JSON);
        when(mockHttpClient.execute(any(HttpUriRequest.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity())
                .thenReturn(new StringEntity(EXPECTED_RESPONSE));
    }

    @Test
    public void constructorTakesUrl() throws MalformedURLException {
        assertThat(swivelConfigurer.swivelURI, equalTo(new URL(SWIVEL_URL)));
    }

    @Test
    public void whenReturnsNewStubConfiguration() {
        StubConfigurer stubConfigurer = swivelConfigurer.when(mockWhen);

        assertThat(stubConfigurer, equalTo(new StubConfigurer(swivelConfigurer, mockWhen)));
    }

    @Test
    public void configureStubGetsClient() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        verify(swivelConfigurerSpy).getClient();
    }

    @Test
    public void configureStubExecutesPost() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        verify(mockHttpClient).execute(isA(HttpPost.class));
    }

    @Test
    public void configureStubPostsToExpectedURL() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(mockHttpClient).execute(captor.capture());

        assertThat(captor.getValue().getURI().toString(),
                equalTo(SWIVEL_URL + "/" + SwivelConfigurer.STUB_CONFIG_URI + "/" + SOME_URI));
    }

    @Test
    public void configurePostsAsApplicationJSON() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
        verify(mockHttpClient).execute(captor.capture());

        assertThat(captor
                .getValue()
                .getEntity()
                .getContentType()
                .getValue(),
                equalTo(ContentType.APPLICATION_JSON.toString()));
    }

    @Test
    public void configurePostsExpectedData() throws IOException {
        swivelConfigurerSpy.configure(mockStub);

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);
        verify(mockHttpClient).execute(captor.capture());

        String entity = EntityUtils.toString(captor
                .getValue()
                .getEntity());

        assertThat(entity, equalTo(EXPECTED_JSON.toString()));
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
}
