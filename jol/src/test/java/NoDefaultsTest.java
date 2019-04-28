import com.github.andrewoma.dexx.collection.KeyFunction;
import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.github.krukow.clj_lang.PersistentTreeMap;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;
import vlsi.utils.CompactHashMap;

import java.util.*;

public class NoDefaultsTest {
    @Test
    @Disabled
    public void emptyCompactMap() {
        Map m = new CompactHashMap();
        System.out.println(ClassLayout.parseClass(m.getClass()).toPrintable(m));
        System.out.println(GraphLayout.parseInstance(m).toPrintable());
    }

    @Test
    @Disabled
    public void emptyHashMap() {
        Map m = new HashMap();
        System.out.println(ClassLayout.parseClass(m.getClass()).toPrintable(m));
        ;
        System.out.println(GraphLayout.parseInstance(m).toPrintable());
    }

    public static class Value {
        Integer key;
        int value;

        public Value(Integer key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Here memory footprint of persistent collections is compared.
     */
    @Test
    public void simpleValues() {
        Map m = new CompactHashMap();
        int n = 100;

        List<Value> data = new ArrayList<Value>(n);
        for (int i = 0; i < n; i++) {
            data.add(new Value(i * 10, i));
        }
        Collections.shuffle(data);
        {
            ArrayList x = new ArrayList();
            Map<Object, Object> u = new HashMap<Object, Object>();
            x.add(u);
            for (Value value : data) {
                u = new HashMap<Object, Object>(u);
                u.put(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            PMap<Object, Object> u = HashTreePMap.empty();
            x.add(u);
            for (Value value : data) {
                u = u.plus(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            com.github.andrewoma.dexx.collection.Map<Object, Object> u = new com.github.andrewoma.dexx.collection.TreeMap();
            x.add(u);
            for (Value value : data) {
                u = u.put(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            com.github.andrewoma.dexx.collection.Map<Integer, Value> u =
                    new com.github.andrewoma.dexx.collection.TreeMap<Integer, Value>(null, new KeyFunction<Integer, Value>() {
                        public Integer key(Value value) {
                            return value.key;
                        }
                    });
            x.add(u);
            for (Value value : data) {
                u = u.put(value.key, value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            com.github.andrewoma.dexx.collection.Map<Object, Object> u = new com.github.andrewoma.dexx.collection.HashMap<Object, Object>();
            x.add(u);
            for (Value value : data) {
                u = u.put(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            com.github.andrewoma.dexx.collection.Map<Integer, Value> u =
                    new com.github.andrewoma.dexx.collection.DerivedKeyHashMap<Integer, Value>(new KeyFunction<Integer, Value>() {
                        public Integer key(Value value) {
                            return value.key;
                        }
                    });
            x.add(u);
            for (Value value : data) {
                u = u.put(value.key, value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            PersistentMap<Object, Object> u = PersistentHashMap.emptyMap();
            x.add(u);
            for (Value value : data) {
                u = u.plus(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
        {
            ArrayList x = new ArrayList();
            PersistentMap<Object, Object> u = PersistentTreeMap.EMPTY;
            x.add(u);
            for (Value value : data) {
                u = u.plus(value.key, value.value);
                x.add(u);
            }
            GraphLayout gl = GraphLayout.parseInstance(x);
            System.out.print(gl.toFootprint());
            System.out.println("gl.totalSize()*1.f/n = " + gl.totalSize() * 1.f / n + "\n");
        }
//            System.out.println(ClassLayout.parseClass(m.getClass()).toPrintable(m));;
//            System.out.println(GraphLayout.parseInstance(m).toPrintable());
    }
    /*
i = 0; 272
i = 1; 384
i = 2; 504
i = 3; 640
i = 4; 736
i = 5; 832
i = 6; 928
i = 7; 1048
i = 8; 1176
i = 9; 1272
i = 10; 1376
i = 11; 1480
i = 12; 1584
i = 13; 1736
i = 14; 1840
i = 15; 1944
i = 16; 2112
i = 17; 2216
i = 18; 2320
i = 19; 2424
i = 20; 2528
i = 21; 2632
i = 22; 2736
i = 23; 2840
i = 24; 2944
i = 25; 3144
i = 26; 3248
i = 27; 3352
i = 28; 3456
i = 29; 3560
i = 30; 3664
i = 31; 3768
i = 32; 4000
i = 33; 4104
i = 34; 4208
i = 35; 4312
i = 36; 4416
i = 37; 4520
i = 38; 4624
i = 39; 4728
i = 40; 4832
i = 41; 4936
i = 42; 5040
i = 43; 5144
i = 44; 5248
i = 45; 5352
i = 46; 5456
i = 47; 5560
i = 48; 5664
i = 49; 5960
i = 50; 6064
i = 51; 6168
i = 52; 6272
i = 53; 6376
i = 54; 6480
i = 55; 6584
i = 56; 6688
i = 57; 6792
i = 58; 6896
i = 59; 7000
i = 60; 7104
i = 61; 7208
i = 62; 7312
i = 63; 7416
i = 64; 7776
i = 65; 7880
i = 66; 7984
i = 67; 8088
i = 68; 8192
i = 69; 8296
i = 70; 8400
i = 71; 8504
i = 72; 8608
i = 73; 8712
i = 74; 8816
i = 75; 8920
i = 76; 9024
i = 77; 9128
i = 78; 9232
i = 79; 9336
i = 80; 9440
i = 81; 9544
i = 82; 9648
i = 83; 9752
i = 84; 9856
i = 85; 9960
i = 86; 10064
i = 87; 10168
i = 88; 10272
i = 89; 10376
i = 90; 10480
i = 91; 10584
i = 92; 10688
i = 93; 10792
i = 94; 10896
i = 95; 11000
i = 96; 11104
i = 97; 11592
i = 98; 11696
i = 99; 11800
     */
}

