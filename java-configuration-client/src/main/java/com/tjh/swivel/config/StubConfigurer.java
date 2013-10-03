package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;

import java.io.IOException;

import static vanderbilt.util.Validators.notNull;

public class StubConfigurer {
    private final SwivelConfigurer swivelConfigurer;
    private When when;
    private Then then;

    public StubConfigurer(SwivelConfigurer swivelConfigurer) {
        this.swivelConfigurer = notNull("swivelConfigurer", swivelConfigurer);
    }

    public StubConfigurer(SwivelConfigurer swivelConfigurer, When when) {
        this(swivelConfigurer);
        setWhen(when);
    }


    public StubConfigurer when(When when) {
        setWhen(when);
        return this;
    }

    public int thenReturn(Then then) throws IOException {
        setThen(then);
        return configure();
    }

    public int configure() throws IOException {return swivelConfigurer.configure(new Stub(when, this.then));}

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

    //<editor-fold desc="bean">
    public SwivelConfigurer getSwivelConfigurer() { return swivelConfigurer; }

    public When getWhen() { return when; }

    public void setWhen(When when) { this.when = notNull("when", when); }

    public Then getThen() { return then; }

    public void setThen(Then then) { this.then = notNull("then", then); }
    //</editor-fold>
}
