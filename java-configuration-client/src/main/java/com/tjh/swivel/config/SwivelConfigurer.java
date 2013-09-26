package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.When;

import java.net.MalformedURLException;
import java.net.URL;

public class SwivelConfigurer {
    protected final URL swivelURI;

    public SwivelConfigurer(String swivelURI) throws MalformedURLException {
        this.swivelURI = new URL(swivelURI);
    }

    public int configure(Stub stub) {
        int result = 0;
        return result;
    }

    public StubConfigurer when(When when) { return new StubConfigurer(this, when); }

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
}
