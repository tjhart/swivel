package com.tjh.swivel.config;

import com.tjh.swivel.config.model.HttpMethod;
import com.tjh.swivel.config.model.HttpResponseCode;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;

import java.net.URI;
import java.net.URISyntaxException;

public class Swivel {
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_URL_ENCODED_FORM = "application/x-www-form-urlencoded";

    public static When get(URI uri) throws URISyntaxException { return new When(HttpMethod.GET, uri); }

    public static When put(URI uri) { return new When(HttpMethod.PUT, uri); }

    public static When post(URI uri) { return new When(HttpMethod.POST, uri); }

    public static When delete(URI uri) throws URISyntaxException { return new When(HttpMethod.DELETE, uri); }

    public static Then responseCode(HttpResponseCode responseCode) { return new Then(responseCode); }

    public static Then execute(String script) { return new Then(script); }

    public static Then ok() { return new Then(HttpResponseCode.OK); }

    public static Then created() { return new Then(HttpResponseCode.CREATED); }

    public static Then accepted() { return new Then(HttpResponseCode.ACCEPTED); }

    public static Then noContent() {return new Then(HttpResponseCode.NO_CONTENT);}

    public static Then badRequest() {return new Then(HttpResponseCode.BAD_REQUEST);}

    public static Then unauthorized() {return new Then(HttpResponseCode.UNAUTHORIZED);}

    public static Then forbidden() {return new Then(HttpResponseCode.FORBIDDEN);}

    public static Then notFound() {return new Then(HttpResponseCode.NOT_FOUND);}

    public static Then internalServerError() {return new Then(HttpResponseCode.INTERNAL_SERVER_ERROR);}
}
