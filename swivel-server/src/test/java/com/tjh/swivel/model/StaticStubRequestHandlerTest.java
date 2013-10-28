package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class StaticStubRequestHandlerTest {

    public static final String DESCRIPTION = "description";
    public static final Map<String, String> WHEN_MAP = Maps.asMap(WhenMatcher.METHOD_KEY, "GET");
    private StaticStubRequestHandler staticResponseHandler;
    private File storageFile;

    @Before
    public void before() throws IOException {
        staticResponseHandler = new StaticStubRequestHandler(Maps.<String, Object>asMap(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,

                AbstractStubRequestHandler.THEN_KEY,
                Maps.asMap(ResponseFactory.STATUS_CODE_KEY, 200)
        ));

        storageFile = new File(System.getProperty("java.io.tmpdir") + "/testFile.tmp");
        storageFile.createNewFile();
    }

    @After
    public void after(){
        storageFile.delete();
    }

    @Test
    public void constructionSetsResponseFileToNullWithoutStoragePath() {
        assertThat(staticResponseHandler.responseFile, nullValue());
    }

    @Test(expected = RuntimeException.class)
    public void constructionThrowsIfResponseFileNotFound() {
        new StaticStubRequestHandler(Maps.<String, Object>asMap(AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY, Maps.asMap(ResponseFactory.STATUS_CODE_KEY, 200,
                AbstractStubRequestHandler.STORAGE_PATH_KEY, "foo/bar")));
    }

    @Test
    public void constructionSetsResponseFileWithStoragePath(){
        StaticStubRequestHandler staticStubRequestHandler = new StaticStubRequestHandler(
                Maps.<String, Object>asMap(AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                        AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                        AbstractStubRequestHandler.THEN_KEY, Maps.asMap(ResponseFactory.STATUS_CODE_KEY, 200,
                        AbstractStubRequestHandler.STORAGE_PATH_KEY, storageFile.getPath())));

        assertThat(staticStubRequestHandler.responseFile, equalTo(storageFile));
    }

    @Test
    public void handleReturnsResponse() {
        assertThat(staticResponseHandler.handle(mock(HttpUriRequest.class), null, null), notNullValue());
    }
}
