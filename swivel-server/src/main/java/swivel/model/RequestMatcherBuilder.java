package swivel.model;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static swivel.model.matchers.ContentMatcher.hasContent;
import static swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static swivel.model.matchers.HeaderMatcher.hasHeader;
import static swivel.model.matchers.ParameterMapMatcher.hasParameterMap;
import static swivel.model.matchers.RemoteAddrMatcher.hasRemoteAddr;
import static swivel.model.matchers.RequestMethodMatcher.hasMethod;
import static swivel.model.matchers.RequestedURIPathMatcher.hasURIPath;
import static swivel.model.matchers.ScriptMatcher.scriptMatches;

public class RequestMatcherBuilder {
    public static final String METHOD_KEY = "method";
    public static final String REMOTE_ADDR_KEY = "remoteAddr";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String CONTENT_KEY = "content";
    public static final String SCRIPT_KEY = "when";
    public static final int STATIC_MATCHER_COUNT = 3;
    public static final int OPTIONAL_MATCHER_COUNT = 5;
    protected ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public Matcher<HttpServletRequest> buildMatcher(final HttpServletRequest expectationRequest)
            throws IOException {
        Map<String, Object> json = objectMapper.readValue(expectationRequest.getInputStream(), Map.class);

        List<Matcher<HttpServletRequest>> matchers = new ArrayList<Matcher<HttpServletRequest>>(STATIC_MATCHER_COUNT);
        matchers.add(hasMethod(equalTo(json.get(METHOD_KEY))));
        matchers.add(hasURIPath(equalTo(expectationRequest.getPathInfo())));

        matchers.add(buildOptionalMatcher(expectationRequest, json));
        return allOf(matchers.toArray(new Matcher[matchers.size()]));
    }

    @SuppressWarnings("unchecked")
    protected Matcher<HttpServletRequest> buildOptionalMatcher(HttpServletRequest expectationRequest,
            Map<String, Object> json) {
        try {
            List<Matcher<HttpServletRequest>> result =
                    new ArrayList<Matcher<HttpServletRequest>>(OPTIONAL_MATCHER_COUNT);
            Map<String, String[]> parameterMap = expectationRequest.getParameterMap();
            if (!parameterMap.isEmpty()) {
                result.add(hasParameterMap(equalTo(convertParameterMap(parameterMap))));
            }
            if (json.containsKey(REMOTE_ADDR_KEY)) {
                String remoteAddr = (String) json.get(REMOTE_ADDR_KEY);
                result.add(
                        anyOf(hasRemoteAddr(equalTo(remoteAddr)), hasHeader("X-Forwarded-For", hasItem(remoteAddr))));
            }
            if (json.containsKey(CONTENT_TYPE_KEY)) {
                result.add(hasContentType(equalTo(json.get(CONTENT_TYPE_KEY))));
            }
            if (json.containsKey(CONTENT_KEY)) {
                result.add(hasContent(equalTo(json.get(CONTENT_KEY))));
            }
            if (json.containsKey(SCRIPT_KEY)) {
                result.add(scriptMatches((String) json.get(SCRIPT_KEY)));
            }
            return allOf(result.toArray(new Matcher[result.size()]));
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, List<String>> result = new HashMap<String, List<String>>(parameterMap.size());
        for (Map.Entry<String, String[]> stringEntry : parameterMap.entrySet()) {
            result.put(stringEntry.getKey(), Arrays.asList(stringEntry.getValue()));
        }
        return result;
    }
}
