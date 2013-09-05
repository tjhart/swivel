package com.tjh.swivel.controller;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.core.Response;
import java.io.IOException;

public class JerseyResponseFactory {
    public Response createResponse(HttpResponse response) throws IOException {
        //NOTE:TJH - Note we're not finding URIs in the response body and trying
        //to rewrite them. That's not the purpose here.
        Response.ResponseBuilder builder =
                Response.status(response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            builder.entity(EntityUtils.toByteArray(entity));
        }

        for (Header header : response.getAllHeaders()) {
            builder.header(header.getName(), header.getValue());
        }

        return builder.build();
    }
}
