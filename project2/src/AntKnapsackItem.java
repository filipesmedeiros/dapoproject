import java.util.function.BiFunction;
import java.util.function.Function;

public class AntKnapsackItem {

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

    double increasePheronomeLevel(double by) {
        return pheromoneLevel += by;
    }

    public double decreasePheronomeLevel(double by) {
        return pheromoneLevel -= by;
    }

    double changePheromoneLevel(Function<Double, Double> function) {
        return pheromoneLevel = function.apply(this.pheromoneLevel);
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