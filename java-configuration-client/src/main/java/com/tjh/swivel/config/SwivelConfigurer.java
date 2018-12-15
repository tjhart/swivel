package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Main class for automatically configuring a swivel server
 */
public class SwivelConfigurer {
    public static final String BASE_CONFIG_URI = "rest/config";
    public static final String STUB_PATH = "stub";
    public static final String SHUNT_PATH = "shunt";
    public static final String ID_KEY = "id";
    public static final String PATH = "path";

    protected final URL swivelURL;

    protected HttpClientConnectionManager clientConnectionManager = new BasicHttpClientConnectionManager();

    /**
     * Create a SwivelConfigurer with the URL of the target Swivel Server
     *
     * @param swivelURL - Swivel Server URL
     * @throws MalformedURLException
     */
    public SwivelConfigurer(String swivelURL) throws MalformedURLException {
        this.swivelURL = new URL(swivelURL);
    }

    /**
     * Configure a specific behavior on the server
     *
     * @param behavior a Behavior (Shunt or Stub)
     * @return The Behavior's ID
     * @throws IOException
     */
    public int configure(Behavior behavior) throws IOException {
        String response = "";
        try {
            HttpEntity responseEntity = getClient()
                    .execute(behavior.toRequest(swivelURL))
                    .getEntity();
            response = EntityUtils.toString(responseEntity);
            return new JSONObject(response)
                    .getInt(ID_KEY);
        } catch (JSONException e) { throw new RuntimeException("Could not parse response: " + response, e); }
    }

    /**
     * Delete a stub previously configured on the target Swivel Server
     *
     * @param path   - the swivel path of the stub to delete
     * @param stubID - the id of the Stub to delete
     * @throws IOException
     */
    public void deleteStub(URI path, int stubID) throws IOException {
        getClient()
                .execute(new HttpDelete(getConfigURL(path, STUB_PATH) + "/" + stubID));
    }

    /**
     * Delete all stubs and shunts at the given Swivel path
     *
     * @param path - the path to delete
     * @throws IOException
     */
    public void deletePath(URI path) throws IOException {
        getClient().execute(new HttpDelete(getConfigURL(path, PATH)));
    }

    /**
     * Delete all stubs and shunts registered in target Swivel server
     *
     * @throws IOException
     */
    public void reset() throws IOException {
        getClient().execute(new HttpDelete(swivelURL.toExternalForm() + "/" + BASE_CONFIG_URI));
    }

    /**
     * Create a StubConfigurer with this SwivelConfigurer, and the given When
     *
     * @param when - The target Stub's When component
     * @return A StubConfigurer to use to continue building a stub
     */
    public StubConfigurer when(When when) { return new StubConfigurer(this, when); }

    /**
     * Create a ShuntConfigurer with this SwivelConfigurer, and the given localURI
     *
     * @param localURI - the SwivelURI where the Shunt will live
     * @return A ShuntConfigurer to use to continue building a shunt
     * @throws URISyntaxException
     */
    public ShuntConfigurer shunt(String localURI) throws URISyntaxException {
        return new ShuntConfigurer(this, localURI);
    }

    private String getConfigURL(URI uri, String configType) {
        return String.format("%1$s/%2$s/%3$s/%4$s", swivelURL.toExternalForm(), BASE_CONFIG_URI, configType, uri);
    }

    //useful for testing
    HttpClient getClient() {
        return HttpClientBuilder.create()
                .setConnectionManager(clientConnectionManager)
                .build();
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SwivelConfigurer)) return false;

        SwivelConfigurer that = (SwivelConfigurer) o;

        return swivelURL.equals(that.swivelURL);
    }

    @Override
    public int hashCode() { return swivelURL.hashCode(); }

    @Override
    public String toString() {
        return "SwivelConfigurer{" +
                "swivelURL='" + swivelURL + '\'' +
                '}';
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public void setClientConnectionManager(HttpClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }
    //</editor-fold>
}
