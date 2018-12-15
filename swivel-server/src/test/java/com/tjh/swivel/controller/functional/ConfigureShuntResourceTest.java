package com.tjh.swivel.controller.functional;

import com.tjh.swivel.config.ServerConfiguration;
import com.tjh.swivel.controller.ConfigureShuntResource;
import com.tjh.swivel.controller.RequestRouter;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;

@ContextConfiguration(classes = {ServerConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigureShuntResourceTest {

    public static final URI LOCAL_URI = URI.create("couch");
    @Resource
    protected ConfigureShuntResource configureShutResource;
    @Resource
    protected RequestRouter requestRouter;

    @Before
    public void before() {
        configureShutResource.putShunt(LOCAL_URI,
                Map.of(ConfigureShuntResource.REMOTE_URL_KEY, "http://localhost:5984"));
    }

    @Test
    public void shuntWorks() {
        requestRouter.route(new HttpGet(URI.create("couch")));
        //would throw if failed
    }

    @Test
    public void shuntThrowsOnUnknownURL() {
        configureShutResource.deleteShunt(LOCAL_URI);
        //would throw if failed
    }
}
