package com.tjh.swivel.model;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.script.ScriptException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class Configuration {
    public static final String SHUNT_NODE = "^shunt";
    public static final String STUB_NODE = "^stub";
    public static final String STUBS_MAP_KEY = "stubs";
    public static final String SHUNT_MAP_KEY = "shunt";

    public static Logger LOGGER = Logger.getLogger(Configuration.class);

    protected final Map<String, Map<String, Object>> uriHandlers = new ConcurrentHashMap<>();

    //<editor-fold desc="query">
    public RequestHandler findRequestHandler(HttpRequestBase request, String matchedPath) {
        RequestHandler result = null;
        LOGGER.debug("checking for handlers at " + matchedPath);
        if (uriHandlers.containsKey(matchedPath)) {
            Map<String, Object> stringObjectMap = uriHandlers.get(matchedPath);
            result = findStub(stringObjectMap, request);
            if (result == null) {
                if (stringObjectMap.containsKey(Configuration.SHUNT_NODE)) {
                    result = (RequestHandler) stringObjectMap.get(Configuration.SHUNT_NODE);
                    if (result != null) {
                        LOGGER.debug("found shunt " + result);
                    }
                }
            } else {
                LOGGER.debug("found stub " + result);
            }
        }
        return result;
    }

    RequestHandler findStub(Map<String, Object> stringObjectMap, final HttpUriRequest request) {
        return ((Collection<StubRequestHandler>) stringObjectMap.get(Configuration.STUB_NODE))
                .stream()
                .filter(requestHandler -> {
                    if (Level.DEBUG.equals(LOGGER.getEffectiveLevel())) {
                        LOGGER.debug("Checking Stub " + requestHandler);
                    }
                    return requestHandler.matches(request);
                })
                .findFirst()
                .orElse(null);
    }

    public Collection<StubRequestHandler> getStubs(String localPath, final List<Integer> stubIds) {
        return getStubRequestHandlers(localPath)
                .stream()
                .filter(stubRequestHandler -> stubIds.isEmpty() || stubIds.contains(stubRequestHandler.getId()))
                .collect(Collectors.toList());
    }
    //</editor-fold>

    //<editor-fold desc="modify">
    public void removeStub(URI localUri, final int stubHandlerId) {
        String path = localUri.getPath();
        List<StubRequestHandler> handlers = getStubRequestHandlers(path);
        System.out.println("handlers = " + handlers);

        StubRequestHandler target = handlers.stream()
                .filter(stubRequestHandler -> stubRequestHandler.getId() == stubHandlerId)
                .findFirst()
                .orElse(null);
        LOGGER.debug(String.format("Removing <%1$s> from path <%2$s>", target, localUri));
        handlers.remove(target);
        target.releaseResources();

        if (handlers.isEmpty()) {
            clean(path, uriHandlers.computeIfAbsent(path, this::newConcurrentHashMap), Configuration.STUB_NODE);
        }
    }

    public void setShunt(URI localURI, ShuntRequestHandler requestHandler) {
        LOGGER.debug(String.format("Setting shunt <%1$s> at <%2$s>", requestHandler, localURI));
        uriHandlers.computeIfAbsent(localURI.getPath(), this::newConcurrentHashMap)
                .put(Configuration.SHUNT_NODE, requestHandler);
    }

    public void addStub(URI localUri, StubRequestHandler stubRequestHandler) {
        LOGGER.debug(String.format("Adding stub <%1$s> to <%2$s>", stubRequestHandler, localUri));
        getStubRequestHandlers(localUri.getPath())
                .add(0, stubRequestHandler);
    }

    public void deleteShunt(URI localURI) {
        String path = localURI.getPath();
        clean(path, uriHandlers.computeIfAbsent(path, this::newConcurrentHashMap), Configuration.SHUNT_NODE);
    }

    public void replaceStub(URI localURI, int stubId, StubRequestHandler stubRequestHandler) {
        List<StubRequestHandler> stubRequestHandlers = getStubRequestHandlers(localURI.getPath());
        int stubIndex = -1;
        for (int i = 0; i < stubRequestHandlers.size(); i++) {
            StubRequestHandler requestHandler = stubRequestHandlers.get(i);
            if (requestHandler.getId() == stubId) {
                stubIndex = i;
                break;
            }
        }

        stubRequestHandlers
                .set(stubIndex, stubRequestHandler)
                .releaseResources();
    }

    public void removePath(URI localUri) {
        removePath(uriHandlers, localUri.toString());
    }

    private void removePath(Map<String, Map<String, Object>> uriHandlers, String key) {
        for (StubRequestHandler stub : getStubRequestHandlers(uriHandlers.remove(key))) {
            stub.releaseResources();
        }
    }

    public void reset() {
        HashMap<String, Map<String, Object>> oldMap = new HashMap<>(uriHandlers);
        uriHandlers.clear();
        for (String path : new HashSet<>(oldMap.keySet())) {
            removePath(oldMap, path);
        }
    }
    //</editor-fold>

    //<editor-fold desc="marshalling">
    public void load(Map<String, Map<String, Object>> configMap) {
        final Map<String, Map<String, Object>> newConfig = new HashMap<>(configMap.size());
        configMap.forEach((uri, shuntsAndStubs) -> {
            Map<String, Object> nodeMap = newConfig.computeIfAbsent(uri, this::newConcurrentHashMap);
            loadShunt((Map<String, String>) shuntsAndStubs.get(Configuration.SHUNT_MAP_KEY), nodeMap);
            loadStubs((List<Map<String, Object>>) shuntsAndStubs.get(Configuration.STUBS_MAP_KEY), nodeMap);
        });
        synchronized (this) {
            reset();
            uriHandlers.putAll(newConfig);
        }
    }

    public Map<String, Map<String, Object>> toMap() {
        final HashMap<String, Map<String, Object>> result = new HashMap<>(uriHandlers.size());

        uriHandlers.forEach((path, handlerMap) -> {
            Map<String, Object> stubsAndShunt = new HashMap<>();
            if (!getStubRequestHandlers(handlerMap).isEmpty()) {
                stubsAndShunt.put(STUBS_MAP_KEY,
                        getStubRequestHandlers(handlerMap).stream()
                                .map(RequestHandler::toMap)
                                .collect(Collectors.toList()));
            }
            if (handlerMap.containsKey(SHUNT_NODE)) {
                stubsAndShunt.put(SHUNT_MAP_KEY, ((ShuntRequestHandler) handlerMap.get(SHUNT_NODE)).toMap());
            }
            result.put(path, stubsAndShunt);
        });

        return result;
    }
    //</editor-fold>

    protected Map<String, Object> newConcurrentHashMap(Object ignored) {
        return new ConcurrentHashMap<>();
    }

    protected List<StubRequestHandler> getStubRequestHandlers(Map<String, Object> node) {
        return (List<StubRequestHandler>) node.computeIfAbsent(STUB_NODE, i -> new CopyOnWriteArrayList<StubRequestHandler>());
    }

    protected List<StubRequestHandler> getStubRequestHandlers(String path) {
        return getStubRequestHandlers(uriHandlers.computeIfAbsent(path, this::newConcurrentHashMap));
    }

    void clean(String path, Map<String, Object> handlerMap, String nodeType) {
        Object shuntRequestHandler = handlerMap.remove(nodeType);
        if (handlerMap.containsKey(STUB_NODE)) {
            List stubs = getStubRequestHandlers(handlerMap);
            if (stubs.isEmpty()) {
                handlerMap.remove(STUB_NODE);
            }
        }
        if (handlerMap.isEmpty()) {
            uriHandlers.remove(path);
        }

        LOGGER.debug(String.format("Removed <%1$s> from <%2$s>", shuntRequestHandler, path));
    }

    private void loadStubs(List<Map<String, Object>> stubDescriptions, Map<String, Object> nodeMap) {
        if (stubDescriptions != null && !stubDescriptions.isEmpty()) {
            getStubRequestHandlers(nodeMap)
                    .addAll(stubDescriptions.stream()
                            .map(this::createStubFor)
                            .collect(Collectors.toList()));
        }
    }

    protected StubRequestHandler createStubFor(Map<String, Object> stubDescription) {
        try {
            return AbstractStubRequestHandler.createStubFor(stubDescription);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadShunt(Map<String, String> shuntDescription, Map<String, Object> nodeMap) {
        try {
            if (shuntDescription != null) {
                nodeMap.put(Configuration.SHUNT_NODE, new ShuntRequestHandler(shuntDescription));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
