import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.json.*;

public class Main {

    private static Pair<List<KnapsackObject>, Integer> parseSetFromJSON(String path)
            throws Exception {

        // Linear time and space complexity O(n) with n = number of objects
        // But this will not be considered for complexity calculations, as it it just reading the input
        // and not actually a part of the algorithm
        try {
            String inputString = new String(Files.readAllBytes(Path.of(path)));

            JSONObject jsonObject = new JSONObject(inputString);

            int objectNumber = jsonObject.getInt("objectNumber");

            Pair<List<KnapsackObject>, Integer> parsed = new Pair<>(new ArrayList<>(objectNumber), 0);

            parsed.setValue(jsonObject.getInt("limit"));

            JSONArray objects = jsonObject.getJSONArray("set");
            objects.forEach(object -> parsed.key().add(new KnapsackObject(((JSONObject) object).getInt("weight"),
                    ((JSONObject) object).getInt("value"))));

            return parsed;
        } catch(IOException e) {
            System.out.println("Error reading JSON file");
            throw new Exception();
        }
    }

    private static int greedy01Knapsack(List<KnapsackObject> objects, int limit) {
        // Set<KnapsackObject> solution = new HashSet<>(objects.size());
        TreeMap<Float, KnapsackObject> sortedObjects = new TreeMap<>((x, y) -> x < y ? 1 : x.equals(y) ? 0 : -1);

        // Time complexity - O(n*log(n)) where n = number of objects
        // For each iteration, insertion is O(log(n))
        // Space complexity - O(n) where n = number of objects
        objects.forEach(object -> sortedObjects.put(1.0f * object.value() / object.weight(), object));

        // Constant time and space complexity | these help with the forEach loop
        AtomicReference<Integer> currentWeight = new AtomicReference<>(0);
        AtomicReference<Integer> currentValue = new AtomicReference<>(0);
        AtomicReference<Integer> maxValue = new AtomicReference<>(0);
        AtomicReference<KnapsackObject> maxObject = new AtomicReference<>(null);

        // Linear time and space complexity - O(n) where n = number of objects
        // All operations inside are constant in time and space
        sortedObjects.forEach((profit, object) -> {
            if(currentWeight.get() + object.weight() <= limit) {
                currentWeight.accumulateAndGet(object.weight(), (x, y) -> x + y);
                // solution.add(object);
                currentValue.accumulateAndGet(object.value(), (x, y) -> x + y);
            }

            if(object.weight() <= limit && object.value() > maxValue.get()) {
                maxValue.set(object.value());
                maxObject.set(object);
            }
        });

        if(maxValue.get() > currentWeight.get()) {
            // System.out.print("    { weight: " + maxObject.get().weight() +
            //      " }, { value: " + maxObject.get().value() + " }");

            // Space and time complexity - O(1)
            return maxObject.get().value();
        } else {
            // solution.forEach(object -> System.out.print("    \n{ weight: " + object.weight() +
            //      " }, { value: " + object.value() + " }"));

            // Space and time complexity - O(1)
            return currentValue.get();
        }
    }

    private static int dynamicPoly01Knapsack(List<KnapsackObject> objects, double precision, int limit) {
        List<KnapsackObject> rounded = roundValues(objects, precision);

        return dynamic01Knapsack(rounded, limit);
    }

    @SuppressWarnings("all")
    private static int dynamic01Knapsack(List<KnapsackObject> objects, int limit) {

        // Note: Since used list is ArrayList, all get operations of the list are O(1) in time complexity

        // Time complexity - O(n * limit) where n = number of objects and limit = maximum weight in knapsack
        // Space complexity - O(n^2 * limit) where n = number of objects and limit = maximum weight in knapsack
        int[][] dynamicTable = new int[objects.size() + 1][limit + 1];

        // Time complexity - O(limit) where limit = maximum weight in knapsack
        for(int i = 0; i < limit + 1; i++)
            dynamicTable[0][i] = 0;

        // Time complexity - O(n) where n = number of objects
        for(int i = 0; i < objects.size() + 1; i++)
            dynamicTable[i][0] = 0;

        // Time complexity - O(n^2 * limit) where n = number of objects and limit = maximum weight in knapsack
        for(int i = 1; i < objects.size() + 1; i++)
            for(int j = 1; j < limit + 1; j++) {
                KnapsackObject object = objects.get(i - 1);

                // Everything in this if clause is constant in time complexity
                if(object.weight() > j)
                    dynamicTable[i][j] = dynamicTable[i - 1][j];
                else {
                    // Time and space complexity - O(1)
                    boolean putThisOne = object.value() +
                            dynamicTable[i - 1][j - object.weight()]
                            > dynamicTable[i - 1][j];

                    if(putThisOne) {
                        // Space and time complexity - O(1)
                        dynamicTable[i][j] = object.value() +
                                dynamicTable[i - 1][j - object.weight()];
                    } else {
                        // Space and time complexity - O(1)
                        dynamicTable[i][j] = dynamicTable[i - 1][j];
                    }
                }
            }

        // Time complexity - O(1)
        // Space complexity - O(n) where n = number of objects
        HashSet<KnapsackObject> solution = new HashSet<>(objects.size());

        int i = dynamicTable.length - 1;
        int j = dynamicTable[0].length - 1;

        // Time complexity - O(n) where n = number of objects, since only iterate that dimension of the matrix
        // The other dimension is a helper to decide what cell to evaluate next
        for(;;) {
            if(dynamicTable[i][j] != dynamicTable[i - 1][j]) {
                solution.add(objects.get(i - 1));
                j -= objects.get(i - 1).weight();
            }

            i--;

            if(i == 0 || j == 0)
                break;
        }

        // solution.forEach(object -> System.out.print("    { weight: " + object.weight() +
        //      " }, { value: " + object.value() + " }\n"));

        // Space and time complexity - O(1)
        return dynamicTable[objects.size()][limit];
    }

    private static List<KnapsackObject> roundValues(List<KnapsackObject> objects, double precision) {
        List<KnapsackObject> rounded = new ArrayList<>();

        objects.forEach(object -> rounded.add(new KnapsackObject(object.weight(), (int) Math.ceil(object.value() * precision))));

        return rounded;
    }

    public static void main(String[] args)
            throws Exception {

        // This part of the code generates the test instances
        // You have to move the files to the "instances" directory after generated
        /*
        InstanceGenerator gen1 = new InstanceGenerator(6, 30, 500, 1);
        InstanceGenerator gen2 = new InstanceGenerator(20, 800, 150, 2);
        InstanceGenerator gen3 = new InstanceGenerator(400, 500, 300, 3);
        InstanceGenerator gen4 = new InstanceGenerator(600, 150, 4000, 4);

        for(int i = 0; i < 5; i++) {
            gen1.generate();
            gen2.generate();
            gen3.generate();
            gen4.generate();
        }
        */

        // Statistics
        double greedyAccuracyAvg = 0;
        double dynamicAccuracyAvg = 0;
        long greedyTimeAvg = 0;
        long dynamicTimeAvg = 0;

        // Some parameters for the test, have to be in accordance with the instance files
        final int numberOfGenerators = 4;
        final int numberOfInstancesPerGenerator = 5;
        final int reps = 10; // Repeat for consistency reasons

        PrintWriter writer = new PrintWriter("output.txt", StandardCharsets.UTF_8);

        // This is just so the PrintWriter is ready, or else it won't write anything
        Thread.sleep(2000);

        for(int x = 0; x < reps; x++)
            for(int i = 1; i <= numberOfGenerators; i++)
                for(int j = 0; j < numberOfInstancesPerGenerator; j++) {
                    Pair<List<KnapsackObject>, Integer> parsed = parseSetFromJSON("instances/gen" + i + "instance" + j + ".json");
                    writer.println(" ------ Computing solutions for instance " + j + " of generator " + i +  " ------ \n");

                    int optimalSolution = dynamic01Knapsack(parsed.key(), parsed.value());
                    writer.println("    Optimal solution = " + optimalSolution);

                    long timer = System.nanoTime();
                    int greedySolution = greedy01Knapsack(parsed.key(), parsed.value());
                    greedyTimeAvg += System.nanoTime() - timer;

                    writer.println("\n  The computed solution is (using a greedy algorithm):");
                    writer.println("    Total value = " + greedySolution);

                    double performance = (1.0 * greedySolution) / optimalSolution;
                    writer.println("    Performance = " + greedySolution + " / " + optimalSolution + " = " + performance);
                    greedyAccuracyAvg += performance;

                    timer = System.nanoTime();
                    int dynamicPolySolution = dynamicPoly01Knapsack(parsed.key(), 0.75, parsed.value());
                    dynamicTimeAvg += System.nanoTime() - timer;

                    writer.println("\n  The computed solution  is (using dynamic programming with ):");
                    writer.println("    Total value = " + dynamicPolySolution);

                    performance = (1.0 * dynamicPolySolution) / optimalSolution;
                    writer.println("    Performance = " + dynamicPolySolution + " / " + optimalSolution + " = " + performance);
                    dynamicAccuracyAvg += performance;

                    writer.println("------ x ------");
                }

        greedyAccuracyAvg /= numberOfGenerators * numberOfInstancesPerGenerator * reps;
        dynamicAccuracyAvg /= numberOfGenerators * numberOfInstancesPerGenerator * reps;
        greedyTimeAvg /= numberOfGenerators * numberOfInstancesPerGenerator * reps;
        dynamicTimeAvg /= numberOfGenerators * numberOfInstancesPerGenerator * reps;

        writer.println("Greedy algorithm accuracy performance average: " + greedyAccuracyAvg);
        writer.println("Greedy algorithm time performance average: " + greedyTimeAvg);
        writer.println("Dynamic programming algorithm accuracy performance average: " + dynamicAccuracyAvg);
        writer.println("Dynamic programming algorithm time performance average: " + dynamicTimeAvg);

        writer.close();
    }
}
