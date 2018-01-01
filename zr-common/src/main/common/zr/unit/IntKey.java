package zr.unit;

import java.util.HashMap;
import java.util.Map;

public final class IntKey {
	private static volatile int inc = 0;
	private static final Map<String, IntKey> keyMap = new HashMap<>();

	public static final IntKey get(String key) {
		IntKey hr = keyMap.get(key);
		if (hr == null)
			synchronized (keyMap) {
				if ((hr = keyMap.get(key)) == null)
					keyMap.put(key, hr = new IntKey(key, inc++));
			}
		return hr;
	}

	public final String key;
	public final int idx;

	private IntKey(String key, int idx) {
		this.key = key;
		this.idx = idx;
	}
}
