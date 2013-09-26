package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;

import java.net.URI;

public class StubConfigurer {
    private final SwivelConfigurer swivelConfigurer;
    private When when;

    public StubConfigurer(SwivelConfigurer swivelConfigurer) {
        if (swivelConfigurer == null) {
            throw new IllegalArgumentException("swivelConfigurer cannot be null");
        }
        this.swivelConfigurer = swivelConfigurer;
    }

    public StubConfigurer(SwivelConfigurer swivelConfigurer, When when) {
        this(swivelConfigurer);
        setWhen(when);
    }


    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StubConfigurer)) return false;

        StubConfigurer that = (StubConfigurer) o;

        return swivelConfigurer.equals(that.swivelConfigurer)
                && !(when != null ? !when.equals(that.when) : that.when != null);

    }

    @Override
    public int hashCode() {
        int result = swivelConfigurer.hashCode();
        result = 31 * result + (when != null ? when.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StubConfigurer{");
        sb.append("swivelConfigurer=").append(swivelConfigurer);
        sb.append(", when=").append(when);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    public void setWhen(When when) {
        URI uri = when.getUri();
        if (uri == null || uri.getPath().length() == 0) {
            throw new IllegalArgumentException("Swivel stubs must be related to a URI:" + when);
        }
        this.when = when;
    }

    public When getWhen() { return when; }

    public SwivelConfigurer getSwivelConfigurer() { return swivelConfigurer; }
}
