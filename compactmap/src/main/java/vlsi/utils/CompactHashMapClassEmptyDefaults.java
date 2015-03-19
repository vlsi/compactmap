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

import com.github.andrewoma.dexx.collection.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * This map represents CompactHashMapClass that has no default values (it can have nonempty key2slot).
 * It is used to determine the right CompactHashMapClass given the desired defaultValues map.
 *
 * @author Vladimir Sitnikov
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
class CompactHashMapClassEmptyDefaults<K, V> extends CompactHashMapClass<K, V> {
    private Map<K, CompactHashMapClassEmptyDefaults<K, V>> key2newKlass;
    private Map<Map<K, V>, CompactHashMapClass<K, V>> defValues2Klass;

    public CompactHashMapClassEmptyDefaults(com.github.andrewoma.dexx.collection.Map<K, Integer> key2Slot) {
        super(key2Slot);
    }

    @Override
    protected CompactHashMapClassEmptyDefaults<K, V> getMapWithEmptyDefaults() {
        return this;
    }

    protected CompactHashMapClass<K, V> getNewDefaultClass(Map<K, V> newDef) {
        CompactHashMapClass<K, V> newClass;
        if (newDef == null || newDef.isEmpty())
            return this;
        synchronized (this) {
            Map<Map<K, V>, CompactHashMapClass<K, V>> defValues2Klass = this.defValues2Klass;
            if (defValues2Klass == null)
                this.defValues2Klass = defValues2Klass =
                        new IdentityHashMap<Map<K, V>, CompactHashMapClass<K, V>>();

            newClass = defValues2Klass.get(newDef);
            if (newClass == null) {
                newClass = new CompactHashMapClassWithDefaults<K, V>(key2slot, newDef, this);
                defValues2Klass.put(newDef, newClass);
            }
        }
        return newClass;
    }

    protected CompactHashMapClass<K, V> getNextKlass(K key, Map<K, V> defaultValues) {
        CompactHashMapClassEmptyDefaults<K, V> newKlass = null;
        synchronized (this) {
            Map<K, CompactHashMapClassEmptyDefaults<K, V>> key2newKlass = this.key2newKlass;
            if (key2newKlass != null)
                newKlass = key2newKlass.get(key);
        }

        if (defaultValues.containsKey(key))
            defaultValues = CompactHashMapDefaultValues.getNewDefaultValues(defaultValues, key, REMOVED_OBJECT);

        if (newKlass != null)
            return newKlass.getNewDefaultClass(defaultValues);

        int size = key2slot.size();
        com.github.andrewoma.dexx.collection.Map<K, Integer> newKey2slot = key2slot;

        if (size < 3) size -= 3;
        else if (size == 3) {
            size = 1;
            for (Pair<K, Integer> entry : key2slot)
                if (entry.component2() == -1) {
                    newKey2slot = newKey2slot.put(entry.component1(), 0);
                    break;
                }
        } else size -= 2;
        newKey2slot = newKey2slot.put(key, size);

        newKlass = new CompactHashMapClassEmptyDefaults<K, V>(newKey2slot);
        synchronized (this) {
            if (key2newKlass == null) {
                key2newKlass = Collections.singletonMap(key, newKlass);
            } else {
                final CompactHashMapClassEmptyDefaults<K, V> anotherNewKlass = key2newKlass.get(key);

                if (anotherNewKlass != null)
                    newKlass = anotherNewKlass;
                else {
                    if (key2newKlass.size() == 1) {
                        key2newKlass = new HashMap<K, CompactHashMapClassEmptyDefaults<K, V>>(key2newKlass);
                    }
                    key2newKlass.put(key, newKlass);
                }
            }
        }

        return newKlass.getNewDefaultClass(defaultValues);
    }
}
