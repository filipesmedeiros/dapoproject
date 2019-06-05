import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static int capacity;
    private static List<AntKnapsackItem> itemList;
    private static List<Ant> antList;
    private static double bestValue;

    private static void readInput(String fileName) throws IOException {
        int[] weights;
        int[] values;

        List<String> lines = Files.readAllLines(Paths.get(fileName));

        capacity = Integer.parseInt(lines.get(0).split(" ")[1]);
        weights = new int[lines.size() - 1];
        values = new int[lines.size() - 1];

        Iterator<String> iterator = lines.iterator();
        iterator.next();
        int index = 0;
        bestValue = 0;
        while(iterator.hasNext()) {
            String[] line = iterator.next().split(" ");
            weights[index] = Integer.parseInt(line[0]);
            values[index] = Integer.parseInt(line[1]);

            double value = values[index] * 1.0 / Math.pow(weights[index], 2.0);
            if(bestValue < value)
                bestValue = value;

            index++;
        }

        itemList = new ArrayList<>(weights.length);
        for(int i = 0; i < weights.length; i++)
            // The specific formula for calculating the interest the ants have on an item can vary
            // and this will be stated in the report
            itemList.add(new AntKnapsackItem(weights[i], values[i], (weight, value) -> value / Math.pow(weight, 2)));
    }

    private static double clamp(double value, double max, double min) {
        if(value > max)
            return max;
        else if(value < min)
            return min;
        else
            return value;
    }

    private static void evaporate(double ro, double tauMax, double tauMin) {
        itemList.forEach(item -> item.changePheromoneLevel(
                pheromoneLevel -> clamp(pheromoneLevel * ro, tauMax, tauMin)
        ));
    }

    private static void solve(double ro, double tauMax, double tauMin, int iterations) {
        int globalBest = 0;
        Boolean[] globalSolution = new Boolean[itemList.size()];

        for(int i = 0; i < iterations; i++) {
            AtomicReference<Integer> tempBest = new AtomicReference<>(0);
            ArrayAtomicReference<Boolean> tempSolution = new ArrayAtomicReference<>();
            AtomicReference<Ant> bestAnt = new AtomicReference<>();
            antList.forEach(ant -> {
                Boolean[] solution = ant.findSolution();
                if(ant.getCurrentValue() > tempBest.get()) {
                    tempBest.set(ant.getCurrentValue());
                    tempSolution.set(solution);
                    bestAnt.set(ant);
                }
            });

            bestAnt.get().leavePheromones();
            bestAnt.get().resetAnt(capacity);

            if(globalBest < tempBest.get()) {
                globalBest = tempBest.get();
                globalSolution = tempSolution.get();
            }

            evaporate(ro, tauMax, tauMin);
        }

        System.out.println(globalBest);

        /*
        for(int i = 0; i < globalSolution.length; i++) {
            if(globalSolution[i])
                System.out.print(i + " ");
        }
        */
    }

    public static void main(String[] args) {
        try {
            readInput("data/data_1.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }

        int numberOfAnts = 5;
        int numberOfIterations = 5;
        double alpha = 1.0;
        double beta = 2.0;
        double ro = 0.90;
        double tauMax = Double.MAX_VALUE;
        double tauMin = 0.6;

        antList = new ArrayList<>(numberOfAnts);
        for(int i = 0; i < numberOfAnts; i++)
            antList.add(new Ant("Ant " + i, itemList, bestValue, capacity, alpha, beta, tauMax, tauMin));

        solve(ro, tauMax, tauMin, numberOfIterations);
    }
}