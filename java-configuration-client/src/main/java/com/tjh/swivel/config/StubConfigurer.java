package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import vanderbilt.util.Maps;

import java.net.URI;

public class StubConfigurer {
    private final SwivelConfigurer swivelConfigurer;
    private When when;

    public StubConfigurer(SwivelConfigurer swivelConfigurer, When when) {
        if (swivelConfigurer == null || when == null) {
            throw new IllegalArgumentException(
                    "Both arguments are required: " + Maps.asMap("swivelConfigurer", swivelConfigurer, "when", when));
        }
        this.swivelConfigurer = swivelConfigurer;
        this.when = when;
    }


    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StubConfigurer)) return false;

        StubConfigurer that = (StubConfigurer) o;

        return swivelConfigurer.equals(that.swivelConfigurer)
                && when.equals(that.when);

    }

    @Override
    public int hashCode() {
        int result = swivelConfigurer.hashCode();
        result = 31 * result + when.hashCode();
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
