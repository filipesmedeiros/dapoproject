package SA;

import java.util.List;

public class Solution {

	private final List<Item> pickedItem;
	private long gainedValue;
	private long gainedWeight;
	private long takenTime;
	
	
	public Solution(List<Item> pickedItem, long takenTime) {
		this.pickedItem = pickedItem;
		this.takenTime = takenTime;
		gainedValue = -1;
		gainedWeight = -1;
	}


	public List<Item> getPickedItem() {
		return pickedItem;
	}


	public long getGainedValue() {
		if (gainedValue == -1) {
            gainedValue = pickedItem.stream().mapToInt(item -> item.getValue()).sum();
        }
		return gainedValue;
	}


	public long getGainedWeight() {
		if (gainedWeight == -1) {
            gainedWeight = pickedItem.stream().mapToInt(item -> item.getWeight()).sum();
        }
		return gainedWeight;
	}


	public long getTakenTime() {
		return takenTime;
	}
	
	
	
}
