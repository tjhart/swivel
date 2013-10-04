package com.tjh.swivel.config.model;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.tjh.swivel.config.Swivel.get;
import static com.tjh.swivel.config.Swivel.ok;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Before
    public void setUp() throws URISyntaxException {
        stub = new Stub(DESCRIPTION, get(URI.create("some/uri")), ok());
    }

    @Test
    public void toJSONIncludesWhen() throws URISyntaxException, JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.WHEN_KEY), notNullValue());
    }

    @Test
    public void toJSONIncludesThen() throws JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.THEN_KEY), notNullValue());
    }

    @Test
    public void toJSONIncludesdescription() throws JSONException {
        assertThat(stub.toJSON().getString(Stub.DESCRIPTION_KEY), equalTo(DESCRIPTION));
    }

    @Test
    public void toRequestCreatesPostRequest() throws MalformedURLException {
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
}
