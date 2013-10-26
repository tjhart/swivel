package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.StringDescription;
import vanderbilt.util.Maps;

import javax.script.ScriptException;
import java.io.File;
import java.net.URI;
import java.util.Map;

import static vanderbilt.util.Validators.notNull;

@SuppressWarnings("unchecked")
public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    public static final String THEN_KEY = "then";
    public static final String WHEN_KEY = "when";
    public static final String SCRIPT_KEY = "script";
    public static final String DESCRIPTION_KEY = "description";
    public static final String ID_KEY = "id";

    private static Logger logger = Logger.getLogger(AbstractStubRequestHandler.class);
    protected static ResponseFactory responseFactory = new ResponseFactory();

    protected WhenMatcher matcher;
    protected final Map<String, Object> then;
    private final String description;

    public static StubRequestHandler createStubFor(Map<String, Object> stubDescription) throws ScriptException {
        StubRequestHandler result;
        if (((Map<String, Object>) stubDescription.get(THEN_KEY)).containsKey(SCRIPT_KEY)) {
            result = new DynamicStubRequestHandler(stubDescription);
        } else {
            result = new StaticStubRequestHandler(stubDescription);
        }
        return result;
    }

    public static StubRequestHandler createStubFor(Map<String, Object> stubMap, File responseFile) {
        return new StaticStubRequestHandler(stubMap, responseFile);
    }

    public AbstractStubRequestHandler(Map<String, Object> stubDescription) {
        this.description = notNull(DESCRIPTION_KEY, (String) stubDescription.get(DESCRIPTION_KEY));
        this.matcher = new WhenMatcher((Map<String, String>) stubDescription.get(WHEN_KEY));
        this.then = notNull(THEN_KEY, (Map<String, Object>) stubDescription.get(THEN_KEY));
    }

    static void setResponseFactory(ResponseFactory responseFactory) {
        AbstractStubRequestHandler.responseFactory = responseFactory;
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
    //</editor-fold>

    //<editor-fold desc="RequestHandler">
    public abstract HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client);

    @Override
    public String description() { return description; }

    @Override
    public Map<String, Object> toMap() {
        return Maps.<String, Object>asMap(
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
        final StringBuilder sb = new StringBuilder("AbstractStubRequestHandler{");
        StringDescription stringDescription = new StringDescription();
        matcher.describeTo(stringDescription);
        sb.append("matcher=").append(stringDescription.toString());
        sb.append(", id=").append(getId());
        sb.append(", description=").append(description);
        sb.append(", matcher=").append(matcher);
        sb.append(", then=").append(then);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public WhenMatcher getMatcher() { return matcher; }

    public Map<String, Object> getThen() { return then; }

    public String getDescription() { return description; }
    //</editor-fold>
}
