package SA;

public class BinarySolution {

	private byte[] chromosome;
	private double fitness;
	private long weight;
	
	public BinarySolution(int size) {
	    chromosome = new byte[size];
	    fitness = Double.MAX_VALUE;
	    weight = Long.MAX_VALUE;
	}

	// copy of a binary solution
	public BinarySolution(BinarySolution other) {
        this.chromosome = other.chromosome.clone();
        fitness = other.getFitness();
        weight = other.getWeight();
    }
	// updates the fitness value
	public void updateFitness(KnapsackObject data, double alpha) {
        long sumVal = 0, sumWeight = 0;
        for (int i = 0; i < data.getNumberItems(); i++) {
            Item item = data.getItems().get(i);
            if (chromosome[i] == 1) {
                sumWeight += item.getWeight();
            } else {
                sumVal += item.getValue();
            }
        }
        double violation = Math.max((double) sumWeight / data.getMaximumWeight() - 1, 0);
        setWeight(sumWeight);
        setFitness(sumVal + alpha * violation);
    }

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public long getWeight() {
		return weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}
	
	public byte[] getChromosome() {
		return chromosome;
	}
	
	public void setChromosome(byte[] newChromosome) {
		chromosome = newChromosome;
	}

	// flips the bit "position" in chromosome
	public void flip(int position) {
	        chromosome[position] = (byte) (chromosome[position] ^ 1);
	}
	
	//generates random chromosome solution
	public void shuffle() {
        for (int i = 0; i < chromosome.length; i++) {
            if (Math.random() > 0.5) {
                chromosome[i] = 0;
            } else {
                chromosome[i] = 1;
            }
        }
    }

	    
}
