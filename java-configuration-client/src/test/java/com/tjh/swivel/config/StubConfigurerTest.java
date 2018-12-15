package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class StubConfigurerTest {

    private static final URI SOME_URI = URI.create("some/path");
    public static final String DESCRIPTION = "fred";
    private StubConfigurer stubConfigurer;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private SwivelConfigurer mockSwivelConfigurer;
    @Mock
    private When mockWhen;
    @Mock
    private Then mockThen;

    @Before
    public void setUp(){
        when(mockWhen.getUri()).thenReturn(SOME_URI);

        stubConfigurer = new StubConfigurer(mockSwivelConfigurer, mockWhen);
    }

    @Test
    public void constructorTakesConfigurerAndWhen(){

        assertThat(stubConfigurer.getSwivelConfigurer(), equalTo(mockSwivelConfigurer));
        assertThat(stubConfigurer.getWhen(), equalTo(mockWhen));
    }

    @Test(expected = NullPointerException.class)
    public void setWhenVerifiesWhen(){
        stubConfigurer.setWhen(null);
    }

    @Test
    public void thenCapturesThen() {
        stubConfigurer.then(mockThen);

        assertThat(stubConfigurer.getThen(), equalTo(mockThen));
    }

    @Test
    public void describeCaptureDescription(){
        stubConfigurer.describe(DESCRIPTION);

        assertThat(stubConfigurer.getDescription(), equalTo(DESCRIPTION));
    }
}
