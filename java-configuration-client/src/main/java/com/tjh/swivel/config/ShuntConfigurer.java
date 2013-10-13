package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Shunt;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static vanderbilt.util.Validators.notNull;

/**
 * Builder to help construct a shunt. Generally returned by SwivelConfigurer#shunt
 */
public class ShuntConfigurer implements ConfigurationElement {
    protected final SwivelConfigurer swivelConfigurer;
    private URI localURI;
    private URL remoteURL;

    /**
     * Construct a shuntConfigurer with a SwivelConfigurer
     *
     * @param swivelConfigurer SwivelConfigurer
     */
    public ShuntConfigurer(SwivelConfigurer swivelConfigurer) {
        this.swivelConfigurer = notNull("swivelConfigurer", swivelConfigurer);
    }

    /**
     * Construct a shuntConfigurer with a SwivelConfigurer and the localURI for the shunt
     *
     * @param swivelConfigurer SwivelConfigurer
     * @param localURI         the shunt's localURI
     */
    public ShuntConfigurer(SwivelConfigurer swivelConfigurer, String localURI) throws URISyntaxException {
        this(swivelConfigurer);
        this.localURI = notNull("localURI", new URI(localURI));
    }

    //<editor-fold desc="builder">

    /**
     * Builder pattern - capture the localURI to create the shunt from and return <code>this</code>
     *
     * @param localURI - Swivel URI where the shunt will live
     * @return <code>this</code>
     */
    public ShuntConfigurer from(URI localURI) {
        setLocalURI(localURI);
        return this;
    }

    /**
     * Builder pattern - capture the remoteURL where the shunt will forward it's requests and return <code>this</code>
     *
     * @param remoteURL - remoteURL for the request
     * @return <code>this</code>
     * @throws IOException
     */
    public ShuntConfigurer to(URL remoteURL) throws IOException {
        setRemoteURL(remoteURL);
        return this;
    }

    //</editor-fold>
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
