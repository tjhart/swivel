package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.When;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class SwivelConfigurer {
    public static final String STUB_CONFIG_URI = "rest/config/stub";
    public static final String ID_KEY = "id";

    protected final URL swivelURI;

    protected ClientConnectionManager clientConnectionManager = new BasicClientConnectionManager();
    protected HttpParams httpParams = new BasicHttpParams();

    public SwivelConfigurer(String swivelURI) throws MalformedURLException {
        this.swivelURI = new URL(swivelURI);
    }

    public int configure(Stub stub) throws IOException {
        try {
            HttpPost request = new HttpPost(
                    stubConfigURL(stub.getURI().toString()));
            request.setEntity(new StringEntity(stub.toJSON().toString(), ContentType.APPLICATION_JSON));
            HttpEntity responseEntity = getClient()
                    .execute(request)
                    .getEntity();
            return new JSONObject(EntityUtils.toString(responseEntity))
                    .getInt(ID_KEY);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public int configure(Shunt shunt) {
        return 0;
    }

    private String stubConfigURL(String uri) {
        return String.format("%1$s/%2$s/%3$s", swivelURI.toExternalForm(), STUB_CONFIG_URI, uri);
    }

    public void deleteStub(String path, int stubID) throws IOException {
        getClient()
                .execute(new HttpDelete(stubConfigURL(path) + "?id=" + stubID));
    }

    public StubConfigurer when(When when) { return new StubConfigurer(this, when); }


    public ShuntConfigurer shunt(String localURI) throws URISyntaxException {
        return new ShuntConfigurer(this, localURI);
    }

    //useful for testing
    HttpClient getClient() { return new DefaultHttpClient(clientConnectionManager, httpParams);}

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SwivelConfigurer)) return false;

        SwivelConfigurer that = (SwivelConfigurer) o;

        return swivelURI.equals(that.swivelURI);
    }

    @Override
    public int hashCode() { return swivelURI.hashCode(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SwivelConfigurer{");
        sb.append("swivelURI='").append(swivelURI).append('\'');
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
