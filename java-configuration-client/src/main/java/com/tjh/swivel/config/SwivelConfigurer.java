package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.When;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;
import java.net.URL;

public class SwivelConfigurer {
    protected final URL swivelURI;

    protected ClientConnectionManager clientConnectionManager = new BasicClientConnectionManager();
    protected HttpParams httpParams = new BasicHttpParams();

    public SwivelConfigurer(String swivelURI) throws MalformedURLException {
        this.swivelURI = new URL(swivelURI);
    }

    public int configure(Stub stub) {
        int result = 0;
        return result;
    }

    public StubConfigurer when(When when) { return new StubConfigurer(this, when); }

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


    public void setClientConnectionManager(ClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setHttpParams(HttpParams httpParams) { this.httpParams = httpParams; }
}
