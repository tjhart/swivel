package com.tjh.swivel.controller.functional;

import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
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
        ShuntRequestHandler requestHandler = new ShuntRequestHandler(URI.create("http://localhost:5984"));
        requestRouter.setShunt(URI.create("couch"), requestHandler);
    }

    @Test
    public void workRedirectsRequest() throws IOException {
        HttpRequestBase request = new HttpGet("couch");
        HttpResponse httpResponse = requestRouter.route(request);

        assertThat(httpResponse.getStatusLine().getStatusCode() / 100, equalTo(2));
        String entity = EntityUtils.toString(httpResponse.getEntity());
        System.out.println("entity = " + entity);
    }
}
