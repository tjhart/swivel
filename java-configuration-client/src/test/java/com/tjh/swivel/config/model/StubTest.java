package com.tjh.swivel.config.model;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;

import static com.tjh.swivel.config.Swivel.get;
import static com.tjh.swivel.config.Swivel.ok;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class StubTest {

    private Stub stub;

    @Before
    public void setUp() throws Exception {
        stub = new Stub(get("some/uri"), ok());
    }

    @Test
    public void toJSONIncludesWhen() throws URISyntaxException, JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.WHEN_KEY), notNullValue());
    }

    @Test
    public void toJSONIncludesThen() throws JSONException {
        assertThat(stub.toJSON().getJSONObject(Stub.THEN_KEY), notNullValue());
    }
}
