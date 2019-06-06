package SA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solver {

	private static final double ALPHA = 1000000;
    private final Random random = new Random(System.nanoTime());
    
	private static KnapsackObject ks;
	private int initMode;
	private Solution bestSolution;
	private double temperature;
    private final double coolingFactor;
    private final double endingTemperature;
    private final int samplingSize;
    
    
    public Solver(KnapsackObject ks, double temperature, double coolingFactor, double endingTemperature, int samplingSize, int initMode) {
		this.bestSolution = null;
		this.ks = ks;
		this.temperature = temperature;
		this.coolingFactor = coolingFactor;
		this.endingTemperature = endingTemperature;
		this.samplingSize = samplingSize;
		this.initMode = initMode;
	}
    
    // keeps finding solutions until the temperature <= final temperature 
    // returns the final solution
    public Solution solve() {

    	long start = System.currentTimeMillis();
    	
    	BinarySolution current = new BinarySolution(ks.getNumberItems());
    	if (initMode == 2) {
    		current.setChromosome(greedyAlgorithm(sortList()));
    	} else if (initMode == 1) {
    		current.shuffle();
    	}
    	
        BinarySolution best = current;
        current.updateFitness(ks, ALPHA);

        while (temperature > endingTemperature) {
        	
            for (int m = 0; m < samplingSize; m++) {
                current = getNextState(current);
                if (current.getFitness() < best.getFitness()) {
                    best = current;
                }
            }
            cool();
        }

        long end = System.currentTimeMillis();
        List<Item> pickedItem = generateSolution(ks, best);
        return new Solution(pickedItem, end - start);
    }
    
    // gets a new random solution
    // if that solution is better then the current one, then returns the new solution
    // else calculates a probability of returning the new solution, accordingly to the temperature
    private BinarySolution getNextState(BinarySolution current) {
        BinarySolution newSolution = getNeighbour(current);
        double delta = newSolution.getFitness() - current.getFitness();
        if (delta < 0) {
            return newSolution;
        } else {
            double x = Math.random();
            if (x < Math.exp(-delta / temperature)) {
                return newSolution;
            } else {
                return current;
            }
        }
    }

    // creates new neighbour solution by flipping a bit of the current solution
    private BinarySolution getNeighbour(BinarySolution current) {
        BinarySolution mutated = new BinarySolution(current);
        int x = random.nextInt(current.getChromosome().length);
        mutated.flip(x);
        mutated.updateFitness(ks, ALPHA);
        return mutated;
    }
    
    // cools down temperature
    private void cool() {
        temperature *= coolingFactor;
    }

    // gets best solution if there isn't one
	public Solution getSolution() {
        if (bestSolution == null) {
            bestSolution = find();
        }
        return bestSolution;
    }
	
	// find best solution
	public Solution find() {
        return solve();
    }
	
	// generate list of items from binary solution
	public List<Item> generateSolution(KnapsackObject ks, BinarySolution solution) {
        List<Item> pickedItem = new ArrayList<>();
        for (int i = 0; i < ks.getNumberItems(); i++) {
            if (solution.getChromosome()[i] == 1) {
                pickedItem.add(ks.getItems().get(i));
            }
        }
        return pickedItem;
    }
	
	private static byte[] greedyAlgorithm(List<Item> list) {
		
		int currentW = 0;
		int currentV = 0;
		
		byte[] solution = new byte[list.size()];
		for (int i = 0; i < ks.getNumberItems(); i++) {
			
			if(ks.getMaximumWeight() > currentW + list.get(i).getWeight()) {
				
				currentW += list.get(i).getWeight();
				currentV += list.get(i).getValue();
				solution[i] = 1;
			}
		}
		return solution;
	}

	//sort the list of items
	private static List<Item> sortList() {
		Item temp;
		
		List<Item> list = ks.getItems();
		
		for(int i = 1; i < list.size(); i++) {
			for(int j = 0; j < list.size() - i; j++) {
				
		    	if(list.get(j+1).getDensity() > list.get(j).getDensity()) {
			        temp = list.get(j+1);
			        list.remove(j+1);
			        list.add(j+1, list.get(j));
			        list.remove(j);
			        list.add(j,temp);
		    	}
		    }
		}
		return list;
	}
    

}
