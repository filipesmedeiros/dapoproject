import java.io.IOException;
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
            objects.forEach(object -> parsed.key.add(new KnapsackObject(((JSONObject) object).getInt("weight"),
                    ((JSONObject) object).getInt("value"))));

            return parsed;
        } catch(IOException e) {
            System.out.println("Error reading JSON file");
            throw new Exception();
        }
    }

    private static int greedyAlgorithm01Knapsack(List<KnapsackObject> objects, int limit, int instance) {

        Set<KnapsackObject> solution = new HashSet<>();
        TreeMap<Float, KnapsackObject> sortedObjects = new TreeMap<>((x, y) -> x < y ? 1 : x.equals(y) ? 0 : -1);

        // Time complexity - O(n*log(n)) where n = number of objects
        // For each iteration, insertion in O(log(n))
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
                solution.add(object);
                currentValue.accumulateAndGet(object.value(), (x, y) -> x + y);
            }

            if(object.weight() <= limit && object.value() > maxValue.get()) {
                maxValue.set(object.value());
                maxObject.set(object);
            }
        });

        if(maxValue.get() > currentWeight.get()) {
            System.out.println("  The computed solution for instance " + instance + " is (using a greedy algorithm):");
            System.out.print("    { weight: " + maxObject.get().weight() +
                    " }, { value: " + maxObject.get().value() + " }");

            // Space and time complexity - O(1)
            return maxObject.get().value();
        } else {
            System.out.println("  The computed solution for instance " + instance + " is (using a greedy algorithm):");
            solution.forEach(object -> System.out.print("    \n{ weight: " + object.weight() +
                    " }, { value: " + object.value() + " }"));

            // Space and time complexity - O(1)
            return currentValue.get();
        }
    }

    @SuppressWarnings("all")
    private static int dynamic01Knapsack(List<KnapsackObject> objects, int limit, int instance) {

        // Note: Since used list is ArrayList, all get operations of the list are O(1) in time complexity

        // Note: Both the space complexity of the dynamic table and the time complexity of the loop that fills
        // the table have one extra n because my implementation wants to store the actual objects of the solution
        // and not just its value. It would be very easy to just remove the HashSets (and consequently, all the
        // operations on them) from the table, and reduce the space and time complexity to O(n * limit)

        // Space complexity - O(n^2 * limit) where n = number of objects and limit = maximum weight in knapsack
        Pair<Set<KnapsackObject>, Integer>[][] dynamicTable = new Pair[objects.size() + 1][limit + 1];

        // Time complexity - O(limit) where limit = maximum weight in knapsack
        for(int i = 0; i < limit + 1; i++)
            dynamicTable[0][i] = new Pair<>(new HashSet<>(), 0);

        // Time complexity - O(n) where n = number of objects
        for(int i = 0; i < objects.size() + 1; i++)
            dynamicTable[i][0] = new Pair<>(new HashSet<>(), 0);

        // Time complexity - O(n^2 * limit) where n = number of objects and limit = maximum weight in knapsack
        for(int i = 1; i < objects.size() + 1; i++)
            for(int j = 1; j < limit + 1; j++) {

                KnapsackObject object = objects.get(i - 1);

                // Everything in this if clause is constant in time complexity
                if(object.weight() > j)
                    dynamicTable[i][j] = new Pair<>(dynamicTable[i - 1][j].key(),
                            dynamicTable[i - 1][j].value());
                else {
                    // Time and space complexity - O(1)
                    boolean putThisOne = object.value() +
                            dynamicTable[i - 1][j - object.weight()].value()
                            > dynamicTable[i - 1][j].value();

                    Set<KnapsackObject> temp;

                    if(putThisOne) {
                        // Time complexity - O(n) where n = number of elements
                        temp = new HashSet<>(dynamicTable[i - 1][j - object.weight()].key());

                        // Space and time complexity - O(1)?
                        // I think the above operation creates a new HashSet from the given one, but with capacity and load
                        // factors such that adding a new element makes the program have to resize the set, in which case
                        // this new addition will be very time expensive (O(n)). If so, creating a new HashSet manually would
                        // fix this
                        temp.add(object);

                        // Space and time complexity - O(1)
                        dynamicTable[i][j] = new Pair<>(temp, object.value() +
                                dynamicTable[i - 1][j - object.weight()].value());
                    } else {
                        // Time complexity - O(n) where n = number of elements
                        temp = new HashSet<>(dynamicTable[i - 1][j].key());

                        // Space and time complexity - O(1)
                        dynamicTable[i][j] = new Pair<>(temp, dynamicTable[i - 1][j].value());
                    }
                }
            }

        System.out.println("\n\n  The computed solution for instance " + instance + " is (using dynamic programming):");
        dynamicTable[objects.size()][limit].key().forEach(object -> System.out.print("    { weight: " + object.weight() +
                " }, { value: " + object.value() + " }\n"));

        // Space and time complexity - O(1)
        return dynamicTable[objects.size()][limit].value();
    }

    public static void main(String[] args)
            throws Exception {

        for(int i = 1; i < 3; i++) {
            Pair<List<KnapsackObject>, Integer> parsed = parseSetFromJSON("instances/instance" + i + ".json");
            System.out.println(" ------ Computing solutions for instance " + i + " ------ \n");
            System.out.println("\n    Total value = " + greedyAlgorithm01Knapsack(parsed.key(), parsed.value(), i));
            System.out.println("    Total value = " + dynamic01Knapsack(parsed.key(), parsed.value(), i));
            System.out.println("\n\n ------ x ------");
        }
    }

    static class Pair<K, V> {

        private K key;
        private V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K key() {
            return key;
        }

        V value() {
            return value;
        }

        void setValue(V value) {
            this.value = value;
        }
    }
}
