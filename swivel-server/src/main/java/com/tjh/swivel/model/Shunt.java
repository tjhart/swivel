package com.tjh.swivel.model;

import java.net.URI;

public class Shunt {
    private final URI targetUri;
    private final ShuntRequestHandler requestHandler;

    public Shunt(URI targetUri, ShuntRequestHandler requestHandler) {
        this.targetUri = targetUri;
        this.requestHandler = requestHandler;
    }

    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shunt)) return false;

        Shunt shunt = (Shunt) o;

        return targetUri.equals(shunt.targetUri) && requestHandler.equals(shunt.requestHandler);

    }

    @Override
    public int hashCode() {
        int result = targetUri.hashCode();
        result = 31 * result + requestHandler.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Shunt{");
        sb.append("targetUri=").append(targetUri);
        sb.append(", requestHandler=").append(requestHandler);
        sb.append('}');
        return sb.toString();
    }
    //</editor-fold>


    public URI getTargetUri() { return targetUri; }
}
