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

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Hash table implementation modelled after memory efficient
 * <a href="http://code.google.com/apis/v8/design.html#prop_access">V8's Fast
 * Property Access</a>. This class however can store specific key-value pairs out of
 * the map, so they do not consume memory when repeated in different maps.
 * This implementation permits <tt>null</tt> keys and
 * <tt>null</tt> values. This map makes no guarantees as to the order of the map.
 *
 * <p>This implementation provides constant access time for the basic
 * operations (<tt>get</tt> and <tt>put</tt>). The <tt>get</tt> operation
 * does not create objects. <tt>put</tt> creates array objects when resizing
 * is required.</p>
 *
 * <p>The expected runtime is as follows (measured in hashmap and array accesses):
 *               best case       worst case
 * get    1 hashmap + 1 array    2 hashmap
 * put    1 hashmap + 1 array    6 hashmap
 * </p>
 *
 * <p>The expected memory consumption (32 bit) is as follows:
 *   # of elements  CompactHashMap  HashMap (with 1.0 fillFactor)
 *               0              24       52
 *               1              24       80
 *               2              24      112
 *               3              24      136
 *               4              56      168
 *               5              56      192
 *               6              56      224
 *               7              56      248
 *               8              80      280
 *
 *  In other words, the first three non default values consume the same
 *  24 bytes, then map grows as 28 + 4*n == 44 + 4*(n-4).
 *  Regular HashMap grows as 52 + 28*n.
 * </p>
 *
 *
 * <p><strong>Note that map keys must be reused (you should not use unique
 * objects for keys), otherwise you will run out of memory.</strong></p>
 *
 * <p><strong>Note that this implementation is not synchronized</strong>
 * If multiple threads access the map concurrently, and at least one
 * of the threads modifies the map, it <i>must</i> be synchronized
 * externally.
 * </p>
 *
 * @author Vladimir Sitnikov
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class CompactHashMap<K, V> implements Map<K, V>, Serializable {
    private static final long serialVersionUID = -7720507706954394566L;

    CompactHashMapClass<K, V> klass = CompactHashMapClass.EMPTY;
    Object v1, v2, v3;

    public int size() {
        return klass.size(this);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        return klass.containsKey(this, key);
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public V get(Object key) {
        return klass.get(this, (K) key);
    }

    public V put(K key, V value) {
        return klass.put(this, key, value);
    }

    public V putOrRemove(K key, Object value) {
        return klass.put(this, key, value);
    }

    public V remove(Object key) {
        return klass.put(this, (K) key, CompactHashMapClass.REMOVED_OBJECT);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        klass = CompactHashMapClass.EMPTY;
        v1 = v2 = v3 = null;
    }

    public Set<K> keySet() {
        return klass.keySet(this);
    }

    public Collection<V> values() {
        return klass.values(this);
    }

    public Set<Entry<K, V>> entrySet() {
        return klass.entrySet(this);
    }

    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        klass.serialize(this, s);
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        CompactHashMapClass.deserialize(this, s);
    }
}
