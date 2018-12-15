package com.tjh.swivel.config.model;

import com.tjh.swivel.config.Behavior;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;


/**
 * Represents a Swivel Shunt
 */
public class Shunt implements Behavior {
    public static final String REMOTE_URL_KEY = "remoteURL";
    private final URL remoteURL;
    private final URI localURI;

    /**
     * Construct a Shunt represented by a the Swivel proxy <code>localURI</code> where the shunt will
     * live, and the <code>remoteURL</code> to which the shunt will forward requests
     *
     * @param localURI  - where the shunt will live in the Swivel lookup tree
     * @param remoteURL - the URL to which the shunt will forward requests
     */
    public Shunt(URI localURI, URL remoteURL) {
        this.localURI = Objects.requireNonNull(localURI);
        this.remoteURL = Objects.requireNonNull(remoteURL);
    }

    /**
     * Transforms the shunt to the JSON representation the Swivel REST tree expects
     *
     * @return JSON expected by <code>rest/config/shunt</code>
     */
    public JSONObject toJSON() { return new JSONObject(Map.of(REMOTE_URL_KEY, remoteURL)); }

    /**
     * Creates an Apache Http Components <code>HttpUriRequest</code> that will submit this shunt to the Swivel server
     *
     * @param baseURL - Swivel baseURL
     * @return the HttpUriRequest to submit
     */
    @Override
    public HttpUriRequest toRequest(URL baseURL) {
        HttpPut request = new HttpPut(String.format("%1$s/rest/config/shunt/%2$s", baseURL.toExternalForm(), localURI));
        request.setEntity(new StringEntity(toJSON().toString(), ContentType.APPLICATION_JSON));
        return request;
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shunt)) return false;

        Shunt shunt = (Shunt) o;

        return localURI.equals(shunt.localURI) && remoteURL.equals(shunt.remoteURL);

    }

    @Override
    public int hashCode() {
        int result = remoteURL.hashCode();
        result = 31 * result + localURI.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Shunt{" +
                "remoteURL=" + remoteURL +
                ", localURI=" + localURI +
                '}';
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public URL getRemoteURL() { return remoteURL; }

    public URI getLocalURI() { return localURI; }
    //</editor-fold>
}
