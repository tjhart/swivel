package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class WhenTest {

    public static final String CONTENT = "data";
    public static final String CONTENT_TYPE = "application/xml";
    public static final String REMOTE_ADDRESS = "127.0.0.1";
    public static final String SCRIPT = "(function(){})();";
    public static final HttpMethod METHOD = HttpMethod.PUT;
    public static final String SOME_URI = "some/path";
    private When when;

    @Before
    public void setUp() throws Exception {
        when = new When(METHOD);
    }

    @Test
    public void constructionTakesMethod() {
        assertThat(when.getMethod(), sameInstance(METHOD));
    }

    @Test
    public void withContentCapturesContent() {
        assertThat(when.withContent(CONTENT).getContent(), equalTo(CONTENT));
    }

    @Test
    public void asSetsContentType() {
        assertThat(when.as(CONTENT_TYPE).getContentType(), equalTo(CONTENT_TYPE));
    }

    @Test
    public void fromSetsRemoteAddr() {
        assertThat(when.from(REMOTE_ADDRESS).getRemoteAddress(), equalTo(REMOTE_ADDRESS));
    }

    @Test
    public void matchesSetsScript() {
        assertThat(when.matches(SCRIPT).getScript(), equalTo(SCRIPT));
    }

    @Test(expected = IllegalStateException.class)
    public void setContentThrowsIfMethodDoesNotAcceptData() {
        new When(HttpMethod.GET).setContent(CONTENT);
    }

    @Test
    public void atSetsURI() throws URISyntaxException {
        assertThat(when.at(SOME_URI).getUri(), equalTo(URI.create(SOME_URI)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void contructionThrowsOnNullMethod() {
        new When(null);
    }

    @Test
    public void toJSONIncludesMethod() throws JSONException {
        assertThat(when
                .toJSON()
                .getString(When.METHOD_KEY),
                equalTo(METHOD.getMethodName()));
    }

    @Test
    public void toJSONIncludesScript() throws JSONException {
        assertThat(when
                .matches(SCRIPT)
                .toJSON()
                .getString(When.SCRIPT_KEY),
                equalTo(SCRIPT));
    }

    @Test
    public void toJSONIncludesContent() throws JSONException {
        assertThat(when
                .withContent(CONTENT)
                .toJSON()
                .getString(When.CONTENT_KEY),
                equalTo(CONTENT));
    }

    @Test
    public void toJSONIncludesContentType() throws JSONException {
        assertThat(when
                .as(CONTENT_TYPE)
                .toJSON()
                .getString(When.CONTENT_TYPE_KEY),
                equalTo(CONTENT_TYPE));
    }

    @Test
    public void toJSONIncludesRemoteAddress() throws JSONException {
        assertThat(when
                .from(REMOTE_ADDRESS)
                .toJSON()
                .getString(When.REMOTE_ADDRESS_KEY),
                equalTo(REMOTE_ADDRESS));
    }
}
