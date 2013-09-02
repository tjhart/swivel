package com.tjh.swivel.controller.functional;

import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@ContextConfiguration(locations = {"classpath:routerContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RequestRouterTest {

    @Autowired
    RequestRouter requestRouter;

    @Before
    public void before() {
        ShuntRequestHandler requestHandler = new ShuntRequestHandler(URI.create("http://www.google.com"));
        requestRouter.setShunt(URI.create("google"), requestHandler);
    }

    @Test
    public void workRedirectsRequest() throws IOException {
        HttpUriRequest request = new HttpGet("google");
        HttpResponse httpResponse = requestRouter.work(request);

        assertThat(httpResponse.getStatusLine().getStatusCode()/100, equalTo(2));
        String entity = EntityUtils.toString(httpResponse.getEntity());
        System.out.println("entity = " + entity);
    }
}
