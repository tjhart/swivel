package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


/**
 * Represents the Then component of a stub
 */
public class Then {

    public static final String SCRIPT_KEY = "script";
    public static final String STATUS_CODE_KEY = "statusCode";
    public static final String CONTENT_KEY = "content";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String FILENAME_KEY = "fileName";

    private static final Map<String, Function<Then, Object>> OPTIONAL_VALUES = Map.of(
            STATUS_CODE_KEY, t -> t.responseCode.getCode(),
            CONTENT_KEY, t -> t.content,
            CONTENT_TYPE_KEY, t -> t.contentType,
            FILENAME_KEY, t -> t.file == null ? null : t.file.getName());

    private final HttpResponseCode responseCode;
    private final String script;
    private String content;
    private String contentType;
    private File file;
    private String contentDisposition;

    /**
     * Construct a Then component with the response code
     *
     * @param responseCode - the response code the stub will return
     */
    public Then(HttpResponseCode responseCode) {
        this.responseCode = Objects.requireNonNull(responseCode);
        this.script = null;
    }

    /**
     * Construct a Then component with a script that will a Apache HTTP Components HttpResponse.
     * The script will
     *
     * @param script - The script to execute to generate the stub response
     */
    public Then(String script) {
        this.script = Objects.requireNonNull(script);
        this.responseCode = null;
    }

    /**
     * The JSON representation of the Then stub component
     *
     * @return JSONObject
     */
    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();

            if (script != null) {
                jsonObject.put(SCRIPT_KEY, script);
            } else {
                for (Map.Entry<String, Function<Then, Object>> entry : OPTIONAL_VALUES.entrySet()) {
                    jsonObject.putOpt(entry.getKey(), entry.getValue().apply(this));
                }
            }
            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //<editor-fold desc="builder">

    /**
     * Builder pattern - Add content to the response and return <code>this</code> to continue
     * building
     *
     * @param content - the content to return
     * @return this
     */
    public Then withContent(String content) {
        setContent(content);
        return this;
    }

    /**
     * Builder pattern - Add content type to the response and return <code>this</code> to continue
     * building
     *
     * @param contentType - the content type (mime-type) of <code>content</code>
     * @return this
     */
    public Then as(String contentType) {
        setContentType(contentType);
        return this;
    }

    public Then withFile(File file) {
        setFile(file);
        return this;
    }

    public Then withContentDisposition(String contentDisposition) {
        setContentDisposition(contentDisposition);
        return this;
    }
    //</editor-fold>

    //<editor-fold desc="bean">
    public HttpResponseCode getResponseCode() { return responseCode; }

    public String getScript() { return script; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }

    public String getContentDisposition() { return contentDisposition; }

    public void setContentDisposition(String contentDisposition) { this.contentDisposition = contentDisposition; }
    //</editor-fold>
}
