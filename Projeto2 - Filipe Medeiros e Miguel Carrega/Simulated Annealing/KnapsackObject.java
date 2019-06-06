package SA;

import java.util.List;

public class KnapsackObject {

    private final List<Item> items;
    private final int maximumWeight;

    public KnapsackObject(int maxSize, List<Item> listItems) {
        items = listItems;
        this.maximumWeight = maxSize;
    
    }

	public List<Item> getItems() {
		return items;
	}

	public int getMaximumWeight() {
		return maximumWeight;
	}
	
	public int getNumberItems() {
		return items.size();
	}
    
}
