[![Build Status](https://travis-ci.org/vlsi/compactmap.svg?branch=master)](https://travis-ci.org/vlsi/compactmap)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.vlsi.compactmap/compactmap/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vlsi.compactmap/compactmap)

CompactHashMap
==============

Usage
-----

Add maven dependency:
```xml
<dependency>
    <groupId>com.github.vlsi.compactmap</groupId>
    <artifactId>compactmap</artifactId>
    <version>1.2.1</version>
</dependency>
```

About
-----
This is a memory efficient alternative to HashMap.
Main design goal is taken from "Fast Property Access"
http://code.google.com/apis/v8/design.html#prop_access.

This implementation however can store specific key-value pairs out of the map,
so they do not consume memory when repeated in different maps.

The expected memory consumption (8u40, 64 bit, compressed references) is as follows:
    # of elements  CompactHashMap  HashMap (with 1.0 fillFactor)
                0              32       48
                1              32      104
                2              32      136
                3              32      176
                4              64      208
                5              64      256
                6              64      288
                7              72      320
                8              72      352

  In other words, the first three non default values consume the same
   32 bytes, then map grows as 32 + 16 + 4 * (n-2) == 40 + 4 * n.
   Regular HashMap grows as 64 + 36 * n.

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
v1.2.1
* Improvement: release to Maven Central
* Improvement: fix EntrySet.remove method

v1.2.0
* Improvement: reduce size of hidden classes by using persistent dexx-collections.
* Improvement: mavenize build
* Switch to semantic versioning

v1.1
* Fix: #1 containKey returns true on non existing key
* Fix: #2 size should account removed keys
* Improvement: #3 Default values should be serialized as map

Author
------
Vladimir Sitnikov <sitnikov.vladimir@gmail.com>
