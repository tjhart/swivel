package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

public class StaticStubRequestHandlerTest {

    public static final String DESCRIPTION = "description";
    public static final Map<String, String> WHEN_MAP = Map.of(WhenMatcher.METHOD_KEY, "GET");
    private StaticStubRequestHandler staticResponseHandler;
    private File storageFile;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpUriRequest mockHttpUriRequest;
    @Mock
    private ResponseFactory mockResponseFactory;
    @Mock
    private HttpResponse mockResponse;

    @Before
    public void before() throws IOException {
        when(mockResponseFactory.createResponse(anyMap()))
                .thenReturn(mockResponse);
        staticResponseHandler = new StaticStubRequestHandler(Map.of(
                AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,

                AbstractStubRequestHandler.THEN_KEY,
                Map.of(AbstractStubRequestHandler.STATUS_CODE_KEY, 200)
        ), mockResponseFactory);

        storageFile = temporaryFolder.newFile();
    }

    @Test
    public void constructionSetsResponseFileToNullWithoutStoragePath() {
        assertThat(staticResponseHandler.responseFile).isNull();
    }

    @Test(expected = RuntimeException.class)
    public void constructionThrowsIfResponseFileNotFound() {
        new StaticStubRequestHandler(Map.of(AbstractStubRequestHandler.DESCRIPTION_KEY, DESCRIPTION,
                AbstractStubRequestHandler.WHEN_KEY, WHEN_MAP,
                AbstractStubRequestHandler.THEN_KEY, Map.of(AbstractStubRequestHandler.STATUS_CODE_KEY, 200,
                        AbstractStubRequestHandler.STORAGE_PATH_KEY, "foo/bar")), mockResponseFactory);
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
                                MimeTypeUtils.APPLICATION_JSON.getType())), mockResponseFactory);

        assertThat(staticStubRequestHandler.responseFile).isEqualTo(storageFile);
    }

    @Test
    public void handleReturnsResponse() {
        assertThat(staticResponseHandler.handle(mockHttpUriRequest, null, null))
                .isNotNull();
    }
}
