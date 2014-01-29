package p;

import java.util.HashMap;
import java.util.Map;

class C4<T> {
}

class C3 {

	Map<String, C4<?>> map = new HashMap<>();
	
	C4<?> wrapGetValue(String key) {
		return getValue(key);
	}
	
	C4<?> getValue(String key) {
		return map.get(key);
	}
	
}
