package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import vanderbilt.util.Maps;

import java.util.Map;

public class Then {

    public static final String SCRIPT_KEY = "script";
    public static final String STATUS_CODE_KEY = "statusCode";
    public static final String REASON_KEY = "reason";
    public static final String CONTENT_KEY = "content";
    public static final String CONTENT_TYPE_KEY = "contentType";
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

    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();

            if (script != null) {
                jsonObject.put(SCRIPT_KEY, script);
            } else {
                Map<String, Object> optionalValues = Maps.<String, Object>asMap(
                        STATUS_CODE_KEY, responseCode.getCode(),
                        REASON_KEY, responseCode.getReason(),
                        CONTENT_KEY, content,
                        CONTENT_TYPE_KEY, contentType);
                for (Map.Entry<String, Object> entry : optionalValues.entrySet()) {
                    jsonObject.putOpt(entry.getKey(), entry.getValue());
                }
            }
            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //<editor-fold desc="builder">
    public Then withContent(String content) {
        setContent(content);
        return this;
    }

    public Then as(String contentType) {
        setContentType(contentType);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public HttpResponseCode getResponseCode() { return responseCode; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getScript() { return script; }
    //</editor-fold>
}
