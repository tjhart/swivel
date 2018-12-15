package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ContentTypeMatcherTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpEntityEnclosingRequestBase mockRequest;

    @Test
    public void contentTypeMatches() {

        when(mockRequest.getEntity()).thenReturn(new StringEntity("content", ContentType.APPLICATION_XML));
        assertThat(mockRequest, hasContentType(containsString("application/xml")));
    }
}
