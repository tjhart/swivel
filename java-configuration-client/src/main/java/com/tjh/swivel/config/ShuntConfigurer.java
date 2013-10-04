package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Shunt;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static vanderbilt.util.Validators.notNull;

public class ShuntConfigurer {
    protected final SwivelConfigurer swivelConfigurer;
    private URI localURI;
    private URL remoteURL;

    public ShuntConfigurer(SwivelConfigurer swivelConfigurer) {
        this.swivelConfigurer = notNull("swivelConfigurer", swivelConfigurer);
    }

    public ShuntConfigurer(SwivelConfigurer swivelConfigurer, String localURI) throws URISyntaxException {
        this(swivelConfigurer);
        this.localURI = notNull("localURI", new URI(localURI));
    }

    public ShuntConfigurer from(URI localURI) {
        setLocalURI(localURI);
        return this;
    }

    public int to(URL remoteURL) throws IOException {
        setRemoteURL(remoteURL);
        return configure();
    }

    public int configure() throws IOException {return swivelConfigurer.configure(new Shunt(localURI, remoteURL));}

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShuntConfigurer)) return false;

        ShuntConfigurer that = (ShuntConfigurer) o;

        return swivelConfigurer.equals(that.swivelConfigurer)
                && !(localURI != null ? !localURI.equals(that.localURI) : that.localURI != null)
                && !(remoteURL != null ? !remoteURL.equals(that.remoteURL) : that.remoteURL != null);

    }

    @Override
    public int hashCode() {
        int result = swivelConfigurer.hashCode();
        result = 31 * result + (localURI != null ? localURI.hashCode() : 0);
        result = 31 * result + (remoteURL != null ? remoteURL.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShuntConfigurer{");
        sb.append("swivelConfigurer=").append(swivelConfigurer);
        sb.append(", localURI=").append(localURI);
        sb.append(", remoteURL=").append(remoteURL);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public URI getLocalURI() { return localURI; }

    public void setLocalURI(URI localURI) {
        this.localURI = notNull("localURI", localURI);
    }

    public URL getRemoteURL() { return remoteURL; }

    public void setRemoteURL(URL remoteURL) {
        this.remoteURL = notNull("remoteURL", remoteURL);
    }
    //</editor-fold>
}
