package com.tjh.swivel.config.model;

public enum HttpMethod {
    GET("GET", false), PUT("PUT"), POST("POST"), DELETE("DELETE", false);
    private final String methodName;
    private final boolean acceptsData;

    HttpMethod(String methodName) {
        this(methodName, true);
    }

    HttpMethod(String methodName, boolean acceptsData) {
        this.methodName = methodName;
        this.acceptsData = acceptsData;
    }

    public String getMethodName() { return methodName; }

    public boolean isAcceptsData() { return acceptsData; }
}
