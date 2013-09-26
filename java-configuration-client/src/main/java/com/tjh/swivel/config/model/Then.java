package com.tjh.swivel.config.model;

public class Then {

    private final HttpResponseCode responseCode;
    private final String script;
    private String content;
    private String contentType;

    public Then(HttpResponseCode responseCode) {
        if (responseCode == null) {
            throw new IllegalArgumentException("responseCode cannot be null");
        }
        this.responseCode = responseCode;
        this.script = null;
    }

    public Then(String script) {
        if (script == null) {
            throw new IllegalArgumentException("script cannot be null");
        }
        this.script = script;
        this.responseCode = null;
    }

    public HttpResponseCode getResponseCode() { return responseCode; }

    public Then withContent(String content) {
        setContent(content);
        return this;
    }

    public Then as(String contentType) {
        setContentType(contentType);
        return this;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getScript() { return script; }
}
