package com.tjh.swivel.model.matchers;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import static com.tjh.swivel.model.matchers.ContentTypeMatcher.hasContentType;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentTypeMatcherTest {

    @Test
    public void contentTypeMatches() {
        HttpEntityEnclosingRequestBase mockRequest = mock(HttpEntityEnclosingRequestBase.class);

        when(mockRequest.getEntity()).thenReturn(new StringEntity("content", ContentType.APPLICATION_XML));
        assertThat(mockRequest, hasContentType(containsString("application/xml")));
    }
}
