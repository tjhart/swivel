package com.tjh.swivel.model.matchers;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;

import static com.tjh.swivel.model.matchers.ContentMatcher.hasContent;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ContentMatcherTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private HttpEntityEnclosingRequestBase mockEntityEnclosingRequest;

    @Test
    public void contentMatches() throws IOException {
        String content = "My happy content";
        HttpEntity mockEntity = new StringEntity(content);
        when(mockEntityEnclosingRequest.getEntity()).thenReturn(mockEntity);

        assertThat(mockEntityEnclosingRequest, hasContent(equalTo(content)));
    }
}
