package com.tjh.swivel.config.model;

import com.tjh.swivel.config.Behavior;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.codehaus.jettison.json.JSONObject;
import vanderbilt.util.Maps;

import java.net.URI;
import java.net.URL;

import static vanderbilt.util.Validators.notNull;

public class Shunt implements Behavior {
    public static final String REMOTE_URL_KEY = "remoteURL";
    private final URL remoteURL;
    private final URI localURI;

    public Shunt(URI localURI, URL remoteURL) {
        this.localURI = notNull("localURI", localURI);
        this.remoteURL = notNull(REMOTE_URL_KEY, remoteURL);
    }

    public JSONObject toJSON() { return new JSONObject(Maps.asMap(REMOTE_URL_KEY, remoteURL)); }

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
        final StringBuilder sb = new StringBuilder("Shunt{");
        sb.append("remoteURL=").append(remoteURL);
        sb.append(", localURI=").append(localURI);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public URL getRemoteURL() { return remoteURL; }

    public URI getLocalURI() { return localURI; }
    //</editor-fold>
}
