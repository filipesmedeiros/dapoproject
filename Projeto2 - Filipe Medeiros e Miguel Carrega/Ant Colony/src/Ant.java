import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class Ant {

    private final String name;

    private Boolean[] currentlySelected;
    private final List<AntKnapsackItem> allItems;
    private List<Integer> nextNeighbourhood;
    private double sumOfTauNiu;

    private int currentValue;
    private int currentCapacity;
    private double alpha;
    private double beta;
    private double tauMax;
    private double tauMin;

    Ant(String name, List<AntKnapsackItem> allItems, int capacity,
        double alpha, double beta, double tauMax, double tauMin) {
        this.name = name;

        currentlySelected = new Boolean[allItems.size()];
        for(int i = 0; i < currentlySelected.length; i++)
            currentlySelected[i] = false;

        this.allItems = new ArrayList<>(allItems.size());
        this.allItems.addAll(allItems);

        nextNeighbourhood = new LinkedList<>();
        for(int i = 0; i < allItems.size(); i++)
            nextNeighbourhood.add(i);

        sumOfTauNiu = 0.0;

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

    Boolean[] getCurrentSolution() {
        return currentlySelected;
    }

    // Selects random item from the list of not selected items (in neighbourhood)
    private int selectRandomItem(Random random) {
        int index = random.nextInt(nextNeighbourhood.size());
        return nextNeighbourhood.get(index);
    }

    private double calcProbOfChoosing(int index) {
        if(sumOfTauNiu == 0) {
            final AtomicReference<Double> tempSum = new AtomicReference<>(0.0);
            nextNeighbourhood.forEach(itemIndex -> {
                AntKnapsackItem item = allItems.get(itemIndex);
                double tauNiu = Math.pow(item.pheromoneLevel(), alpha) * Math.pow(item.interest(), beta);
                tempSum.accumulateAndGet(tauNiu, (a, b) -> a + b);
            });
            sumOfTauNiu = tempSum.get();
        }

        return (Math.pow(allItems.get(index).pheromoneLevel(), alpha)
                * Math.pow(allItems.get(index).interest(), beta)) / sumOfTauNiu;
    }

    private void updateNeighbourhood() {
        nextNeighbourhood.removeIf(index -> allItems.get(index).weight() > currentCapacity);

        sumOfTauNiu = 0.0;
    }

    // Chooses next item to add to intermediate solutions,
    // based on a probabilistic formula, and executes the respective
    // consequent actions
    private void chooseNext() {
        if(nextNeighbourhood.size() == 1) {
            int lastItem = nextNeighbourhood.get(0);
            currentlySelected[lastItem] = true;
            nextNeighbourhood.remove(0);
            currentValue += allItems.get(lastItem).value();
            currentCapacity -= allItems.get(lastItem).weight();
            return;
        }

        Random random = new Random();

        while(true) {
            int chosen = selectRandomItem(random);

            // System.out.println("prob -> " + calcProbOfChoosing(chosen) +
                    // " pheromone -> " + allItems.get(chosen).pheromoneLevel());

            double r = Math.random();
            if(r <= calcProbOfChoosing(chosen)) {
                currentlySelected[chosen] = true;
                nextNeighbourhood.remove(Integer.valueOf(chosen));

                currentValue += allItems.get(chosen).value();
                currentCapacity -= allItems.get(chosen).weight();

                updateNeighbourhood();

                return;
            }
        }
    }

    void findSolution() {
        while(nextNeighbourhood.size() > 0)
            chooseNext();
    }

    // Leaves pheromones on items, based on a formula that can vary
    // This formula will be stated in the report
    void leavePheromones(int bestValue) {
    	double pheromoneDelta = 1.0 / (1.0 + (bestValue * 1.0 - currentValue) / bestValue);
    	
        for(int i = 0; i < allItems.size(); i++) {
            AntKnapsackItem item = allItems.get(i);
            if(currentlySelected[i])
                item.setPheromoneLevel(Main.clamp(item.pheromoneLevel() * pheromoneDelta, tauMax, tauMin));
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

    // We give a name to the ant so it can be identified in logs
    String name() {
        return name;
    }
}