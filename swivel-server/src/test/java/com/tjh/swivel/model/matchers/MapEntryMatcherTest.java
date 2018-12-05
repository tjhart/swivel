package com.tjh.swivel.model.matchers;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class MapEntryMatcherTest {
    @Test
    public void mapEntriesMatch() {

        assertThat(Map.of("key", Arrays.asList("val1", "val2")),
                MapEntryMatcher.hasEntry("key", hasItem("val1")));
    }
}
