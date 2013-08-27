package swivel.model.matchers;

import org.junit.Test;
import vanderbilt.util.Maps;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

public class MapEntryMatcherTest {
    @Test
    public void mapEntriesMatch() {

        assertThat(Maps.asMap("key", Arrays.asList("val1", "val2")),
                MapEntryMatcher.<String, List<String>>hasEntry("key", hasItem("val1")));
    }
}
