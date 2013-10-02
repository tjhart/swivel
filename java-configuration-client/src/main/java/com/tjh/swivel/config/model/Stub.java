package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONObject;
import vanderbilt.util.Maps;

import java.net.URI;

import static vanderbilt.util.Validators.notNull;

public class Stub {
    public static final String WHEN_KEY = "when";
    public static final String THEN_KEY = "then";
    private final When when;
    private final Then then;

    public Stub(When when, Then then) {
        this.when = notNull("when", when);
        this.then = notNull("then", then);
    }

    public JSONObject toJSON() {
        return new JSONObject(Maps.asMap(WHEN_KEY, when.toJSON(), THEN_KEY, then.toJSON()));
    }

    public URI getURI() { return when.getUri(); }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stub)) return false;

        Stub stub = (Stub) o;

        return when.equals(stub.when)
                && then.equals(stub.then);

    }

    @Override
    public int hashCode() {
        int result = when.hashCode();
        result = 31 * result + then.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stub{");
        sb.append("when=").append(when);
        sb.append(", then=").append(then);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public When getWhen() { return when; }

    public Then getThen() { return then; }
    //</editor-fold>
}
