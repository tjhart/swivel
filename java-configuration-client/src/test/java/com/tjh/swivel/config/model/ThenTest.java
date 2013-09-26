package com.tjh.swivel.config.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThenTest {

    public static final HttpResponseCode RESPONSE_CODE = HttpResponseCode.OK;
    public static final String CONTENT = "content";
    public static final String CONTENT_TYPE = "application/xml";
    public static final String SCRIPT = "(function(){})()";
    private Then then;

    @Before
    public void setUp() throws Exception {
        then = new Then(RESPONSE_CODE);
    }

    @Test
    public void constructionCapturesResponseCode(){
        assertThat(then.getResponseCode(), equalTo(RESPONSE_CODE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionThrowsOnNull(){
        new Then((HttpResponseCode)null);
    }

    @Test
    public void withContentSetsContent(){
        assertThat(then.withContent(CONTENT).getContent(), equalTo(CONTENT));
    }

    @Test
    public void asSetsContentType(){
        assertThat(then.as(CONTENT_TYPE).getContentType(), equalTo(CONTENT_TYPE));
    }

    @Test
    public void constructionWithStringCapturesScript(){
        assertThat(new Then(SCRIPT).getScript(), equalTo(SCRIPT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionWithScriptThrowsOnNull(){
        new Then((String)null);
    }
}
