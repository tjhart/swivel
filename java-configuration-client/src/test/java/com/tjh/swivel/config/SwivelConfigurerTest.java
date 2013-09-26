package com.tjh.swivel.config;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SwivelConfigurerTest {

    public static final String SWIVEL_URI = "http://localhost:8080/swivel-server-1.0-SNAPSHOT";
    private SwivelConfigurer swivelConfigurer;

    @Before
    public void setUp() throws Exception {
        swivelConfigurer = new SwivelConfigurer(SWIVEL_URI);
    }

    @Test
    public void constructorTakesUrl(){
        assertThat(swivelConfigurer.swivelURI, equalTo(SWIVEL_URI));
    }

    @Test
    public void whenCapturesPreconditions(){
//        swivelConfigurer
//                .when(post("data").to("some/uri"))
//                .then(returnCode(204).reason("OK"));
//        ;
    }
}
