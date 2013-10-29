package com.tjh.swivel.controller;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import com.tjh.swivel.model.AbstractStubRequestHandler;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.StubRequestHandler;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import vanderbilt.util.Block;
import vanderbilt.util.Lists;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Path("config/stub/{localPath: .*}")
public class ConfigureStubResource {
    public static final String STUB_ID_KEY = "id";

    protected static Logger LOGGER = Logger.getLogger(ConfigureStubResource.class);

    private Configuration configuration;
    private ObjectMapper objectMapper;
    private StubFileStorage stubFileStorage;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Map<String, Object>> getStub(@PathParam("localPath") String localPath,
            @QueryParam("ids[]") List<Integer> stubIds) {
        return Lists.collect(configuration.getStubs(localPath, stubIds),
                new Block<StubRequestHandler, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> invoke(StubRequestHandler stubRequestHandler) {
                        return stubRequestHandler.toMap();
                    }
                });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> postStub(@PathParam("localPath") String localPath, Map<String, Object> stubDescription)
            throws URISyntaxException, ScriptException {

        return addStub(new URI(localPath), createStubRequestHandler(stubDescription));
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public Map<String, Object> postStub(@PathParam("localPath") String localPath,
            @FormDataParam("stubDescription") String stubDescriptionJSON,
            @FormDataParam("contentFile") InputStream formFile,
            @FormDataParam("contentFile") FormDataBodyPart bodyPart)
            throws URISyntaxException, IOException, ScriptException {

        LOGGER.debug("Creating file for " + bodyPart.getContentDisposition().getFileName());

        return addStub(new URI(localPath), createStubRequestHandler(formFile,
                objectMapper.readValue(stubDescriptionJSON, Map.class), bodyPart.getMediaType().toString(),
                bodyPart.getContentDisposition().getFileName()));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> editStub(@PathParam("localPath") String localPath, Map<String, Object> stubDescription)
            throws URISyntaxException, ScriptException, IOException {
        RequestPathParser requestPathParser = new RequestPathParser(localPath);

        Collection<StubRequestHandler> stubs =
                configuration.getStubs(requestPathParser.getLocalPath(), Arrays.asList(requestPathParser.getStubId()));
        return editStub(requestPathParser.getLocalPath(), requestPathParser.getStubId(),
                editStubRequestHandler(stubDescription, stubs.iterator().next()));
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public Map<String, Object> editStub(@PathParam("localPath") String localPath,
            @FormDataParam("stubDescription") String stubDescriptionJSON,
            @FormDataParam("contentFile") InputStream formFile,
            @FormDataParam("contentFile") FormDataBodyPart bodyPart)
            throws URISyntaxException, IOException, ScriptException {

        RequestPathParser requestPathParser = new RequestPathParser(localPath);
        return editStub(requestPathParser.getLocalPath(), requestPathParser.getStubId(),
                createStubRequestHandler(formFile, objectMapper.readValue(stubDescriptionJSON, Map.class),
                        bodyPart.getMediaType().toString(), bodyPart.getContentDisposition().getFileName())
        );
    }

    //REDTAG:TJH - FIX THIS - should be some/path/<stubId>
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Map<String, Object>> deleteStub(@PathParam("localPath") URI localUri,
            @QueryParam(STUB_ID_KEY) int stubId) {
        LOGGER.debug(String.format("Deleting stub with id %1$d at path %2$s", stubId, localUri));
        configuration.removeStub(localUri, stubId);

        return configuration.toMap();
    }

    @SuppressWarnings("unchecked")
    protected StubRequestHandler createStubRequestHandler(InputStream formFile,
            Map<String, Object> stubMap, String contentType, String fileName) throws IOException, ScriptException {

        Map<String, Object> thenMap = (Map<String, Object>) stubMap.get(AbstractStubRequestHandler.THEN_KEY);
        thenMap.put(AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY, contentType);
        thenMap.put(AbstractStubRequestHandler.FILE_NAME_KEY, fileName);
        File storage = stubFileStorage.createFile(formFile);

        thenMap.put(AbstractStubRequestHandler.STORAGE_PATH_KEY, storage.getPath());

        return createStubRequestHandler(stubMap);
    }

    @SuppressWarnings("unchecked")
    protected StubRequestHandler editStubRequestHandler(Map<String, Object> stubDescription,
            StubRequestHandler currentStub) throws ScriptException, IOException {

        FileInputStream fileInputStream = null;
        try {
            StubRequestHandler result;
            String resourcePath = currentStub.getResourcePath();
            if (resourcePath != null) {
                //useful lie - we're only accessing strings
                Map<String, String> currentThenMap =
                        (Map<String, String>) currentStub.toMap().get(AbstractStubRequestHandler.THEN_KEY);

                fileInputStream = new FileInputStream(resourcePath);
                result = createStubRequestHandler(fileInputStream, stubDescription,
                        currentThenMap.get(AbstractStubRequestHandler.FILE_CONTENT_TYPE_KEY),
                        currentThenMap.get(AbstractStubRequestHandler.FILE_NAME_KEY));
            } else {
                result = createStubRequestHandler(stubDescription);
            }

            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
    }

    protected Map<String, Object> addStub(URI localUri, StubRequestHandler stubRequestHandler) {
        LOGGER.debug(String.format("Adding stub for %1$s", localUri));
        configuration.addStub(localUri, stubRequestHandler);
        return Maps.<String, Object>asMap(STUB_ID_KEY, stubRequestHandler.getId());
    }

    protected Map<String, Object> editStub(String localPath, int stubId, StubRequestHandler stub)
            throws URISyntaxException {
        LOGGER.debug(String.format("Replacing stub %1$s:%2$d with %3$s", localPath, stubId, stub.toMap()));
        configuration.replaceStub(new URI(localPath), stubId, stub);
        return Maps.<String, Object>asMap(STUB_ID_KEY, stub.getId());
    }

    protected StubRequestHandler createStubRequestHandler(Map<String, Object> stubDescription) throws ScriptException {
        return AbstractStubRequestHandler.createStubFor(stubDescription);
    }

    public void setConfiguration(Configuration configuration) {this.configuration = configuration;}

    public void setObjectMapper(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    public void setStubFileStorage(StubFileStorage stubFileStorage) {this.stubFileStorage = stubFileStorage;}

}
