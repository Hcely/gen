package zr.unit;

import java.util.Map.Entry;

public class KeyValue<K, V> implements Entry<K, V> {
	private final K key;
	private V value;

	public KeyValue(K key) {
		this(key, null);
	}

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V tmp = value;
		this.value = value;
		return tmp;
	}

}
