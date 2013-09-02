package com.tjh.swivel.controller.functional;

import com.tjh.swivel.controller.ConfigurationResource;
import com.tjh.swivel.controller.RequestRouter;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import vanderbilt.util.Maps;

import java.net.URI;
import java.net.URISyntaxException;

@ContextConfiguration(locations = {"classpath:serverContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurationResourceTest {

    @Autowired
    protected ConfigurationResource configurationResource;
    @Autowired
    protected RequestRouter requestRouter;

    @Before
    public void before() throws URISyntaxException {
        configurationResource.putShunt("google",
                Maps.asMap(ConfigurationResource.REMOTE_URI_KEY, "http://www.google.com"));
    }

    @Test
    public void shuntWorks(){
        requestRouter.work(new HttpGet(URI.create("google")));
        //would throw if failed
    }

    @Test
    public void shuntThrowsOnUnknownURL() throws URISyntaxException {
        configurationResource.deleteShunt("google");
        //would throw if failed
    }
}
