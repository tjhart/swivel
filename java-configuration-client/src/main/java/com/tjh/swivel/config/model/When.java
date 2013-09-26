package com.tjh.swivel.config.model;

import java.net.URI;
import java.net.URISyntaxException;

public class When {
    private HttpMethod method;
    private String content;
    private String contentType;
    private String remoteAddress;
    private String script;
    private URI uri;

    public When(HttpMethod method) {
        this.method = method;
    }

    //<editor-fold desc="builder">
    public When withContent(String content) {
        setContent(content);
        return this;
    }

    public When as(String contentType) {
        setContentType(contentType);
        return this;
    }

    public When from(String remoteAddress) {
        setRemoteAddress(remoteAddress);
        return this;
    }

    public When matches(String script) {
        setScript(script);
        return this;
    }

    public When to(String uriString) throws URISyntaxException {
        setURI(uriString);
        return this;
    }
    //</editor-fold>


    //<editor-fold desc="Object">
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof When)) return false;

        When when = (When) o;

        return method == when.method
                && uri.equals(when.uri)
                && !(content != null ? !content.equals(when.content) : when.content != null)
                && !(contentType != null ? !contentType.equals(when.contentType) : when.contentType != null)
                && !(remoteAddress != null ? !remoteAddress.equals(when.remoteAddress) : when.remoteAddress != null)
                && !(script != null ? !script.equals(when.script) : when.script != null);
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (remoteAddress != null ? remoteAddress.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("When{");
        sb.append("method=").append(method);
        sb.append(", content='").append(content).append('\'');
        sb.append(", contentType='").append(contentType).append('\'');
        sb.append(", remoteAddress='").append(remoteAddress).append('\'');
        sb.append(", script='").append(script).append('\'');
        sb.append(", uri=").append(uri);
        sb.append('}');
        return sb.toString();
    }

    //</editor-fold>

    //<editor-fold desc="bean">
    public void setContent(String content) {
        if (!method.isAcceptsData()) {
            throw new IllegalStateException("HTTP '" + method.getMethodName() + "' does not accept data");
        }
        this.content = content;
    }

    public String getContent() { return content; }

    public HttpMethod getMethod() { return method; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getRemoteAddress() { return remoteAddress; }

    public void setRemoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; }

    public String getScript() { return script; }

    public void setScript(String script) { this.script = script; }

    public URI getUri() { return uri; }

    public void setURI(URI uri) { this.uri = uri; }

    public void setURI(String uriString) throws URISyntaxException {this.uri = new URI(uriString);}
    //</editor-fold>
}
