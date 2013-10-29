package com.tjh.swivel.controller;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.tjh.swivel.model.AbstractStubRequestHandler;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.StubRequestHandler;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConfigureStubResourceTest {
    public static final String LOCAL_PATH = "extra/path";
    public static final URI LOCAL_URI = URI.create(LOCAL_PATH);
    public static final int STUB_HANDLER_ID = 12345;
    public static final String APPLICATION_PDF = "application/pdf";
    public static final String FILE_NAME_TXT = "fileName.txt";
    public static final long FILE_SIZE = 42L;
    public static final String MOCK_JSON_STRING = "{}";
    private ConfigureStubResource configureStubResource;
    private Configuration mockConfiguration;
    private Map<String, Object> mockStubMap;
    private Map<String, Object> mockThenMap;
    private StubRequestHandler mockStubRequestHandler;
    private ObjectMapper mockObjectMapper;
    private InputStream mockInputStream;
    private FormDataBodyPart mockBodyPart;
    private ContentDisposition mockContentDisposition;
    private File mockFile;
    private StubFileStorage mockStorage;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        configureStubResource = new ConfigureStubResource();
        mockConfiguration = mock(Configuration.class);
        mockStubRequestHandler = mock(StubRequestHandler.class);
        mockObjectMapper = mock(ObjectMapper.class);

        mockStubMap = mock(Map.class);
        mockThenMap = mock(Map.class);
        mockInputStream = mock(InputStream.class);
        mockStorage = mock(StubFileStorage.class);
        mockFile = mock(File.class);

        configureStubResource.setConfiguration(mockConfiguration);
        configureStubResource.setObjectMapper(mockObjectMapper);
        configureStubResource.setStubFileStorage(mockStorage);

        configureStubResource = spy(configureStubResource);
        mockBodyPart = mock(FormDataBodyPart.class);
        mockContentDisposition = mock(ContentDisposition.class);

        doReturn(mockStubRequestHandler)
                .when(configureStubResource)
                .createStubRequestHandler(anyMap());

        when(mockStubRequestHandler.getId())
                .thenReturn(STUB_HANDLER_ID);
        when(mockObjectMapper.readValue(anyString(), any(Class.class)))
                .thenReturn(mockStubMap);
        when(mockBodyPart.getMediaType())
                .thenReturn(MediaType.valueOf(APPLICATION_PDF));
        when(mockStubMap.get(AbstractStubRequestHandler.THEN_KEY))
                .thenReturn(mockThenMap);
        when(mockBodyPart.getContentDisposition())
                .thenReturn(mockContentDisposition);
        when(mockContentDisposition.getSize())
                .thenReturn(FILE_SIZE);
        when(mockContentDisposition.getFileName())
                .thenReturn(FILE_NAME_TXT);
        when(mockStorage.createFile(any(InputStream.class)))
                .thenReturn(mockFile);
        when(mockConfiguration.getStubs(LOCAL_PATH, Arrays.asList(STUB_HANDLER_ID)))
                .thenReturn(Arrays.asList(mockStubRequestHandler));
    }

    @Test
    public void postStubDefersToFactory() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, mockStubMap);

        verify(configureStubResource).createStubRequestHandler(mockStubMap);
    }

    @Test
    public void postStubDefersToConfiguration() throws URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, mockStubMap);

        verify(mockConfiguration).addStub(LOCAL_URI, mockStubRequestHandler);
    }

    @Test
    public void postStubReturnsRouterIDForStub() throws URISyntaxException, ScriptException {

        assertThat((Integer) configureStubResource.postStub(LOCAL_PATH, mockStubMap).get("id"),
                equalTo(STUB_HANDLER_ID));
    }

    @Test
    public void deleteStubRemovesStubAtURI() throws ScriptException, URISyntaxException {
        configureStubResource.postStub(LOCAL_PATH, mockStubMap);

        configureStubResource.deleteStub(LOCAL_PATH + "/" + STUB_HANDLER_ID);

        verify(mockConfiguration).removeStub(LOCAL_URI, STUB_HANDLER_ID);
    }

    @Test
    public void getStubDefersToConfiguration() {
        List<Integer> stubIds = Arrays.asList(1);
        configureStubResource.getStub(LOCAL_PATH, stubIds);

        verify(mockConfiguration).getStubs(LOCAL_PATH, stubIds);
    }

    @Test
    public void getStubsCollectsMapsReturnedByConfiguration() {
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        when(mockConfiguration.getStubs(eq(LOCAL_PATH), anyList()))
                .thenReturn(Arrays.asList(mockStubRequestHandler));
        when(mockStubRequestHandler.toMap()).thenReturn(expectedMap);
        assertThat(configureStubResource.getStub(LOCAL_PATH, Collections.<Integer>emptyList()),
                CoreMatchers.<Collection<Map<String, Object>>>equalTo(Arrays.asList(expectedMap)));
    }

    @Test
    public void editStubDefersToCreateStub() throws ScriptException, URISyntaxException, IOException {
        configureStubResource.editStub(LOCAL_PATH + "/12345", mockStubMap);

        verify(configureStubResource).createStubRequestHandler(mockStubMap);
    }

    @Test
    public void editStubDefersToConfigurationAsExpected() throws ScriptException, URISyntaxException, IOException {
        configureStubResource.editStub(LOCAL_PATH + "/12345", mockStubMap);

        verify(mockConfiguration).replaceStub(URI.create(LOCAL_PATH), 12345, mockStubRequestHandler);
    }

    @Test
    public void editStubReturnsIDMap() throws ScriptException, URISyntaxException, IOException {
        assertThat(configureStubResource.editStub(LOCAL_PATH + "/12345", mockStubMap),
                CoreMatchers.<Map<String, Object>>equalTo(
                        Maps.<String, Object>asMap(ConfigureStubResource.STUB_ID_KEY, STUB_HANDLER_ID)));
    }

    @Test
    public void postStubWithFileDefersToObjectMapper() throws IOException, URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(mockObjectMapper).readValue(MOCK_JSON_STRING, Map.class);
    }

    @Test
    public void postStubWithFileUpdatesStubDescriptionWithContentType()
            throws IOException, URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(mockStubMap).get(AbstractStubRequestHandler.THEN_KEY);
        verify(mockThenMap).put(AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY, APPLICATION_PDF);
    }

    @Test
    public void postStubWithFileStoresFile() throws IOException, URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(mockBodyPart, atLeastOnce()).getContentDisposition();
        verify(mockStorage).createFile(mockInputStream);
    }

    @Test
    public void postStubWithFileDefersToCreateStub() throws IOException, URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(mockBodyPart, atLeastOnce()).getContentDisposition();
        verify(mockContentDisposition, atLeastOnce()).getFileName();
        verify(configureStubResource).createStubRequestHandler(mockStubMap);
    }

    @Test
    public void postStubWithFileStoresFilename() throws IOException, URISyntaxException, ScriptException {
        configureStubResource.postStub(LOCAL_PATH, MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(mockThenMap).put(AbstractStubRequestHandler.FILE_NAME_KEY, FILE_NAME_TXT);
    }

    @Test
    public void editStubWithInputStreamDefersToCreateStubRequestHandlerWithInputStream()
            throws IOException, URISyntaxException, ScriptException {

        doReturn(mockStubRequestHandler)
                .when(configureStubResource)
                .createStubRequestHandler(any(InputStream.class), anyMap(), anyString(), anyString());

        configureStubResource.editStub(LOCAL_PATH + "/12345", MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(configureStubResource).createStubRequestHandler(mockInputStream, mockStubMap, APPLICATION_PDF,
                FILE_NAME_TXT);
    }

    @Test
    public void editStubWithInputStreamDefersToEditStub()
            throws IOException, URISyntaxException, ScriptException {

        doReturn(mockStubRequestHandler)
                .when(configureStubResource)
                .createStubRequestHandler(any(InputStream.class), anyMap(), anyString(), anyString());

        configureStubResource.editStub(LOCAL_PATH + "/12345", MOCK_JSON_STRING, mockInputStream, mockBodyPart);

        verify(configureStubResource).editStub(LOCAL_PATH, 12345, mockStubRequestHandler);
    }

    @Test
    public void editStubRequestHandlerPreservesExistingFile() throws ScriptException, IOException, URISyntaxException {

        File tmpFile = File.createTempFile("test", "txt");
        tmpFile.deleteOnExit();

        when(mockStubRequestHandler.getResourcePath()).
                thenReturn(tmpFile.getPath());
        when(mockStubRequestHandler.toMap())
                .thenReturn(Maps.<String, Object>asMap(AbstractStubRequestHandler.THEN_KEY,
                        Maps.asMap(AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY, "text/plain",
                                AbstractStubRequestHandler.FILE_NAME_KEY, "someFile.txt")));

        configureStubResource.editStubRequestHandler(mockStubMap, mockStubRequestHandler);

        verify(configureStubResource).createStubRequestHandler(any(InputStream.class), eq(mockStubMap), anyString(),
                anyString());
    }

    @Test
    public void editStubRequestHandlerIgnoresExistingStubIfNoResourcePath() throws ScriptException, IOException {
        when(mockStubRequestHandler.toMap())
                .thenReturn(Maps.<String, Object>asMap(AbstractStubRequestHandler.THEN_KEY,
                        Maps.asMap(AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY, "text/plain",
                                AbstractStubRequestHandler.FILE_NAME_KEY, "someFile.txt")));

        configureStubResource.editStubRequestHandler(mockStubMap, mockStubRequestHandler);

        verify(configureStubResource).createStubRequestHandler(mockStubMap);
    }
}
