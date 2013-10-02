package com.tjh.swivel.config;

import java.net.URI;
import java.net.URL;

import static vanderbilt.util.Validators.notNull;

public class Shunt {
    private final URL remoteURL;
    private final URI localUri;

    public Shunt(URI localUri, URL remoteURL) {
        this.localUri = notNull("localUri", localUri);
        this.remoteURL = notNull("remoteURL", remoteURL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shunt)) return false;

        Shunt shunt = (Shunt) o;

        return localUri.equals(shunt.localUri) && remoteURL.equals(shunt.remoteURL);

    }

    @Override
    public int hashCode() {
        int result = remoteURL.hashCode();
        result = 31 * result + localUri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Shunt{");
        sb.append("remoteURL=").append(remoteURL);
        sb.append(", localUri=").append(localUri);
        sb.append('}');
        return sb.toString();
    }

    public URL getRemoteURL() { return remoteURL; }

    public URI getLocalUri() { return localUri; }
}
