package com.tjh.swivel.config;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class ShuntTest {

    public static final String URL_STRING = "http://host/path";
    public static final URL SOME_URL;

    static {
        try {
            SOME_URL = new URL(URL_STRING);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private Shunt shunt;

    @Before
    public void setUp() {
        shunt = new Shunt(URI.create("some/path"), SOME_URL);
    }

    @Test
    public void toJSONIncludesRemoteURL() throws JSONException {
        assertThat(shunt.toJSON().getString(Shunt.REMOTE_URL_KEY), equalTo(URL_STRING));
    }

    @Test
    public void toRequestCreatesPut() {
        assertThat(shunt.toRequest(SOME_URL), instanceOf(HttpPut.class));
    }

    @Test
    public void assertThatRequestContainsJSON() throws IOException {
        String entity = EntityUtils.toString(((HttpPut) shunt.toRequest(SOME_URL)).getEntity());

        assertThat(entity, equalTo(shunt.toJSON().toString()));
    }

    @Test
    public void assertThatRequestIsApplicationJSON(){
        assertThat(((HttpPut) shunt.toRequest(SOME_URL))
                .getEntity()
                .getContentType()
                .getValue(),
                equalTo(ContentType.APPLICATION_JSON.toString()));
    }
}
