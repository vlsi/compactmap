package vlsi.utils;

import java.util.Map;

class CompactHashMapClassWithDefaults<K, V> extends CompactHashMapClass<K, V> {
    private final Map<K, V> defaultValues;
    private final CompactHashMapClassEmptyDefaults<K, V> mapClassEmptyDefaults;

    public CompactHashMapClassWithDefaults(
            com.github.andrewoma.dexx.collection.Map<K, Integer> key2Slot,
            Map<K, V> defaultValues,
            CompactHashMapClassEmptyDefaults<K, V> mapClassEmptyDefaults) {
        super(key2Slot);
        this.defaultValues = defaultValues;
        this.mapClassEmptyDefaults = mapClassEmptyDefaults;
    }

    @Override
    protected Map<K, V> getDefaultValues() {
        return defaultValues;
    }

    @Override
    protected CompactHashMapClassEmptyDefaults<K, V> getMapWithEmptyDefaults() {
        return mapClassEmptyDefaults;
    }
}
