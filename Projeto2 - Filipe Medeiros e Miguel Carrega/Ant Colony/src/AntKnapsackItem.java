import java.util.function.BiFunction;
import java.util.function.Function;

class AntKnapsackItem {

    private int weight;
    private int value;
    private double pheromoneLevel;
    private double interest;

    AntKnapsackItem(int weight, int value,
                           BiFunction<Integer, Integer, Double> interestFunction) {

        this.weight = weight;
        this.value = value;
        this.pheromoneLevel = 1;
        this.interest = interestFunction.apply(weight, value);
    }

    void changePheromoneLevel(Function<Double, Double> function) {
        pheromoneLevel = function.apply(this.pheromoneLevel);
    }

    void setPheromoneLevel(double pheromoneLevel) {
        this.pheromoneLevel = pheromoneLevel;
    }

    int weight() {
        return weight;
    }

    int value() {
        return value;
    }

    double pheromoneLevel() {
        return pheromoneLevel;
    }

    double interest() {
        return interest;
    }
}