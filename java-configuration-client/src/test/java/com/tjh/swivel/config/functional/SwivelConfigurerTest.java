package com.tjh.swivel.config.functional;

import com.tjh.swivel.config.SwivelConfigurer;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tjh.swivel.config.Swivel.APPLICATION_URL_ENCODED_FORM;
import static com.tjh.swivel.config.Swivel.get;
import static com.tjh.swivel.config.Swivel.ok;
import static com.tjh.swivel.config.Swivel.post;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SwivelConfigurerTest {

    public static final String SWIVEL_BASE_URL = "http://localhost:8080/swivel_server_war_exploded";
    public static final String SWIVEL_PROXY_URL = SWIVEL_BASE_URL + "/rest/proxy";
    public static final String CONTENT = "content";
    public static final String PATH = "some/path";

    private List<Integer> stubIDs = new ArrayList<Integer>();
    private SwivelConfigurer swivelConfigurer;

    @Before
    public void setUp() throws Exception {
        swivelConfigurer = new SwivelConfigurer(SWIVEL_BASE_URL);

        swivelConfigurer.setClientConnectionManager(new PoolingClientConnectionManager());
    }

    @After
    public void after() throws IOException {
        for (Integer stubID : stubIDs) {
            swivelConfigurer
                    .deleteStub(PATH, stubID);
        }
    }

    @Test
    public void configureStubWorks() throws IOException, URISyntaxException {

        stubIDs.add(swivelConfigurer
                .when(get(PATH))
                .thenReturn(ok()
                        .withContent(CONTENT)));

        String entity = EntityUtils.toString(
                new DefaultHttpClient().execute(new HttpGet(SWIVEL_PROXY_URL + "/" + PATH))
                        .getEntity());

        assertThat(entity, equalTo(CONTENT));
    }

    //integration tests
    @Test
    public void matchingContentWorks() throws IOException, URISyntaxException {
        stubIDs.add(swivelConfigurer
                .when(post("some data")
                        .at(PATH))
                .thenReturn(ok()
                        .withContent("you matched data")));
        stubIDs.add(swivelConfigurer
                .when(post("some other data")
                        .at(PATH))
                .thenReturn(ok()
                        .withContent("you matched some other data")));

        HttpPost httpPost = new HttpPost(SWIVEL_PROXY_URL + "/" + PATH);
        httpPost.setEntity(new StringEntity("some data"));
        String response = EntityUtils.toString(new DefaultHttpClient()
                .execute(httpPost)
                .getEntity());

        assertThat(response, equalTo("you matched data"));

        httpPost.setEntity(new StringEntity("some other data"));
        response = EntityUtils.toString(new DefaultHttpClient()
                .execute(httpPost)
                .getEntity());

        assertThat(response, equalTo("you matched some other data"));
    }

    @Test
    public void scriptMatchingWorks() throws URISyntaxException, IOException {
        stubIDs.add(swivelConfigurer
                .when(post()
                        .at(PATH)
                        .as(APPLICATION_URL_ENCODED_FORM)
                        .matches("(function(){" +
                                "   var entity = Packages.org.apache.http.util.EntityUtils" +
                                "       .toString(request.getEntity());\n" +
                                "   java.lang.System.out.println('entity = ' + entity);\n" +
                                "   return entity.matches('foo=bar');\n" +
                                "})();"))
                .thenReturn(ok()
                        .withContent("it matched!")));

        HttpPost httpPost = new HttpPost(SWIVEL_PROXY_URL + "/" + PATH);
        httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("foo", "bar"))));
        String response = EntityUtils.toString(new DefaultHttpClient()
                .execute(httpPost)
                .getEntity());
        assertThat(response, equalTo("it matched!"));
    }
}
