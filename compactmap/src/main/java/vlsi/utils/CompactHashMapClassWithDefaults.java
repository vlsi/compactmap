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
