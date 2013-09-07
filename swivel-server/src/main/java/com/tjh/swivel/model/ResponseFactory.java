package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ResponseFactory {

    public static final String STATUS_CODE_KEY = "statusCode";
    public static final String REASON_KEY = "reason";
    public static final String ENTITY_KEY = "entity";
    public static final String CONTENT_TYPE_KEY = "contentType";

    public HttpResponse createResponse(int code, String reason, String stringEntity, String contentType) {
        try {
            BasicHttpResponse result = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, code, reason));
            if (stringEntity != null) {
                StringEntity entity = new StringEntity(stringEntity, "UTF-8");
                entity.setContentType(contentType);
                result.setEntity(entity);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse createResponse(Map<String, Object> map) {
        return createResponse(((Number) map.get(STATUS_CODE_KEY)).intValue(),
                (String) map.get(REASON_KEY),
                (String) map.get(ENTITY_KEY),
                (String) map.get(CONTENT_TYPE_KEY));
    }

    public HttpResponse createResponse(int code, String reason) { return createResponse(code, reason, null, null); }
}
