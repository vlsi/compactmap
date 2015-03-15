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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Map;

public class CompactHashMapClassTest {
    @BeforeMethod
    public void clearDefaults() {
        CompactHashMapDefaultValues.clear();
    }

    @Test
    public void emptyMapReturnsNull() {
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        Assert.assertEquals(map.get("test"), null);
        Assert.assertEquals(map.size(), 0);
    }

    @Test
    public void nonDefaultIsStored() {
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        Assert.assertEquals(map.put("test", "abc"), null);
        Assert.assertEquals(map.get("test"), "abc");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);
    }

    @Test
    public void putPutGet20() {
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        for (int i = 0; i < 20; i++) {
            Assert.assertEquals(map.put("k" + i, "v" + i), null);
            Assert.assertEquals(map.size(), i + 1);
            Assert.assertEquals(map.klass.getDefaultValues().size(), 0);
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(map.get("k" + j), "v" + j);
            }
        }
    }

    @Test
    public void putPutGet20WithDefault() {
        CompactHashMapDefaultValues.add("k1", "v1");
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        int SIZE = 20;
        for (int i = 0; i < SIZE; i++) {
            Assert.assertEquals(map.put("k" + i, "v" + i), null);
            Assert.assertEquals(map.size(), i + 1);
            Assert.assertEquals(map.klass.getDefaultValues().size(), i >= 1 ? 1 : 0);
            for (int j = 0; j < i; j++) {
                Assert.assertEquals(map.get("k" + j), "v" + j);
            }
        }
        Assert.assertEquals(map.remove("k1"), "v1");
        Assert.assertEquals(map.size(), SIZE - 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);
    }

    @Test
    public void putDefault() {
        CompactHashMapDefaultValues.add("default"); // all values are default

        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        Assert.assertEquals(map.put("default", "a"), null);
        Assert.assertEquals(map.get("default"), "a");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);

        Assert.assertEquals(map.put("default", "b"), "a");
        Assert.assertEquals(map.get("default"), "b");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);

        Assert.assertEquals(map.putOrRemove("default", CompactHashMapClass.REMOVED_OBJECT), "b");
        Assert.assertEquals(map.get("default"), null);
        Assert.assertEquals(map.size(), 0);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);
    }

    @Test
    public void putDefaultSpecificValue() {
        CompactHashMapDefaultValues.add("test", "testDefaultValue");
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();
        Assert.assertEquals(map.put("test", "testDefaultValue"), null);
        Assert.assertEquals(map.get("test"), "testDefaultValue");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);

        Assert.assertEquals(map.put("test", "non-default"), "testDefaultValue");
        Assert.assertEquals(map.get("test"), "non-default");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);

        Assert.assertEquals(map.put("test", "testDefaultValue"), "non-default");
        Assert.assertEquals(map.get("test"), "testDefaultValue");
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);

        Assert.assertEquals(map.putOrRemove("test", CompactHashMapClass.REMOVED_OBJECT), "testDefaultValue");
        Assert.assertEquals(map.get("test"), null);
        Assert.assertTrue(map.size() == 0 || map.size() == 1); // we allow to count deleted objects in size
        Assert.assertEquals(map.klass.getDefaultValues().size(), 0);
    }

    @Test
    public void putDefaultAndRegular() {
        CompactHashMapDefaultValues.add("default");
        CompactHashMap<String, String> map = new CompactHashMap<String, String>();

        Assert.assertEquals(map.put("default", "x"), null);
        Assert.assertEquals(map.get("default"), "x");
        Assert.assertEquals(map.get("qwer"), null);
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);

        Assert.assertEquals(map.put("qwer", "b"), null);
        Assert.assertEquals(map.get("default"), "x");
        Assert.assertEquals(map.get("qwer"), "b");
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);

        Assert.assertEquals(map.put("default", "y"), "x");
        Assert.assertEquals(map.get("default"), "y");
        Assert.assertEquals(map.get("qwer"), "b");
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.klass.getDefaultValues().size(), 1);
    }

    @Test
    public void containsKeyOnNonExistingKey() {
        CompactHashMapDefaultValues.add("k1", "v1");
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        Assert.assertFalse(map.containsKey("abcd"));
        Assert.assertFalse(map.containsKey("k1"));
    }

    @Test
    public void removeShouldBeReflectedInSize() {
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("charset", "UTF-8");
        map.remove("charset");
        Assert.assertEquals(map.size(), 0);
        Assert.assertFalse(map.containsKey("charset"));
    }

    @Test
    public void cmpactHashMapInstanceShouldBeSerializable() throws IOException, ClassNotFoundException {
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("charset", "UTF-8");
        map.remove("charset");
        CompactHashMap<String, Object> deserialized = serialize(map);
        Assert.assertEquals(deserialized.size(), 0);
        Assert.assertFalse(deserialized.containsKey("charset"));
    }

    private CompactHashMap<String, Object> serialize(CompactHashMap<String, Object> map) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return (CompactHashMap<String, Object>) ois.readObject();
    }

    @Test
    public void defaultValuesAreDeserialized() throws IOException, ClassNotFoundException {
        CompactHashMapDefaultValues.add("k1", "v1");
        CompactHashMapDefaultValues.add("k2", "v2");
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("k1", "v1");
        map.put("k2", "v2");

        CompactHashMap<String, Object> deserialized = serialize(map);
        Assert.assertEquals(deserialized.size(), 2);
        Assert.assertEquals(deserialized.get("k1"), "v1");
        Assert.assertEquals(deserialized.get("k2"), "v2");
    }

    @Test
    public void defaultAndRegularValuesAreDeserialized() throws IOException, ClassNotFoundException {
        CompactHashMapDefaultValues.add("k1", "v1");
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("k1", "v1");
        map.put("k2", "v2");

        CompactHashMap<String, Object> deserialized = serialize(map);
        Assert.assertEquals(deserialized.size(), 2);
        Assert.assertEquals(deserialized.get("k1"), "v1");
        Assert.assertEquals(deserialized.get("k2"), "v2");
    }

    @Test
    public void putNullWorks() {
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("k1", null);
        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get("k1"), null);
    }

    @Test
    public void deleteNullWorks() {
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("k1", null);
        map.remove("k1");
        Assert.assertEquals(map.size(), 0);
        Assert.assertEquals(map.get("k1"), null);
    }

    @Test
    public void deleteFromEntrySet() {
        CompactHashMap<String, Object> map = new CompactHashMap<String, Object>();
        map.put("k1", "v1");
        Assert.assertTrue(map.entrySet().remove(new Map.Entry() {
            public Object getKey() {
                return "k1";
            }

            public Object getValue() {
                return "v1";
            }

            public Object setValue(Object value) {
                return null;
            }
        }), "entry was removed");
        Assert.assertEquals(map.size(), 0);
    }
}
