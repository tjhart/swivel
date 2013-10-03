package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Stub;
import com.tjh.swivel.config.model.Then;
import com.tjh.swivel.config.model.When;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StubConfigurerTest {

    private static final URI SOME_URI = URI.create("some/path");
    private StubConfigurer stubConfigurer;
    private SwivelConfigurer mockSwivelConfigurer;
    private When mockWhen;
    private Then mockThen;

    @Before
    public void setUp(){
        mockSwivelConfigurer = mock(SwivelConfigurer.class);
        mockWhen = mock(When.class);
        mockThen = mock(Then.class);

        when(mockWhen.getUri()).thenReturn(SOME_URI);

        stubConfigurer = new StubConfigurer(mockSwivelConfigurer, mockWhen);
    }

    @Test
    public void constructorTakesConfigurerAndWhen(){

        assertThat(stubConfigurer.getSwivelConfigurer(), equalTo(mockSwivelConfigurer));
        assertThat(stubConfigurer.getWhen(), equalTo(mockWhen));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setWhenVerifiesWhen(){
        stubConfigurer.setWhen(null);
    }

    @Test
    public void thenCapturesThen() throws IOException {
        stubConfigurer.thenReturn(mockThen);

        assertThat(stubConfigurer.getThen(), equalTo(mockThen));
    }

    @Test
    public void thenDefersToConfigurer() throws IOException {
        stubConfigurer.thenReturn(mockThen);

        verify(mockSwivelConfigurer).configure(new Stub(mockWhen, mockThen));
    }
}
