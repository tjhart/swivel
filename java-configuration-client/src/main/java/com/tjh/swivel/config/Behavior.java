package com.tjh.swivel.config;

import org.apache.http.client.methods.HttpUriRequest;

import java.net.URL;

public interface Behavior {
    HttpUriRequest toRequest(URL baseURL);
}
