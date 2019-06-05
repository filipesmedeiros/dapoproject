import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class Ant {

    private final String name;

    private Boolean[] currentlySelected;
    private final List<AntKnapsackItem> allItems;
    private List<Integer> nextNeighbourhood;
    private final double bestValue;

    private int currentValue;
    private int currentCapacity;
    private double alpha;
    private double beta;
    private double tauMax;
    private double tauMin;

    Ant(String name, List<AntKnapsackItem> allItems, double bestValue, int capacity,
        double alpha, double beta, double tauMax, double tauMin) {
        this.name = name;

        currentlySelected = new Boolean[allItems.size()];
        for(int i = 0; i < currentlySelected.length; i++)
            currentlySelected[i] = false;

        this.allItems = allItems;

        nextNeighbourhood = new LinkedList<>();
        for(int i = 0; i < allItems.size(); i++)
            nextNeighbourhood.add(i);

        this.bestValue = bestValue;

        currentValue = 0;
        currentCapacity = capacity;
        this.alpha = alpha;
        this.beta = beta;
        this.tauMax = tauMax;
        this.tauMin = tauMin;
    }

    int getCurrentValue() {
        return currentValue;
    }

    // Selects random item from the list of not selected items (in neighbourhood)
    private int selectRandomItem(Random random) {
        int index = random.nextInt(nextNeighbourhood.size());
        return nextNeighbourhood.get(index);
    }

    private double calcProbOfChoosing(int index) {
        final AtomicReference<Double> sumOfTauNiu = new AtomicReference<>(0.0);
        nextNeighbourhood.forEach(itemIndex -> {
            AntKnapsackItem item = allItems.get(itemIndex);
            double tauNiu = Math.pow(item.pheromoneLevel(), alpha) + Math.pow(item.interest(), beta);
            sumOfTauNiu.accumulateAndGet(tauNiu, (a, b) -> a + b);
        });

        return (Math.pow(allItems.get(index).pheromoneLevel(), alpha)
                + Math.pow(allItems.get(index).interest(), beta)) / sumOfTauNiu.get();
    }

    private void updateNeighbourhood() {
        nextNeighbourhood.removeIf(index -> allItems.get(index).weight() > currentCapacity);
    }

    // Chooses next item to add to intermediate solutions,
    // based on a probabilistic formula, and executes the respective
    // consequent actions
    // Returns -1 when ant has found a final solution
    private void chooseNext() {
        Random random = new Random();

        while(true) {
            int chosen = selectRandomItem(random);

            if(random.nextDouble() <= calcProbOfChoosing(chosen)) {
                currentlySelected[chosen] = true;
                nextNeighbourhood.remove(Integer.valueOf(chosen));

                currentValue += allItems.get(chosen).value();
                currentCapacity -= allItems.get(chosen).weight();

                updateNeighbourhood();
                return;
            }
        }
    }

    Boolean[] findSolution() {
        while(nextNeighbourhood.size() > 0)
            chooseNext();

        return currentlySelected;
    }

    // Leaves pheromones on items, based on a formula that can vary
    // This formula will be stated in the report
    void leavePheromones() {
        System.out.println(name + " x-----------");
        for(int i = 0; i < allItems.size(); i++) {
            System.out.print("from: " + allItems.get(i).pheromoneLevel());

            if(currentlySelected[i]) {
                AntKnapsackItem item = allItems.get(i);

                double valueRatio = item.value() * 1.0 / Math.pow(item.weight(), 2.0);

                double pheromoneDelta = 1.0 / (1.0 + ((bestValue - valueRatio) / bestValue));

                double newPheromoneLevel = item.increasePheronomeLevel(pheromoneDelta);
                if (newPheromoneLevel > tauMax)
                    item.setPheromoneLevel(tauMax);
                else if (newPheromoneLevel < tauMin)
                    item.setPheromoneLevel(tauMin);
            }

            System.out.println(" to: " + allItems.get(i).pheromoneLevel());
        }
    }

    // Between iterations, the ants have to be reset
    void resetAnt(int capacity) {
        currentlySelected = new Boolean[allItems.size()];
        for(int i = 0; i < currentlySelected.length; i++)
            currentlySelected[i] = false;

        nextNeighbourhood = new LinkedList<>();
        for(int i = 0; i < allItems.size(); i++)
            nextNeighbourhood.add(i);

        currentValue = 0;
        currentCapacity = capacity;
    }
}