package com.tjh.swivel.config;

import com.tjh.swivel.config.model.HttpMethod;
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
}
