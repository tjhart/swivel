package com.tjh.swivel.config.model;

import com.tjh.swivel.config.Behavior;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jettison.json.JSONObject;
import vanderbilt.util.Maps;

import java.net.URL;

import static vanderbilt.util.Validators.notNull;

/**
 * Represents a Swivel Stub
 */
public class Stub implements Behavior {
    public static final String WHEN_KEY = "when";
    public static final String THEN_KEY = "then";
    public static final String DESCRIPTION_KEY = "description";
    private final String description;
    private final When when;
    private final Then then;

    /**
     * Construct a stub with it's description, When, and Then components
     *
     * @param description - The description of the stub
     * @param when        - The stub's When component
     * @param then        - the stub's Then component
     */
    public Stub(String description, When when, Then then) {
        this.description = notNull("description", description);
        this.when = notNull("when", when);
        this.then = notNull("then", then);
    }

    /**
     * Transforms the stub to the JSON representation the Swivel REST tree expects
     *
     * @return JSON expected by <code>rest/config/stub</code>
     */
    public JSONObject toJSON() {
        return new JSONObject(Maps.asMap(
                DESCRIPTION_KEY, description,
                WHEN_KEY, when.toJSON(),
                THEN_KEY, then.toJSON()));
    }

    /**
     * Creates an Apache Http Components <code>HttpUriRequest</code> that will submit this stub to the Swivel server
     *
     * @param baseURL - Swivel baseURL
     * @return the HttpUriRequest to submit
     */
    public HttpUriRequest toRequest(URL baseURL) {
        HttpPost request =
                new HttpPost(String.format("%1$s/rest/config/stub/%2$s", baseURL.toExternalForm(), when.getUri()));
        request.setEntity(new StringEntity(toJSON().toString(), ContentType.APPLICATION_JSON));
        return request;
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stub)) return false;

        Stub stub = (Stub) o;

        return description.equals(stub.description)
                && then.equals(stub.then)
                && when.equals(stub.when);

    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + when.hashCode();
        result = 31 * result + then.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stub{");
        sb.append("description=").append(description);
        sb.append(", when=").append(when);
        sb.append(", then=").append(then);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public String getDescription() { return description; }

    public When getWhen() { return when; }

    public Then getThen() { return then; }
    //</editor-fold>
}
