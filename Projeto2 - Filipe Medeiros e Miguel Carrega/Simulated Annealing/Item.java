package SA;

public class Item{
	
	private int weight;
	private int value;
	private double density;
	
	public Item(int weight,int value) {
		
		this.weight = weight;
		this.value = value;
		this.density = (double) value/ (double) weight;
	}

	public int getWeight() {
		return weight;
	}

	public int getValue() {
		return value;
	}
	
	public double getDensity() {
		return density;
	}
	
}
