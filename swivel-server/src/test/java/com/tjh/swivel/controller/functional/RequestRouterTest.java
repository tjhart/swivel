package com.tjh.swivel.controller.functional;

import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@ContextConfiguration(locations = {"classpath:routerContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RequestRouterTest {

    @Autowired
    RequestRouter requestRouter;
    @Autowired
    Configuration configuration;

    @Before
    public void before() throws MalformedURLException {
        ShuntRequestHandler requestHandler = new ShuntRequestHandler(new URL("http://localhost:5984"));
        configuration.setShunt(URI.create("couch"), requestHandler);
    }

    @Test
    public void workRedirectsRequest() throws IOException {
        HttpRequestBase request = new HttpGet("couch");
        HttpResponse httpResponse = requestRouter.route(request);

        assertThat(httpResponse.getStatusLine().getStatusCode() / 100, equalTo(2));
    }
}
