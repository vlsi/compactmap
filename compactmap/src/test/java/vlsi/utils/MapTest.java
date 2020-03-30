/*
 * Copyright 2019 Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
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

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Map;

public class MapTest {

    public static Test suite() {
        TestSuite suite = new TestSuite("All tests");
        suite.addTest(tests("Simple", new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                return populate(new CompactHashMap<String, String>(), entries);
            }
        }));
        suite.addTest(tests("All as defaults", new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                for (Map.Entry<String, String> entry : entries) {
                    CompactHashMapDefaultValues.add(entry.getKey(), entry.getValue());
                }
                return populate(new CompactHashMap<String, String>(), entries);
            }
        }));
        suite.addTest(tests("First as defaults", new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                for (Map.Entry<String, String> entry : entries) {
                    CompactHashMapDefaultValues.add(entry.getKey(), entry.getValue());
                    break;
                }
                return populate(new CompactHashMap<String, String>(), entries);
            }
        }));
        suite.addTest(tests("Keys as defaults", new TestStringMapGenerator() {
            @Override
            protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                for (Map.Entry<String, String> entry : entries) {
                    CompactHashMapDefaultValues.add(entry.getKey());
                }
                return populate(new CompactHashMap<String, String>(), entries);
            }
        }));
        return suite;
    }

    private static TestSuite tests(final String name, TestStringMapGenerator generator) {
        return MapTestSuiteBuilder
                .using(generator)
                .named(name)
                .withTearDown("Simple".equals(name) ? null : new Runnable() {
                    public void run() {
                        CompactHashMapDefaultValues.clear();
                    }
                })
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        MapFeature.RESTRICTS_KEYS,
                        MapFeature.RESTRICTS_VALUES,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.SERIALIZABLE,
                        CollectionSize.ANY)
                .createTestSuite();
    }

    private static <T, M extends Map<T, String>> M populate(M map, Map.Entry<T, String>[] entries) {
        for (Map.Entry<T, String> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
