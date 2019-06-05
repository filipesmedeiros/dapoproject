import java.util.function.BiFunction;
import java.util.function.Function;

public class AntKnapsackItem {

	private int weight;
	private int value;
	private double pheromoneLevel;
	private double interest;

	public AntKnapsackItem(int weight, int value,
			BiFunction<Integer, Integer, Double> interestFunction) {

		this.weight = weight;
		this.value = value;
		this.pheromoneLevel = 0;
		this.interest = interestFunction.apply(weight, value);
	}

	public double increasePheronomeLevel(double by) {
		return pheromoneLevel += by;
	}

	public double decreasePheronomeLevel(double by) {
		return pheromoneLevel -= by;
	}

	public double changePheromoneLevel(Function<Double, Double> function) {
		return pheromoneLevel = function.apply(this.pheromoneLevel);
	}
	
	public int weight() {
		return weight;
	}
	
	public int value() {
		return value;
	}
	
	public double pheromoneLevel() {
		return pheromoneLevel;
	}
	
	public double interest() {
		return interest;
	}
}
