package vlsi;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import vlsi.utils.CompactHashMap;

import java.util.HashMap;
import java.util.Map;

@RunWith(AllTests.class)
public class GuavaTestSuiteForMapsTest
{
    public static TestSuite suite()
    {
        return MapTestSuiteBuilder.using(
                new TestStringMapGenerator() {
                    @Override
                    protected Map<String, String> create(Map.Entry<String, String>[] entries) {
                        Map<String, String> map = new HashMap<String, String>();
//                        Map<String, String> map = new CompactHashMap<String, String>();
                        for (Map.Entry<String, String> entry : entries) {
                            map.put(entry.getKey(), entry.getValue());
                        }
                        return map;
                    }
                })
                .named("CompactHashMap")
                .withFeatures(
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.SUPPORTS_REMOVE,
                        MapFeature.SUPPORTS_PUT,
                        CollectionFeature.KNOWN_ORDER,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionSize.ANY
                )
                .createTestSuite();
     }
}
