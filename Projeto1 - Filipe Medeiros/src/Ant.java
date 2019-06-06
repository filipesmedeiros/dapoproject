import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Ant {

	private boolean[] currentlySelected;
	private List<AntKnapsackItem> allItems;
	private Set<AntKnapsackItem> nextNeighbourhood;
	private int bestValueSelected;
	
	public Ant(List<AntKnapsackItem> allItems) {
		currentlySelected = new boolean[allItems.size()];
		
		this.allItems = allItems;
		
		nextNeighbourhood = new HashSet<>();
		allItems.forEach(item -> nextNeighbourhood.add(item));
		
		bestValueSelected = 0;
	}
	
	public int chooseNext() {
		Random random = new Random();
	}
	
	public void leavePheromones() {
		for(int i = 0; i < allItems.size(); i++)
			if(currentlySelected[i]) {
				double pheronomeDelta = 1 / (1 +
						((bestValueSelected - allItems.get(i).value()) / bestValueSelected));
				
				allItems.get(i).increasePheronomeLevel(pheronomeDelta);
			}
	}
	
	private int selectRandomItem(Random random) {
		int index = random.nextInt(nextNeighbourhood.size());
		Iterator<Object> iter = nextNeighbourhood.iterator();
		for (int i = 0; i < index; i++) {
		    iter.next();
		}
		return iter.next();
	}
}
