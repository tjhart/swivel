package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


/**
 * A class representing the 'When' component of a stub
 */
public class When {
    public static final String METHOD_KEY = "method";
    public static final String SCRIPT_KEY = "script";
    public static final String CONTENT_KEY = "content";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String REMOTE_ADDRESS_KEY = "remoteAddress";
    public static final String QUERY_KEY = "query";

    private static final Map<String, Function<When, Object>> OPTIONAL_VALUES = Map.of(
            QUERY_KEY, w -> w.query,
            CONTENT_KEY, w -> w.content,
            SCRIPT_KEY, w -> w.script,
            CONTENT_TYPE_KEY, w -> w.contentType,
            REMOTE_ADDRESS_KEY, w -> w.remoteAddress);

    private final HttpMethod method;
    private final URI uri;
    private String query;
    private String content;
    private String contentType;
    private String remoteAddress;
    private String script;

    /**
     * Construct a stub with the method and swivel path where it will live
     *
     * @param method - the Http method the stub responds to
     * @param uri    - the Swivel URI path where the stub will reside
     */
    public When(HttpMethod method, URI uri) {
        this.method = Objects.requireNonNull(method);
        this.uri = Objects.requireNonNull(URI.create(uri.getPath()));
        this.query = uri.getQuery();
    }

    /**
     * JSON representation of the When component
     *
     * @return JSONObject
     */
    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject(Map.of(METHOD_KEY, method.getMethodName()));

            for (Map.Entry<String, Function<When, Object>> entry : OPTIONAL_VALUES.entrySet()) {
                jsonObject.putOpt(entry.getKey(), entry.getValue().apply(this));
            }
            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //<editor-fold desc="builder">

    /**
     * Builder pattern - set the content field and return <code>this</code>
     *
     * @param content - The content to match
     * @return <code>this</code>
     */
    public When withContent(String content) {
        setContent(content);
        return this;
    }

    /**
     * Builder pattern - set the query string field and return <code>this</code>
     *
     * @param query - the query to match
     * @return <code>this</code>
     */
    public When withQuery(String query) {
        setQuery(query);
        return this;
    }

    /**
     * Builder pattern - set the contentType field and return <code>this</code>
     *
     * @param contentType - the contentType to match
     * @return <code>this</code>
     */
    public When as(String contentType) {
        setContentType(contentType);
        return this;
    }

    /**
     * Builder pattern - set the remoteAddress field and return <code>this</code>
     *
     * @param remoteAddress - the remoteAddress to match
     * @return <code>this</code>
     */
    public When from(String remoteAddress) {
        setRemoteAddress(remoteAddress);
        return this;
    }

    //YELLOWTAG:TJH - Contemplating either 'helpfully' replacing '\\' with '\\\\', or
    //at least issuing a warning. Modifying data on a setter is full of problems,
    //but so is the nasty double-interpreted strings that will be coming through here.

    /**
     * Builder pattern - set the script to execute to determine matching and return <code>this</code>
     *
     * @param script - the script to execute for matching
     * @return <code>this</code>
     */
    public When matches(String script) {
        setScript(script);
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
                && !(script != null ? !script.equals(when.script) : when.script != null)
                && !(query != null ? !query.equals(when.query) : when.query != null);
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        result = 31 * result + (remoteAddress != null ? remoteAddress.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
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

    public String getQuery() { return query; }

    public void setQuery(String query) { this.query = query; }
    //</editor-fold>
}
