package com.tjh.swivel.model.matchers;

import org.hamcrest.Factory;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Map;

public class MapEntryMatcher<K, V> extends FeatureMatcher<Map<K, V>, V> {

    private final K desiredKey;

    public MapEntryMatcher(K desiredKey, Matcher<? super V> subMatcher) {
        super(subMatcher, String.format("Map with entry for key '%1$s'", desiredKey), "entry");
        this.desiredKey = desiredKey;
    }

    @Override
    protected V featureValueOf(Map<K, V> kvMap) {
        return kvMap.get(desiredKey);
    }

    @Factory
    public static <K, V> Matcher<Map<K, V>> hasEntry(K key, Matcher<? super V> val1) {
        return new MapEntryMatcher<K, V>(key, val1);
    }
}
