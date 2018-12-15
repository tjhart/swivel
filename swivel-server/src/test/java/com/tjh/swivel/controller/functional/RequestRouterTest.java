package com.tjh.swivel.controller.functional;

import com.tjh.swivel.config.RouterConfiguration;
import com.tjh.swivel.controller.RequestRouter;
import com.tjh.swivel.model.Configuration;
import com.tjh.swivel.model.ShuntRequestHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = RouterConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RequestRouterTest {

    @Resource
    RequestRouter requestRouter;
    @Resource
    Configuration configuration;

    @Before
    public void before() throws MalformedURLException {
        ShuntRequestHandler requestHandler =
                new ShuntRequestHandler(Map.of(ShuntRequestHandler.REMOTE_URL_KEY, "http://localhost:5984"));
        configuration.setShunt(URI.create("couch"), requestHandler);
    }

    @Test
    public void workRedirectsRequest() {
        HttpRequestBase request = new HttpGet("couch");
        HttpResponse httpResponse = requestRouter.route(request);

        assertThat(httpResponse.getStatusLine().getStatusCode() / 100, equalTo(2));
    }
}
