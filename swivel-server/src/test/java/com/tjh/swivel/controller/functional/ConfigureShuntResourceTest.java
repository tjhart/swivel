package com.tjh.swivel.controller.functional;

import com.tjh.swivel.controller.ConfigureShuntResource;
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
public class ConfigureShuntResourceTest {

    public static final URI LOCAL_URI = URI.create("couch");
    @Autowired
    protected ConfigureShuntResource configurationResource;
    @Autowired
    protected RequestRouter requestRouter;

    @Before
    public void before() throws URISyntaxException {
        configurationResource.putShunt(LOCAL_URI,
                Maps.asMap(ConfigureShuntResource.REMOTE_URL_KEY, "http://localhost:5984"));
    }

    @Test
    public void shuntWorks() {
        requestRouter.route(new HttpGet(URI.create("couch")));
        //would throw if failed
    }

    @Test
    public void shuntThrowsOnUnknownURL() throws URISyntaxException {
        configurationResource.deleteShunt(LOCAL_URI);
        //would throw if failed
    }
}