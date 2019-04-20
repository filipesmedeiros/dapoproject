class Pair<K, V> {

    private K key;
    private V value;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K key() {
        return key;
    }

    V value() {
        return value;
    }

    void setValue(V value) {
        this.value = value;
    }
}
