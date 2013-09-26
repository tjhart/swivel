package com.tjh.swivel.config;

import com.tjh.swivel.config.model.When;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static com.tjh.swivel.config.Swivel.post;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class StubConfigurerTest {

    private StubConfigurer stubConfigurer;
    private SwivelConfigurer mockSwivelConfigurer;
    private When mockWhen;

    @Before
    public void setUp(){
        mockSwivelConfigurer = mock(SwivelConfigurer.class);
        mockWhen = mock(When.class);

        stubConfigurer = new StubConfigurer(mockSwivelConfigurer, mockWhen);
    }

    @Test
    public void constructorTakesConfigurerAndWhen(){

        assertThat(stubConfigurer.getSwivelConfigurer(), equalTo(mockSwivelConfigurer));
        assertThat(stubConfigurer.getWhen(), equalTo(mockWhen));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWhenVerifiesURI(){
        stubConfigurer.setWhen(post("data"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWhenVerifiesURIIsNotEmpty() throws URISyntaxException {
        stubConfigurer.setWhen(post("data").at(""));
    }
}
