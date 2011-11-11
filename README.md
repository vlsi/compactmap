CompactHashMap
==============

About
-----
This is a memory efficient alternative to HashMap.
Main design goal is taken from "Fast Property Access"
http://code.google.com/apis/v8/design.html#prop_access.

This implementation however can store specific key-value pairs out of the map,
so they do not consume memory when repeated in different maps.

The expected memory consumption (32 bit jvm) is as follows:

	# of elements  CompactHashMap  HashMap (with 1.0 fillFactor)
	            0              24       52
	            1              24       80
	            2              24      112
	            3              24      136
	            4              56      168
	            5              56      192
	            6              56      224
	            7              56      248
	            8              80      280

The runtime of put and get is constant.
The expected runtime is as follows (measured in hashmap and array accesses): 

	             best case        worst case
	get    1 hashmap + 1 array    2 hashmap
	put    1 hashmap + 1 array    6 hashmap


Sample
------

``` java
	// Mark height->auto a default mapping entry, so it would not consume memory in CompactHashMaps
	CompactHashMapDefaultValues.add("height", "auto");

	// Mark all values of width as default, so they would not consume memory in real maps
	CompactHashMapDefaultValues.add("width");

	CompactHashMap<String, String> map = new CompactHashMap<String, String>();
	map.put("height", "auto");      // does not consume memory in map
	map.put("width", "100%");       // does not consume memory in map either
	map.put("id", "myFirstButton"); // consumes some memory

	map.get("height"); // => "auto"
	map.get("width");  // => "100%"
	map.get("id");     // => "myFirstButton"

	map.put("height", "50px"); // consumes some memory (switches from default to custom)

	map.get("height"); // => "50px"
```

License
-------
This library is distibuted under terms of GNU Lesser General Public License
as published by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

Change log
----------
v1.1
  Fix: #1 containKey returns true on non existing key
  Fix: #2 size should account removed keys
  Improvement: #3 Default values should be serialized as map

Author
------
Vladimir Sitnikov <sitnikov.vladimir@gmail.com>