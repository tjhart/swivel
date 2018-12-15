package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThenTest {

    public static final HttpResponseCode RESPONSE_CODE = HttpResponseCode.OK;
    public static final String CONTENT = "content";
    public static final String CONTENT_TYPE = "application/xml";
    public static final String SCRIPT = "(function(){})()";
    private Then then;

    @Before
    public void setUp() {
        then = new Then(RESPONSE_CODE);
    }

    @Test
    public void constructionCapturesResponseCode() {
        assertThat(then.getResponseCode(), equalTo(RESPONSE_CODE));
    }

    @Test(expected = NullPointerException.class)
    public void constructionThrowsOnNull() {
        new Then((HttpResponseCode) null);
    }

    @Test
    public void withContentSetsContent() {
        assertThat(then.withContent(CONTENT).getContent(), equalTo(CONTENT));
    }

    @Test
    public void asSetsContentType() {
        assertThat(then.as(CONTENT_TYPE).getContentType(), equalTo(CONTENT_TYPE));
    }

    @Test
    public void constructionWithStringCapturesScript() {
        assertThat(new Then(SCRIPT).getScript(), equalTo(SCRIPT));
    }

    @Test(expected = NullPointerException.class)
    public void constructionWithScriptThrowsOnNull() {
        new Then((String) null);
    }

    @Test
    public void toJSONIncludesScriptIfProvided() throws JSONException {
        assertThat(new Then(SCRIPT)
                .toJSON()
                .getString(Then.SCRIPT_KEY),
                equalTo(SCRIPT));
    }

    @Test
    public void toJSONIncludesResponseCode() throws JSONException {
        JSONObject jsonObject = then.toJSON();

        assertThat(jsonObject.getInt(Then.STATUS_CODE_KEY), equalTo(RESPONSE_CODE.getCode()));
    }

    @Test
    public void toJSONIncludesContent() throws JSONException {
        assertThat(then
                .withContent(CONTENT)
                .toJSON()
                .getString(Then.CONTENT_KEY),
                equalTo(CONTENT));
    }

    @Test
    public void toJSONIncludesContentType() throws JSONException {
        assertThat(then
                .as(CONTENT_TYPE)
                .toJSON()
                .getString(Then.CONTENT_TYPE_KEY),
                equalTo(CONTENT_TYPE));
    }

    @Test
    public void withFileSetsFile() {
        File file = new File("tmpFile");
        assertThat(then.withFile(file).getFile(), equalTo(file));
    }

    @Test
    public void withContentDispositionSetsContentDisposition() {
        String contentDisposition = "contentDisposition";

        assertThat(then.withContentDisposition(contentDisposition).getContentDisposition(),
                equalTo(contentDisposition));
    }

    @Test
    public void toJSONIncludesFilename() throws JSONException {
        String filename = "filename.txt";
        File file = new File(filename);

        assertThat(then
                .withFile(file)
                .toJSON()
                .getString(Then.FILENAME_KEY),
                equalTo(filename));
    }
}
