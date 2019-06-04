public class ArrayAtomicReference<T> {

    private T[] array;

    public ArrayAtomicReference() {
        array = null;
    }

    public T[] get() {
        return array;
    }

    public void set(T[] array) {
        this.array = array;
    }
}
