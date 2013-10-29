package com.tjh.swivel.controller;

class RequestPathParser {
    private String localPath;
    private int stubId;

    public RequestPathParser(String localPath) {
        int lastElementIndex = localPath.lastIndexOf('/');
        stubId = Integer.valueOf(localPath.substring(lastElementIndex + 1));
        this.localPath = localPath.substring(0, lastElementIndex);
    }

    public String getLocalPath() { return localPath; }

    public int getStubId() { return stubId; }
}
