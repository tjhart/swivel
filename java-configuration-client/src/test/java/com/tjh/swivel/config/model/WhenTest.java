package com.tjh.swivel.config.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

public class WhenTest {

    public static final String CONTENT = "data";
    public static final String CONTENT_TYPE = "application/xml";
    public static final String REMOTE_ADDRESS = "127.0.0.1";
    public static final String SCRIPT = "(function(){})();";
    private When when;

    @Before
    public void setUp() throws Exception {
        when = new When(HttpMethod.GET);
    }

    @Test
    public void constructionTakesMethod(){
        assertThat(when.getMethod(), sameInstance(HttpMethod.GET));
    }

    @Test
    public void withContentCapturesContent(){
        assertThat(when.withContent(CONTENT).getContent(), equalTo(CONTENT));
    }

    @Test
    public void asSetsContentType(){
        assertThat(when.as(CONTENT_TYPE).getContentType(), equalTo(CONTENT_TYPE));
    }

    @Test
    public void fromSetsRemoteAddr(){
        assertThat(when.from(REMOTE_ADDRESS).getRemoteAddress(), equalTo(REMOTE_ADDRESS));
    }

    @Test
    public void matchesSetsScript(){
        assertThat(when.matches(SCRIPT).getScript(), equalTo(SCRIPT));
    }
}
