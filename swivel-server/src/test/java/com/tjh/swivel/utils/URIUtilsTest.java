package com.tjh.swivel.utils;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class URIUtilsTest {

    @Test
    public void resolveURIWorksAsExpected() {
        URI baseURI = URI.create("https://episerviceint.mc.vanderbilt.edu/epi5_3/request");
        URI requestedPath = URI.create("epi5_3/request/foo");
        URI matchedURI = URI.create("epi5_3/request");

        assertThat(URIUtils.resolveUri(baseURI, requestedPath, matchedURI),
                equalTo(URI.create("https://episerviceint.mc.vanderbilt.edu/epi5_3/request/foo")));
    }

}
