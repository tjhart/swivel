package com.tjh.swivel.config.model;

public class When {
    private HttpMethod method;
    private String content;
    private String contentType;
    private String remoteAddress;
    private String script;

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
    //</editor-fold>

    //<editor-fold desc="bean">
    public HttpMethod getMethod() { return method; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getRemoteAddress() { return remoteAddress; }

    public void setRemoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; }

    public String getScript() { return script; }

    public void setScript(String script) { this.script = script; }
    //</editor-fold>
}
