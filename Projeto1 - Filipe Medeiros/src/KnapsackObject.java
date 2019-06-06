class KnapsackObject {

    private int weight;
    private int value;

    KnapsackObject(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    int weight() {
        return weight;
    }

    int value() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }
}
