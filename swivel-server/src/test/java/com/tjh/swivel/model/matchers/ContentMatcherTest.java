package com.tjh.swivel.model.matchers;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.io.IOException;

import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentMatcherTest {

    @Test
    public void contentMatches() throws IOException {
        HttpEntityEnclosingRequestBase mockRequest = mock(HttpEntityEnclosingRequestBase.class);

        String content = "My happy content";
        HttpEntity mockEntity = new StringEntity(content);
        when(mockRequest.getEntity()).thenReturn(mockEntity);

        assertThat(mockRequest, hasContent(equalTo(content)));
    }
}
