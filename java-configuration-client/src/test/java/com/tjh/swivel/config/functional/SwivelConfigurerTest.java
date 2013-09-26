package com.tjh.swivel.config.functional;

import com.tjh.swivel.config.SwivelConfigurer;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.tjh.swivel.config.Swivel.get;
import static com.tjh.swivel.config.Swivel.ok;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SwivelConfigurerTest {

    public static final String SWIVEL_BASE_URL = "http://localhost:8080/swivel-server-1.0-SNAPSHOT";
    public static final String SWIVEL_PROXY_URL = SWIVEL_BASE_URL + "/rest/proxy";
    public static final String CONTENT = "content";

    @Test
    public void configureStubWorks() throws IOException, URISyntaxException {
        SwivelConfigurer swivelConfigurer = new SwivelConfigurer(SWIVEL_BASE_URL);

        swivelConfigurer
                .when(get("foo"))
                .thenReturn(ok()
                .withContent(CONTENT));

        String entity = EntityUtils.toString(
                new DefaultHttpClient()
                        .execute(new HttpGet(SWIVEL_PROXY_URL + "/foo"))
                        .getEntity());

        assertThat(entity, equalTo(CONTENT));
    }
}
