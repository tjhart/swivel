package com.tjh.swivel.integration;

import com.tjh.swivel.config.SwivelConfigurer;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.tjh.swivel.config.Swivel.APPLICATION_URL_ENCODED_FORM;
import static com.tjh.swivel.config.Swivel.TEXT_PLAIN;
import static com.tjh.swivel.config.Swivel.get;
import static com.tjh.swivel.config.Swivel.ok;
import static com.tjh.swivel.config.Swivel.post;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SwivelConfigurerTest {

    public static final String SWIVEL_BASE_URL = "http://localhost:8080/swivel_server_war_exploded";
    public static final String SWIVEL_PROXY_URL = SWIVEL_BASE_URL + "/rest/proxy";
    public static final String CONTENT = "content";
    public static final URI PATH = URI.create("some/path");

    private SwivelConfigurer swivelConfigurer;
    private BasicHttpClientConnectionManager clientConnectionManager;

    @Before
    public void setUp() throws Exception {
        swivelConfigurer = new SwivelConfigurer(SWIVEL_BASE_URL);

        clientConnectionManager = new BasicHttpClientConnectionManager();
        swivelConfigurer.setClientConnectionManager(clientConnectionManager);
    }

    @After
    public void after() throws IOException {
        swivelConfigurer.reset();
    }

    @Test
    public void configureStubWorks() throws IOException, URISyntaxException {

        swivelConfigurer
                .when(get(PATH))
                .then(ok()
                        .withContent(CONTENT)
                        .as(TEXT_PLAIN))
                .describe("get path returns content")
                .configure();

        String entity = EntityUtils.toString(getEntity(new HttpGet(SWIVEL_PROXY_URL + "/" + PATH)));

        assertThat(entity, equalTo(CONTENT));
    }

    //integration tests
    @Test
    public void matchingContentWorks() throws IOException, URISyntaxException {
        swivelConfigurer
                .when(post(PATH)
                        .withContent("some data"))
                .then(ok()
                        .withContent("you matched data")
                        .as(TEXT_PLAIN))
                .describe("match some data on post")
                .configure();
        swivelConfigurer
                .when(post(PATH)
                        .withContent("some other data"))
                .then(ok()
                        .withContent("you matched some other data")
                        .as(TEXT_PLAIN))
                .describe("match some other data on post")
                .configure();

        HttpPost httpPost = new HttpPost(SWIVEL_PROXY_URL + "/" + PATH);
        httpPost.setEntity(new StringEntity("some data"));
        String response = EntityUtils.toString(getEntity(httpPost));

        assertThat(response, equalTo("you matched data"));

        httpPost.setEntity(new StringEntity("some other data"));
        response = EntityUtils.toString(getEntity(httpPost));

        assertThat(response, equalTo("you matched some other data"));
    }

    private HttpEntity getEntity(HttpUriRequest httpPost) throws IOException {
        return HttpClientBuilder.create()
                .setConnectionManager(clientConnectionManager)
                .build()
                .execute(httpPost)
                .getEntity();
    }

    @Test
    public void scriptMatchingWorks() throws URISyntaxException, IOException {
        swivelConfigurer
                .when(post(PATH)
                        .as(APPLICATION_URL_ENCODED_FORM)
                        .matches("(function(request){" +
                                "   return Packages.org.apache.http.util.EntityUtils\n" +
                                "       .toString(request.getEntity())\n" +
                                "       .matches(\"foo=bar\");\n" +
                                "})(request);"))
                .then(ok()
                        .withContent("it matched!")
                        .as(TEXT_PLAIN))
                .describe("Script matcher")
                .configure();

        HttpPost httpPost = new HttpPost(SWIVEL_PROXY_URL + "/" + PATH);
        httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("foo", "bar"))));
        String response = EntityUtils.toString(getEntity(httpPost));
        assertThat(response, equalTo("it matched!"));
    }

    @Test
    public void stubWithFileConfigurationWorks() throws IOException {
        File testFile = new File(System.getProperty("java.io.tmpdir") + "/testFile.tmp");
        FileWriter fileWriter = new FileWriter(testFile);
        fileWriter.write("test content");
        fileWriter.close();
        try {
            swivelConfigurer
                    .when(get(PATH))
                    .then(ok()
                            .withFile(testFile)
                            .as(TEXT_PLAIN))
                    .describe("test configure stub with file")
                    .configure();

            assertThat(EntityUtils.toString(getEntity(new HttpGet(SWIVEL_PROXY_URL + "/" + PATH))),
                    equalTo("test content"));
        } finally {
            if (!testFile.delete()) {
                System.out.println("WARNING! Could not delete " + testFile.getPath());
            }
        }
    }
}
