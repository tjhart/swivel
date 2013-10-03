package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SwivelConfigurer {
    public static final String BASE_CONFIG_URI = "rest/config";
    public static final String STUB_PATH = "stub";
    public static final String SHUNT_PATH = "shunt";
    public static final String ID_KEY = "id";
    public static final String PATH = "path";

    protected final URL swivelURL;

    protected ClientConnectionManager clientConnectionManager = new BasicClientConnectionManager();
    protected HttpParams httpParams = new BasicHttpParams();

    public SwivelConfigurer(String swivelURL) throws MalformedURLException {
        this.swivelURL = new URL(swivelURL);
    }

    public int configure(Behavior behavior) throws IOException {
        try {
            HttpEntity responseEntity = getClient()
                    .execute(behavior.toRequest(swivelURL))
                    .getEntity();
            return new JSONObject(EntityUtils.toString(responseEntity))
                    .getInt(ID_KEY);
        } catch (JSONException e) { throw new RuntimeException(e); }
    }

    public void deleteStub(URI path, int stubID) throws IOException {
        getClient()
                .execute(new HttpDelete(getConfigURL(path, STUB_PATH) + "?id=" + stubID));
    }

    public void deletePath(URI path) throws IOException {
        getClient().execute(new HttpDelete(getConfigURL(path, PATH)));
    }

    public void reset() throws IOException {
        getClient().execute(new HttpDelete(swivelURL.toExternalForm() + "/" + BASE_CONFIG_URI));
    }

    public StubConfigurer when(When when) { return new StubConfigurer(this, when); }

    public ShuntConfigurer shunt(String localURI) throws URISyntaxException {
        return new ShuntConfigurer(this, localURI);
    }

    private String getConfigURL(URI uri, String configType) {
        return String.format("%1$s/%2$s/%3$s/%4$s", swivelURL.toExternalForm(), BASE_CONFIG_URI, configType, uri);
    }

    //useful for testing
    HttpClient getClient() { return new DefaultHttpClient(clientConnectionManager, httpParams);}

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
        final StringBuilder sb = new StringBuilder("SwivelConfigurer{");
        sb.append("swivelURL='").append(swivelURL).append('\'');
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
    //</editor-fold>
}
