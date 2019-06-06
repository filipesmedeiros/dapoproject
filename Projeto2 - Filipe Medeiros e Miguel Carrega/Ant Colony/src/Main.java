import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static int loggingDepth = 0;

    private static int capacity;
    private static List<AntKnapsackItem> itemList;
    private static List<Ant> antList;

    // Just reads the input and initializes the data structures
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
        while(iterator.hasNext()) {
            String[] line = iterator.next().split(" ");
            weights[index] = Integer.parseInt(line[0]);
            values[index] = Integer.parseInt(line[1]);
            index++;
        }

        itemList = new ArrayList<>(weights.length);
        for(int i = 0; i < weights.length; i++)
            // The specific formula for calculating the interest the ants have on an item can vary
            // and this will be stated in the report
            itemList.add(new AntKnapsackItem(weights[i], values[i], (weight, value) -> value / Math.pow(weight, 2)));
    }

    // The evaporation is the forgetting method
    // It is used to make sure the ants don't get stuck on a local optimum
    private static void evaporate(double ro, double tauMax, double tauMin) {
        itemList.forEach(item -> item.changePheromoneLevel(
                pheromoneLevel -> clamp(pheromoneLevel * ro, tauMax, tauMin)
        ));
    }

    private static void solve(double ro, double tauMax, double tauMin, int iterations) {
        Boolean[] globalBestSolution = new Boolean[itemList.size()];
        for(int i = 0; i < globalBestSolution.length; i++)
            globalBestSolution[i] = false;
        int globalBestValue = 0;

        long timer = System.currentTimeMillis();

        for(int i = 0; i < iterations; i++) {
            Thread[] antThreads = new Thread[antList.size()];

            int index = 0;
            for(Ant ant : antList) {
                antThreads[index] = new Thread(() -> {
                    ant.findSolution();

                    if (loggingDepth >= 2)
                        System.out.println(ant.name() + " found a solution with value = " + ant.getCurrentValue());
                });
                antThreads[index].start();

                index++;
            }

            for(Thread antThread : antThreads)
                try {
                    antThread.join();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

            for(Ant ant : antList) {
                ant.leavePheromones(globalBestValue);
                if(ant.getCurrentValue() > globalBestValue) {
                    globalBestValue = ant.getCurrentValue();
                    globalBestSolution = ant.getCurrentSolution();
                }
                ant.resetAnt(capacity);
            }

            if(loggingDepth >= 1)
                System.out.println("------\nIteration = " + i + "\n  Current best = " + globalBestValue);

            evaporate(ro, tauMax, tauMin);
        }

        timer = System.currentTimeMillis() - timer;

        int totalWeight = 0;
        for(int i = 0; i < itemList.size(); i++)
            if(globalBestSolution[i])
                totalWeight += itemList.get(i).weight();

        System.out.println("\n --- Solution stats ---");
        System.out.println("   Computed solution = ........  " + globalBestValue);
        System.out.println("   Computed solution (weight) =  " + totalWeight);
        System.out.println("   Compute time = .............  " + timer + "ms");
        System.out.println(" --- Solution stats ---");
    }

    static double clamp(double value, double max, double min) {
        if(value > max)
            return max;
        else if(value < min)
            return min;
        else
            return value;
    }

    public static void main(String[] args) {
        try {
            readInput("data/data_4.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }

        loggingDepth = 2;

        int numberOfAnts = 5;
        int numberOfIterations = 50;
        double alpha = 2.0;
        double beta = 4.0;
        double ro = 0.95;
        double tauMax = 10;
        double tauMin = 0.1;

        antList = new ArrayList<>(numberOfAnts);
        for(int i = 0; i < numberOfAnts; i++)
            antList.add(new Ant("Ant " + i, itemList, capacity, alpha, beta, tauMax, tauMin));

        solve(ro, tauMax, tauMin, numberOfIterations);
    }
}