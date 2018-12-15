package com.tjh.swivel.config.model;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class StubTest {

    private static final URL SOME_URL;

    static {
        try {
            SOME_URL = new URL("http://host/path");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String DESCRIPTION = "description";

    private Stub stub;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private When mockWhen;
    @Mock
    private Then mockThen;
    @Mock
    private JSONObject mockJSONObject;
    @Mock
    private File mockFile;

    @Before
    public void setUp() {

        stub = new Stub(DESCRIPTION, mockWhen, mockThen);

        when(mockWhen.toJSON()).thenReturn(mockJSONObject);
        when(mockThen.toJSON()).thenReturn(mockJSONObject);
    }

    @Test
    public void toJSONIncludesWhen() throws JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.WHEN_KEY), notNullValue());
    }

    @Test
    public void toJSONIncludesThen() throws JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.THEN_KEY), notNullValue());
    }

    @Test
    public void toJSONIncludesDescription() throws JSONException {
        assertThat(stub.toJSON().getString(Stub.DESCRIPTION_KEY), equalTo(DESCRIPTION));
    }

    @Test
    public void toRequestCreatesPostRequest() {
        assertThat(stub.toRequest(SOME_URL), instanceOf(HttpPost.class));
    }

    @Test
    public void toRequestIncludesEntity() throws IOException {
        String entity = EntityUtils.toString(((HttpPost) stub.toRequest(SOME_URL))
                .getEntity());

        assertThat(entity, equalTo(stub.toJSON().toString()));
    }

    @Test
    public void toRequestIsApplicationJSON() {
        assertThat(((HttpPost) stub.toRequest(SOME_URL))
                .getEntity()
                .getContentType()
                .getValue(),
                equalTo(ContentType.APPLICATION_JSON.toString()));
    }

    @Test
    public void createEntityCreatesMultiPartIfThenIsFileBased(){
        when(mockThen.getFile()).thenReturn(mockFile);
        when(mockThen.getContentType()).thenReturn("text/plain");

        //MultipartFormEntity isn't accessible
        assertThat(stub.createEntity(), not(instanceOf(StringEntity.class)));
    }
}
