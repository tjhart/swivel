package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.MimeTypeUtils;

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
    public static final Map<String, String> WHEN_MAP = Map.of(WhenMatcher.METHOD_KEY, "GET");
    private StaticStubRequestHandler staticResponseHandler;
    private File storageFile;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void before() throws IOException {
        staticResponseHandler = new StaticStubRequestHandler(Map.of(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,

                AbstractStubRequestHandler.THEN_KEY,
                Map.of(AbstractStubRequestHandler.STATUS_CODE_KEY, 200)
        ));

        storageFile = temporaryFolder.newFile();
    }

    @Test
    public void constructionSetsResponseFileToNullWithoutStoragePath() {
        assertThat(staticResponseHandler.responseFile, nullValue());
    }

    @Test(expected = RuntimeException.class)
    public void constructionThrowsIfResponseFileNotFound() {
        new StaticStubRequestHandler(Map.of(AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY, Map.of(AbstractStubRequestHandler.STATUS_CODE_KEY, 200,
                        AbstractStubRequestHandler.STORAGE_PATH_KEY, "foo/bar")));
    }

    @Test
    public void constructionSetsResponseFileWithStoragePath() {
        StaticStubRequestHandler staticStubRequestHandler = new StaticStubRequestHandler(
                Map.of(AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                        AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                        AbstractStubRequestHandler.THEN_KEY, Map.of(
                                AbstractStubRequestHandler.STATUS_CODE_KEY,
                                200,
                                AbstractStubRequestHandler.STORAGE_PATH_KEY,
                                storageFile.getPath(),
                                AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY,
                                MimeTypeUtils.APPLICATION_JSON.getType())));

        assertThat(staticStubRequestHandler.responseFile, equalTo(storageFile));
    }

    @Test
    public void handleReturnsResponse() {
        assertThat(staticResponseHandler.handle(mock(HttpUriRequest.class), null, null), notNullValue());
    }
}
