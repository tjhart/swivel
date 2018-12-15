package com.tjh.swivel.config;

import com.tjh.swivel.config.model.Shunt;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

public class ShuntConfigurerTest {

    public static final String SOME_PATH = "some/path";
    public static final URI LOCAL_URI = URI.create(SOME_PATH);
    public static final URL REMOTE_URL;

    static{
        try {
            REMOTE_URL = new URL("http://host/path");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private ShuntConfigurer shuntConfigurer;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private SwivelConfigurer mockSwivelConfigurer;

    @Before
    public void setUp() {
        shuntConfigurer = new ShuntConfigurer(mockSwivelConfigurer);
    }

    @Test
    public void constructorInitializesSwivelConfigurer() {
        assertThat(shuntConfigurer.swivelConfigurer, sameInstance(mockSwivelConfigurer));
    }

    @Test
    public void constructorInitializesSwivelConfigurerAndLocalURI() throws URISyntaxException {
        ShuntConfigurer shuntConfigurer = new ShuntConfigurer(mockSwivelConfigurer, SOME_PATH);

        assertThat(shuntConfigurer.swivelConfigurer, sameInstance(mockSwivelConfigurer));
        assertThat(shuntConfigurer.getLocalURI(), equalTo(LOCAL_URI));
    }

    @Test
    public void fromSetsLocalURI() {
        shuntConfigurer.from(LOCAL_URI);

        assertThat(shuntConfigurer.getLocalURI(), sameInstance(LOCAL_URI));
    }

    @Test
    public void fromReturnsConfigurer() {
        assertThat(shuntConfigurer.from(LOCAL_URI), sameInstance(shuntConfigurer));
    }

    @Test
    public void toSetsRemoteURL() {
        shuntConfigurer.setLocalURI(LOCAL_URI);
        shuntConfigurer.to(REMOTE_URL);

        assertThat(shuntConfigurer.getRemoteURL(), sameInstance(REMOTE_URL));
    }

    @Test
    public void configureDefersToSwivelConfigurer() throws IOException {
        shuntConfigurer.setLocalURI(LOCAL_URI);
        shuntConfigurer.setRemoteURL(REMOTE_URL);
        shuntConfigurer.configure();

        verify(mockSwivelConfigurer).configure(new Shunt(LOCAL_URI, REMOTE_URL));
    }
}
