/*
 * Copyright 2011 Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vlsi.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
 * <p>The expected memory consumption (8u40, 64 bit, compressed references) is as follows:
 *   # of elements  CompactHashMap  HashMap (with 1.0 fillFactor)
 *               0              32       48
 *               1              32      104
 *               2              32      136
 *               3              32      176
 *               4              64      208
 *               5              64      256
 *               6              64      288
 *               7              72      320
 *               8              72      352
 *
 *  In other words, the first three non default values consume the same
 *  32 bytes, then map grows as 32 + 16 + 4 * (n-2) == 40 + 4 * n.
 *  Regular HashMap grows as 64 + 36 * n.
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
        return values().contains(value);
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Map)) {
            return false;
        }

        Map<?,?> m = (Map<?,?>) o;
        if (m.size() != size())
            return false;

        for (Entry<K, V> e : entrySet()) {
            K key = e.getKey();
            V value = e.getValue();

            if (value == null) {
                if (m.get(key) != null || !m.containsKey(key)) {
                    return false;
                }
            } else {
                if (!value.equals(m.get(key))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        if (!it.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (it.hasNext()) {
            Entry<K, V> e = it.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key).append('=').append(value);
            sb.append(',').append(' ');
        }
        sb.setLength(sb.length() - 2);
        return sb.append('}').toString();
    }

    public int hashCode() {
        int h = 0;
        for (Entry<K, V> entry : entrySet()) {
            h += entry.hashCode();
        }
        return h;
    }

    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        klass.serialize(this, s);
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        CompactHashMapClass.deserialize(this, s);
    }
}
