import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class Ant {

    private Boolean[] currentlySelected;
    private List<AntKnapsackItem> allItems;
    private List<Integer> nextNeighbourhood;
    private int currentValue;

    Ant(List<AntKnapsackItem> allItems) {
        currentlySelected = new Boolean[allItems.size()];
        for(int i = 0; i < currentlySelected.length; i++)
            currentlySelected[i] = false;

        this.allItems = allItems;

        nextNeighbourhood = new LinkedList<>();
        for(int i = 0; i < allItems.size(); i++)
            nextNeighbourhood.add(i);
    }

    int getCurrentValue() {
        return currentValue;
    }

    Boolean[] getSolution() {
        return currentlySelected;
    }

    // Chooses next item to add to intermediate solutions,
    // based on a probabilistic formula, and executes the respective
    // consequent actions
    // Returns -1 when ant has found a final solution
    int chooseNext(double alpha, double beta, int currentCapacity) {
        // Solution is done
        if(nextNeighbourhood.size() == 0)
            return -1;

        Random random = new Random();

        while(true) {
            int chosen = selectRandomItem(random);

            final AtomicReference<Double> sumOfTauNiu = new AtomicReference<>(0.0);
            nextNeighbourhood.forEach(itemIndex -> {
                AntKnapsackItem item = allItems.get(itemIndex);
                double tauNiu = Math.pow(item.pheromoneLevel(), alpha) + Math.pow(item.interest(), beta);
                sumOfTauNiu.accumulateAndGet(tauNiu, (a, b) -> a + b);
            });

            double probabilityOfChoosing = (Math.pow(allItems.get(chosen).pheromoneLevel(), alpha)
                    + Math.pow(allItems.get(chosen).interest(), beta)) / sumOfTauNiu.get();

            if(random.nextDouble() < probabilityOfChoosing) {
                currentlySelected[chosen] = true;
                nextNeighbourhood.remove(Integer.valueOf(chosen));

                currentValue += allItems.get(chosen).value();

                final int newCapacity = currentCapacity - allItems.get(chosen).weight();
                nextNeighbourhood.removeIf(index -> allItems.get(index).weight() > newCapacity);

                return newCapacity;
            }
        }
    }

    // Leaves pheromones on items, based on a formula that can vary
    // This formula will be stated in the report
    void leavePheromones(double bestValue) {
        for(int i = 0; i < allItems.size(); i++)
            if(currentlySelected[i]) {
                double pheronomeDelta = 1 / (1 + ((bestValue - allItems.get(i).value()) / bestValue));

                allItems.get(i).increasePheronomeLevel(pheronomeDelta);
            }
    }

    // Selects random item from the list of not selected items (and in neighbourhood)
    private int selectRandomItem(Random random) {
        int index = random.nextInt(nextNeighbourhood.size());
        return nextNeighbourhood.get(index);
    }
}