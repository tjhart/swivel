package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SwivelConfigurerTest {

    public static final String SWIVEL_URI = "http://localhost:8080/swivel-server-1.0-SNAPSHOT";
    public static final URI SOME_URI = URI.create("some/path");
    private SwivelConfigurer swivelConfigurer;
    private When mockWhen;

    @Before
    public void setUp() throws Exception {
        swivelConfigurer = new SwivelConfigurer(SWIVEL_URI);
        mockWhen = mock(When.class);

        when(mockWhen.getUri()).thenReturn(SOME_URI);
    }

    @Test
    public void constructorTakesUrl() throws MalformedURLException {
        assertThat(swivelConfigurer.swivelURI, equalTo(new URL(SWIVEL_URI)));
    }

    @Test
    public void whenReturnsNewStubConfiguration(){
        StubConfigurer stubConfigurer = swivelConfigurer.when(mockWhen);

        assertThat(stubConfigurer, equalTo(new StubConfigurer(swivelConfigurer, mockWhen)));
    }
}
