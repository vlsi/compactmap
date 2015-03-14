/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.github.vlsi.compactmap;

import com.github.andrewoma.dexx.collection.DerivedKeyHashMap;
import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.github.krukow.clj_lang.PersistentTreeMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MyBenchmark {
    @Param({"5", "10", "25", "50", "75", "100", "150", "500", "1000"})
    int n = 100;
    List<Value> data;

    Map<Integer, Integer> hashMap;
    Map<Integer, Integer> treeMap;
    PMap<Integer, Integer> pcollHashMap;
    com.github.andrewoma.dexx.collection.Map<Integer, Integer> dexxTreeMap;
    com.github.andrewoma.dexx.collection.Map<Integer, Value> dexxSmartMap;
    com.github.andrewoma.dexx.collection.Map<Integer, Value> hashDexx;
    com.github.andrewoma.dexx.collection.Map<Integer, Value> hashDexxSmart;
    PersistentMap<Integer, Integer> cljHashMap;
    PersistentMap<Integer, Integer> cljTreeMap;

    public static class Value {
        int key, value;

        public Value(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    @Setup
    public void init() {
        data = new ArrayList<Value>(n);
        for (int i = 0; i < n; i++) {
            data.add(new Value(i * 1001, i));
        }
        Collections.shuffle(data);

        pcollHashMap = HashTreePMap.empty();
        dexxTreeMap = new com.github.andrewoma.dexx.collection.TreeMap<Integer, Integer>();
        KeyFunction<Integer, Value> keyFunction = new KeyFunction<Integer, Value>() {
            public Integer key(Value value) {
                return value.key;
            }
        };
        dexxSmartMap =
                new com.github.andrewoma.dexx.collection.TreeMap<Integer, Value>(null, keyFunction);
        hashDexx = com.github.andrewoma.dexx.collection.HashMap.empty();
        hashDexxSmart = new DerivedKeyHashMap(keyFunction);
        cljHashMap = PersistentHashMap.emptyMap();
        cljTreeMap = PersistentTreeMap.EMPTY;
        hashMap = new HashMap<Integer, Integer>();
        treeMap = new TreeMap<Integer, Integer>();
        for (Value value : data) {
            pcollHashMap = pcollHashMap.plus(value.key, value.value);
            dexxTreeMap = dexxTreeMap.put(value.key, value.value);
            dexxSmartMap = dexxSmartMap.put(value.key, value);
            hashDexx = hashDexx.put(value.key, value);
            hashDexxSmart = hashDexxSmart.put(value.key, value);
            cljHashMap = cljHashMap.plus(value.key, value.value);
            cljTreeMap = cljTreeMap.plus(value.key, value.value);
            hashMap.put(value.key, value.value);
            treeMap.put(value.key, value.value);
        }
    }

    @Benchmark
    public void hashPcoll(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(pcollHashMap.get(value.key));
        }
    }

    @Benchmark
    public void treeDexx(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(dexxTreeMap.get(value.key));
        }
    }

    @Benchmark
    public void treeDexxSmart(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(dexxSmartMap.get(value.key));
        }
    }

    @Benchmark
    public void hashDexx(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(hashDexx.get(value.key));
        }
    }

    @Benchmark
    public void hashDexxSmart(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(hashDexxSmart.get(value.key));
        }
    }

    @Benchmark
    public void hashClj(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(cljHashMap.get(value.key));
        }
    }

    @Benchmark
    public void treeClj(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(cljTreeMap.get(value.key));
        }
    }

    @Benchmark
    public void hashBase(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(hashMap.get(value.key));
        }
    }

    @Benchmark
    public void treeBase(Blackhole b) {
        List<Value> data = this.data;
        for (int i = 0; i < data.size(); i++) {
            Value value = data.get(i);
            b.consume(treeMap.get(value.key));
        }
    }
}
