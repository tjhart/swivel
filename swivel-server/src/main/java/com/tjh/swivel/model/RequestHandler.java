package com.tjh.swivel.model;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

public interface RequestHandler {
    //YELLOWTAG:TJH - should this be a raw HttpServletRequest instead?
    HttpResponse handle(HttpUriRequest request);
}
