package com.tjh.swivel.config;

import com.tjh.swivel.config.model.HttpMethod;
import com.tjh.swivel.config.model.HttpResponseCode;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;

public class Swivel {

    public static When get() { return new When(HttpMethod.GET); }

    public static When put(String data) {
        return new When(HttpMethod.PUT).withContent(data);
    }

    public static When post(String data) {
        return new When(HttpMethod.POST).withContent(data);
    }

    public static When delete() { return new When(HttpMethod.DELETE); }

    public static Then responseCode(HttpResponseCode responseCode) {
        return new Then(responseCode);
    }

    public static Then execute(String script) {
        return new Then(script);
    }

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
