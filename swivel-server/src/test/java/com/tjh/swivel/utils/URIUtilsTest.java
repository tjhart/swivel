package com.tjh.swivel.utils;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class URIUtilsTest {

    public static final URI BASE_URI = URI.create("https://episerviceint.mc.vanderbilt.edu/epi5_3/request");
    public static final URI MATCHED_URI = URI.create("epi5_3/request");

    @Test
    public void resolveURIWorksAsExpected() {
        URI requestedPath = URI.create("epi5_3/request/foo");

        assertThat(URIUtils.resolveUri(BASE_URI, requestedPath, MATCHED_URI),
                equalTo(URI.create("https://episerviceint.mc.vanderbilt.edu/epi5_3/request/foo")));
    }

    @Test
    public void resolveURIIncludesQuery(){
        URI requestedUri = URI.create("epi5_3/request?queryString");


        assertThat(URIUtils.resolveUri(BASE_URI, requestedUri, MATCHED_URI).getQuery(),
                equalTo("queryString"));
    }

}
