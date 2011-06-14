/*
 * Copyright (c) 2011
 *
 * This file is part of CompactMap
 *
 * CompactMap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CompactMap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CompactMap.  If not, see <http://www.gnu.org/licenses/>.
 */

package vlsi.utils;

import java.util.*;

public class CompactHashMapClass<K, V> {
    public static final CompactHashMapClass EMPTY = new CompactHashMapClassEmptyDefaults(Collections.emptyMap());

    final Map<K, Integer> key2slot; // Immutable
    final Map<K, V> defaultValues; // Immutable

    // Reference to the map with defaultValues == emptyMap
    private CompactHashMapClassEmptyDefaults<K, V> mapWithEmptyDefaults;

    // This value is used as a marker of deleted object
    // "new String" is required to avoid clashing with regular strings
    public static final String[] REMOVED_OBJECT = new String[]{"Non existing mapping value"};

    public CompactHashMapClass(Map<K, Integer> key2slot, Map<K, V> defaultValues, CompactHashMapClassEmptyDefaults<K, V> mapWithEmptyDefaults) {
        this.key2slot = key2slot;
        this.defaultValues = defaultValues;
        this.mapWithEmptyDefaults = mapWithEmptyDefaults;
    }

    public CompactHashMapClass(Map<K, Integer> key2slot) {
        this.key2slot = key2slot;
        this.defaultValues = Collections.emptyMap();
        this.mapWithEmptyDefaults = (CompactHashMapClassEmptyDefaults<K, V>) this;
    }

    public V get(CompactHashMap<K, V> map, K key) {
        Object result = getInternal(map, key);
        return result != REMOVED_OBJECT ? (V) result : null;
    }

    private Object getInternal(CompactHashMap<K, V> map, Object key) {
        final Integer slot = key2slot.get(key);
        if (slot == null)
            return defaultValues.get(key);

        return getValueFromSlot(map, slot);
    }

    protected static Object getValueFromSlot(CompactHashMap map, int slot) {
        switch (slot) {
            case -1:
                return map.v1;
            case -2:
                return map.v2;
            case -3:
                return map.v3;
        }

        return ((Object[]) map.v1)[slot];
    }

    public V put(CompactHashMap<K, V> map, K key, Object value) {
        Integer slot = key2slot.get(key);
        Object prevValue = REMOVED_OBJECT;
        if (slot == null) {
            prevValue = defaultValues.get(key);

            // Try put value as "default"
            Map<K, V> newDef = CompactHashMapDefaultValues.getNewDefaultValues(defaultValues, key, value);
            if (newDef != null) {
                map.klass = mapWithEmptyDefaults.getNewDefaultClass(newDef);
                return (V) prevValue;
            }

            if (value == REMOVED_OBJECT)
                return (V) prevValue;
            // The value is not default -- put using regular way
            slot = createNewSlot(map, key);
        }

        switch (slot) {
            case -1:
                if (prevValue == REMOVED_OBJECT)
                    prevValue = map.v1;
                map.v1 = value;
                break;
            case -2:
                if (prevValue == REMOVED_OBJECT)
                    prevValue = map.v2;
                map.v2 = value;
                break;
            case -3:
                if (prevValue == REMOVED_OBJECT)
                    prevValue = map.v3;
                map.v3 = value;
                break;
            default:
                Object[] array = (Object[]) map.v1;
                if (prevValue == REMOVED_OBJECT)
                    prevValue = array[slot];
                array[slot] = value;
                break;
        }

        return (V) prevValue;
    }

    private Integer createNewSlot(CompactHashMap<K, V> map, K key) {
        final CompactHashMapClass<K, V> nextKlass = mapWithEmptyDefaults.getNextKlass(key, defaultValues);
        map.klass = nextKlass;

        int prevSize = key2slot.size();

        if (prevSize == 3) {
            // Array length should be odd to play well with 8 byte alignment of object size
            //  2 refs (object header) + 1 int (array length) + n*length refs (contents)
            Object[] array = new Object[5];
            array[0] = map.v1;
            map.v1 = array;
        } else if (prevSize > 3) {
            Object[] array = (Object[]) map.v1;
            if (array.length < prevSize - 1) {
                Object[] newArray = new Object[array.length * 2 + 1]; // length should be odd
                System.arraycopy(array, 0, newArray, 0, array.length);
                map.v1 = newArray;
            }
        }

        return nextKlass.key2slot.get(key);
    }

    public int size() {
        return key2slot.size() + defaultValues.size();
    }

    public boolean containsKey(CompactHashMap<K, V> map, Object key) {
        return getInternal(map, key) != REMOVED_OBJECT;
    }

    public Set<K> keySet(CompactHashMap<K, V> map) {
        return new CompactHashMapClass.KeySet<K, V>(map);
    }

    public Set<V> values(CompactHashMap<K, V> map) {
        return new CompactHashMapClass.Values<K, V>(map);
    }

    public Set<Map.Entry<K, V>> entrySet(CompactHashMap<K, V> map) {
        return new CompactHashMapClass.EntrySet<K, V>(map);
    }

    static class KeySet<K, V> extends AbstractSet<K> {
        private final CompactHashMap<K, V> map;

        public KeySet(CompactHashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return map.remove(o) != null; // TODO: support null as "previous" value
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator<K, V>(map);
        }

        @Override
        public void clear() {
            map.clear();
        }
    }

    static class Values<K, V> extends AbstractSet<V> {
        private final CompactHashMap<K, V> map;

        public Values(CompactHashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean contains(Object o) {
            return map.containsValue(o);
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator<K, V>(map);
        }

        @Override
        public void clear() {
            map.clear();
        }
    }

    static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
        private final CompactHashMap<K, V> map;

        public EntrySet(CompactHashMap<K, V> map) {
            this.map = map;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator<K, V>(map);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<K, V> e = (Map.Entry<K, V>) o;
            Object mapValue = map.get(e.getKey());
            return mapValue == e.getValue() || mapValue != null && mapValue.equals(e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            return map.remove(o) != null; // TODO: support null as "previous" value
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public void clear() {
            map.clear();
        }
    }

    static abstract class HashIterator<K, V, E> implements Iterator<E> {
        boolean defValues = true;
        private final CompactHashMap<K, V> map;
        Iterator it;
        Map.Entry<K, V> current, next;

        public HashIterator(CompactHashMap<K, V> map) {
            this.map = map;
            if (map.isEmpty()) return;
            this.it = map.klass.defaultValues.entrySet().iterator();
            advance();
        }

        private void advance() {
            if (!it.hasNext() && defValues) {
                defValues = false;
                it = map.klass.key2slot.entrySet().iterator();
            }

            if (!it.hasNext()) {
                next = null;
                return;
            }

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                V value;
                if (defValues)
                    value = (V) entry.getValue();
                else {
                    value = (V) getValueFromSlot(map, (Integer) entry.getValue());
                    if (value == REMOVED_OBJECT) continue;
                }
                next = new SimpleEntry<K, V>(map, (K) entry.getKey(), value);
                return;
            }
            next = null;
        }

        public boolean hasNext() {
            return next != null;
        }

        public Map.Entry<K, V> nextEntry() {
            if (next == null)
                throw new NoSuchElementException();
            current = next;
            advance();
            return current;
        }

        public void remove() {
            map.remove(current.getKey());
        }
    }


    static class KeyIterator<K, V> extends HashIterator<K, V, K> {
        public KeyIterator(CompactHashMap<K, V> kvCompactMap) {
            super(kvCompactMap);
        }

        public K next() {
            return nextEntry().getKey();
        }
    }

    static class ValueIterator<K, V> extends HashIterator<K, V, V> {
        public ValueIterator(CompactHashMap<K, V> kvCompactMap) {
            super(kvCompactMap);
        }

        public V next() {
            return nextEntry().getValue();
        }
    }

    static class EntryIterator<K, V> extends HashIterator<K, V, Map.Entry<K, V>> {
        public EntryIterator(CompactHashMap<K, V> kvCompactMap) {
            super(kvCompactMap);
        }

        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    static class SimpleEntry<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        private final CompactHashMap<K, V> map;

        public SimpleEntry(CompactHashMap<K, V> map, K key, V value) {
            this.map = map;
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            this.value = value;
            return map.put(key, value);
        }
    }

}
