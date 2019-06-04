import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    private static int resetCapacity;
    private static int capacity;
    private static List<AntKnapsackItem> itemList;
    private static List<Ant> antList;
    private static int bestValue;

    private static void readInput(String fileName) throws IOException {
        int[] weights;
        int[] values;

        List<String> lines = Files.readAllLines(Paths.get(fileName));

        capacity = Integer.parseInt(lines.get(0).split(" ")[1]);
        resetCapacity = capacity;
        weights = new int[lines.size() - 1];
        values = new int[lines.size() - 1];

        bestValue = 0;

        Iterator<String> iterator = lines.iterator();
        iterator.next();
        int index = 0;
        while(iterator.hasNext()) {
            String[] line = iterator.next().split(" ");
            weights[index] = Integer.parseInt(line[0]);
            values[index] = Integer.parseInt(line[1]);
            if(bestValue < values[index])
                bestValue = values[index];
            index++;
        }

        itemList = new ArrayList<>(weights.length);
        for(int i = 0; i < weights.length; i++)
            // The specific formula for calculating the interest the ants have on an item can vary
            // and this will be stated in the report
            itemList.add(new AntKnapsackItem(weights[i], values[i], (weight, value) -> value / Math.pow(weight, 2)));
    }

    private static void solve(double alpha, double beta, double ro, int iterations) {
        int globalBest = 0;
        Boolean[] globalSolution;
        for(int i = 0; i < iterations; i++) {
            AtomicReference<Integer> tempBest = new AtomicReference<>(0);
            ArrayAtomicReference<Boolean> tempSolution = new ArrayAtomicReference<>();
            antList.forEach(ant -> {
                capacity = ant.chooseNext(alpha, beta, capacity);
                while(capacity > 0) {
                    capacity = ant.chooseNext(alpha, beta, capacity);
                }

                if(tempBest.get() < ant.getCurrentValue()) {
                    tempBest.set(ant.getCurrentValue());
                    tempSolution.set(ant.getSolution());
                }
            });

            if(globalBest < tempBest.get()) {
                globalBest = tempBest.get();
                globalSolution = tempSolution.get();
            }

            antList.forEach(ant -> ant.leavePheromones(bestValue));
            itemList.forEach(item -> item.changePheromoneLevel(pheromoneLevel -> pheromoneLevel * ro));

            capacity = resetCapacity;
        }

        System.out.println(globalBest);
    }

    public static void main(String[] args) {
        try {
            readInput("data/data_1.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }

        int numberOfAnts = 1;
        antList = new ArrayList<>(numberOfAnts);
        for(int i = 0; i < numberOfAnts; i++)
            antList.add(new Ant(itemList));

        solve(1.0, 2.0, 0.1, 1);
    }
}