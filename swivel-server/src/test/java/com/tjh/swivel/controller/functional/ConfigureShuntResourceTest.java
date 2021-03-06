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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
                Map.of(ConfigureShuntResource.REMOTE_URL_KEY, "http://localhost:5984"));
    }

    @Test
    public void shuntWorks() {
        requestRouter.route(new HttpGet(URI.create("couch")));
        //would throw if failed
    }

    @Test
    public void shuntThrowsOnUnknownURL() {
        configurationResource.deleteShunt(LOCAL_URI);
        //would throw if failed
    }
}
