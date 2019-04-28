import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.GraphLayout;
import vlsi.utils.CompactHashMap;

import java.lang.reflect.Field;
import java.util.*;

public class CompactMapSizeTest {
    private final static ArrayList<String> keys = new ArrayList<String>();

    private final BitSet seenKeys = new BitSet();

    static {
        for (int i = 0; i < 1000; i++) {
            keys.add("key" + i);
        }
    }

    private int fill(Map<String, String> map, int size, Random rnd, boolean keySize, boolean valueSize) {
        int overhead = 0;
        seenKeys.clear();
        for (int i = 0; i < size; i++) {
            int index = rnd.nextInt(keys.size());
            if (seenKeys.get(index)) {
                i--;
                continue;
            }
            seenKeys.set(index);
            String key = keys.get(index);
            String value = "value" + index;
            map.put(key, value);
            if (keySize)
                overhead += GraphLayout.parseInstance(key).totalSize();
            if (valueSize)
                overhead += GraphLayout.parseInstance(value).totalSize();
        }
        return overhead;
    }

    @Test
    public void business() {
        ArrayList<Map<String, String>>
                data = new ArrayList<Map<String, String>>();
        Random rnd = new Random();
        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 100; i++) {
                rnd.setSeed(42 * j);
                Map<String, String> m;
                if (false) {
                    m = new CompactHashMap<String, String>();
                } else {
                    m = new HashMap<String, String>();
                }
                fill(m, 100, rnd, false, false);
                data.add(m);
            }
        }
        System.out.println(GraphLayout.parseInstance(data).toFootprint());
    }

    /**
     * Proof that {@link vlsi.utils.CompactHashMap} has ~4 bytes per entry overhead (i.e. the size of a reference).
     * {@link java.util.HashMap} has 36 bytes per entry
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void perEntryOverhead() throws NoSuchFieldException, IllegalAccessException {
        Random rnd = new Random();
        Field klass = CompactHashMap.class.getDeclaredField("klass");
        klass.setAccessible(true);

        System.out.println("number_of_keys;compactmap_estimate;compactmap_size;hashmap_estimate;hashmap_size");
        for (int j = 0; j < 101; j++) {
            rnd.setSeed(42 * j);
            Map<String, String> m = new CompactHashMap<String, String>();
            long compactValues = fill(m, j, rnd, false, true);

            rnd.setSeed(42 * j);
            Map<String, String> hm = new HashMap<String, String>(j, 1.0f);
            long hmVals = fill(hm, j, rnd, true, true) + (j == 0 ? 16 : 0);

            long compactTotalSize = GraphLayout.parseInstance(m).totalSize();
            compactValues += GraphLayout.parseInstance(klass.get(m)).totalSize();

            if (false) {
                System.out.println(GraphLayout.parseInstance(hm).toFootprint());
            }

            long hashMapTotalSize = GraphLayout.parseInstance(hm).totalSize();
            System.out.println(j
                            + ";" + (32 + (j < 4 ? 0 : (16 + 4 * (j - 2))))
                            + ";" + (compactTotalSize - compactValues)
                            + ";" + (48 + (j == 0 ? 0 : (16 + (4 + 32) * j)))
                            + ";" + (hashMapTotalSize - hmVals)
            );
        }
    }

}

