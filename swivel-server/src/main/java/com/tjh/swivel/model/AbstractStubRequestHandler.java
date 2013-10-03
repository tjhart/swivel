package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.StringDescription;
import vanderbilt.util.Maps;

import java.net.URI;
import java.util.Map;

import static vanderbilt.util.Validators.notNull;

public abstract class AbstractStubRequestHandler implements StubRequestHandler {
    private static Logger logger = Logger.getLogger(AbstractStubRequestHandler.class);
    protected final WhenMatcher matcher;
    private final Map<String, Object> then;

    public AbstractStubRequestHandler(WhenMatcher matcher, Map<String, Object> then) {
        this.matcher = notNull("matcher", matcher);
        this.then = notNull("then", then);
    }

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

    //<editor-fold desc="RequestHandler">
    @Override
    public int getId() { return System.identityHashCode(this); }

    public abstract HttpResponse handle(HttpUriRequest request, URI matchedURI, HttpClient client);

    @Override
    public String description() { return "Stub " + getId(); }

    @Override
    public Map<String, Object> toMap() {
        return Maps.<String, Object>asMap(
                "id", getId(),
                "when", matcher.getWhen(),
                "then", then
        );
    }
    //</editor-fold>

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractStubRequestHandler{");
        StringDescription stringDescription = new StringDescription();
        matcher.describeTo(stringDescription);
        sb.append("matcher=").append(stringDescription.toString());
        sb.append(", id=").append(getId());
        sb.append('}');
        return sb.toString();
    }

    public WhenMatcher getMatcher() { return matcher; }

    public Map<String, Object> getThen() { return then; }
}
