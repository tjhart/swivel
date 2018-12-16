package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.StringDescription;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    public static final String THEN_KEY = "then";
    public static final String WHEN_KEY = "when";
    public static final String SCRIPT_KEY = "script";
    public static final String DESCRIPTION_KEY = "description";
    public static final String ID_KEY = "id";
    public static final String STORAGE_PATH_KEY = "_storagePath";
    public static final String CONTENT_KEY = "content";
    public static final String STATUS_CODE_KEY = "statusCode";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String FILE_CONTENT_TYPE_KEY = "fileContentType";
    public static final String FILE_NAME_KEY = "fileName";

    private static Logger logger = Logger.getLogger(AbstractStubRequestHandler.class);

    protected ResponseFactory responseFactory;
    protected WhenMatcher matcher;
    protected final Map<String, Object> then;
    private final String description;

    public AbstractStubRequestHandler(Map<String, Object> stubDescription,
            ResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
        this.description = Objects.requireNonNull((String) stubDescription.get(DESCRIPTION_KEY));
        this.matcher = new WhenMatcher((Map<String, String>) stubDescription.get(WHEN_KEY));
        this.then = Collections.unmodifiableMap(
                Objects.requireNonNull((Map<String, Object>) stubDescription.get(THEN_KEY)));
    }

    //<editor-fold desc="StubRequestHandler">
    @Override
    public boolean matches(HttpUriRequest request) {
        boolean result = matcher.matches(request);
        if (Level.DEBUG.equals(logger.getEffectiveLevel())) {
            String matchDescription = "matches!";
            if (!result) {
                StringDescription mismatchDescription = new StringDescription();
                matcher.describeMismatch(request, mismatchDescription);
                matchDescription = mismatchDescription.toString();
            }

            logger.debug("Stub match result: " + matchDescription);
        }
        return result;
    }

    @Override
    public int getId() { return System.identityHashCode(this); }

    @Override
    public void releaseResources() { }

    @Override
    public String getResourcePath() { throw new UnsupportedOperationException("This stub doesn't support resources"); }

    //</editor-fold>

    //<editor-fold desc="RequestHandler">
    public abstract HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client);

    @Override
    public String description() { return description; }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                DESCRIPTION_KEY, description,
                ID_KEY, getId(),
                WHEN_KEY, matcher.toMap(),
                THEN_KEY, then
        );
    }
    //</editor-fold>

    //<editor-fold desc="Object">
    @Override
    public String toString() {
        StringDescription stringDescription = new StringDescription();
        matcher.describeTo(stringDescription);
        return "AbstractStubRequestHandler{" +
                "matcher=" + stringDescription.toString() +
                ", id=" + getId() +
                ", description=" + description +
                ", matcher=" + matcher +
                ", then=" + then +
                '}';
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public WhenMatcher getMatcher() { return matcher; }

    public Map<String, Object> getThen() { return then; }

    public String getDescription() { return description; }
    //</editor-fold>
}
