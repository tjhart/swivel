package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static com.tjh.swivel.config.Swivel.post;
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
    public void whenCapturesWhen() throws URISyntaxException {
        When when = post("data").to("some/uri");
        swivelConfigurer
                .when(when);

        assertThat(swivelConfigurer.getWhen(), equalTo(when));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWhenVerifiesURI(){
        swivelConfigurer.setWhen(post("data"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWhenVerifiesURIIsNotEmpty() throws URISyntaxException {
        swivelConfigurer.setWhen(post("data").to(""));
    }
}
