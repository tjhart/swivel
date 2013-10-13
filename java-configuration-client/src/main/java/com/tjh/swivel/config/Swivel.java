package com.tjh.swivel.config;

import com.tjh.swivel.config.model.HttpMethod;
import com.tjh.swivel.config.model.HttpResponseCode;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;

import java.net.URI;

/**
 * Convenience methods for configuring Swivel
 */
public class Swivel {
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_URL_ENCODED_FORM = "application/x-www-form-urlencoded";

    /**
     * Create a When component that will match a GET request for the uri parameter
     *
     * @param uri - uri where a stub will respond
     * @return When
     */
    public static When get(URI uri) { return new When(HttpMethod.GET, uri); }

    /**
     * Create a When component that will match a PUT request for the uri parameter
     *
     * @param uri - uri where a stub will respond
     * @return When
     */
    public static When put(URI uri) { return new When(HttpMethod.PUT, uri); }

    /**
     * Create a When component that will match a POST request for the uri parameter
     *
     * @param uri - uri where a stub will respond
     * @return When
     */
    public static When post(URI uri) { return new When(HttpMethod.POST, uri); }

    /**
     * Create a When component that will match a DELETE request for the uri parameter
     *
     * @param uri - uri where a stub will respond
     * @return When
     */
    public static When delete(URI uri) { return new When(HttpMethod.DELETE, uri); }

    /**
     * Create a Then component that will return the given <code>responseCode</code>
     *
     * @param responseCode HttpResponseCode
     * @return Then
     */
    public static Then responseCode(HttpResponseCode responseCode) { return new Then(responseCode); }

    /**
     * Create a Then component that will return the results of the <code>script</code>
     *
     * @param script script
     * @return Then
     */
    public static Then execute(String script) { return new Then(script); }

    /**
     * Create a Then component that will return 200 (OK)
     *
     * @return Then
     */
    public static Then ok() { return new Then(HttpResponseCode.OK); }

    /**
     * Create a Then component that will return 201 (CREATED)
     *
     * @return Then
     */
    public static Then created() { return new Then(HttpResponseCode.CREATED); }

    /**
     * Create a Then component that will return 202 (ACCEPTED)
     *
     * @return Then
     */
    public static Then accepted() { return new Then(HttpResponseCode.ACCEPTED); }

    /**
     * Create a Then component that will return 204 (NO CONTENT)
     *
     * @return Then
     */
    public static Then noContent() {return new Then(HttpResponseCode.NO_CONTENT);}

    /**
     * Create a Then component that will return 400 (BAD REQUEST)
     *
     * @return Then
     */
    public static Then badRequest() {return new Then(HttpResponseCode.BAD_REQUEST);}

    /**
     * Create a Then component that will return 401 (UNAUTHORIZED)
     *
     * @return Then
     */
    public static Then unauthorized() {return new Then(HttpResponseCode.UNAUTHORIZED);}

    /**
     * Create a Then component that will return 403 (FORBIDDEN)
     *
     * @return Then
     */
    public static Then forbidden() {return new Then(HttpResponseCode.FORBIDDEN);}

    /**
     * Create a Then component that will return 404 (NOT FOUND)
     *
     * @return Then
     */
    public static Then notFound() {return new Then(HttpResponseCode.NOT_FOUND);}

    /**
     * Create a Then component that will return 500 (INTERNAL SERVER ERROR)
     *
     * @return Then
     */
    public static Then internalServerError() {return new Then(HttpResponseCode.INTERNAL_SERVER_ERROR);}
}
