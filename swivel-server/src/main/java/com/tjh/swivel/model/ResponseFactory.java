package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.UnsupportedEncodingException;

public class ResponseFactory {

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

    public HttpResponse createResponse(int code, String reason) {
        return createResponse(code, reason, null, null);
    }
}