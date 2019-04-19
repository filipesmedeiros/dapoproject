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

    private static int greedyAlgorithm01Knapsack(List<KnapsackObject> objects, int limit) {

        Set<KnapsackObject> solution = new HashSet<>();
        TreeMap<Float, KnapsackObject> sortedObjects = new TreeMap<>();

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
        sortedObjects.descendingMap().forEach((profit, object) -> {
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
            System.out.println("The computed solution for this problem is (using a greedy algorithm):");
            System.out.print(" { weight: " + maxObject.get().weight() +
                    " }, { value: " + maxObject.get().value() + " }");

            return maxObject.get().value();
        } else {
            System.out.println("The computed solution for this problem is (using a greedy algorithm):");
            solution.forEach(object -> System.out.print(" { weight: " + object.weight() +
                    " }, { value: " + object.value() + " }"));

            return currentValue.get();
        }
    }

    public static int dynamic01Knapsack(List<KnapsackObject> objects, int limit) {
        Pair<Set<KnapsackObject>, Integer>[][] dynamicTable = new Pair[objects.size()][limit];

        System.out.println("\n");

        for(int i = 0; i < limit; i++)
            dynamicTable[0][i] = new Pair<>(new HashSet<>(), 0);

        for(int i = 0; i < objects.size(); i++)
            dynamicTable[i][0] = new Pair<>(new HashSet<>(), 0);

        for(int i = 1; i < objects.size(); i++)
            for(int j = 1; j < limit; j++) {
                if(objects.get(i).weight() > j)
                    dynamicTable[i][j] = new Pair<>(dynamicTable[i - 1][j].key(),
                            dynamicTable[i - 1][j].value());
                else {
                    boolean putThisOne = objects.get(i).value() +
                            dynamicTable[i - 1][j - objects.get(i).weight()].value()
                            > dynamicTable[i - 1][j].value();

                    Set<KnapsackObject> temp;

                    if(putThisOne) {
                        temp = new HashSet<>(dynamicTable[i - 1][j - objects.get(i).weight()].key());
                        temp.add(objects.get(i));
                        dynamicTable[i][j] = new Pair<>(temp, objects.get(i).value() +
                                dynamicTable[i - 1][j - objects.get(i).weight()].value());
                    } else {
                        temp = new HashSet<>(dynamicTable[i - 1][j].key());
                        dynamicTable[i][j] = new Pair<>(temp, dynamicTable[i - 1][j].value());
                    }
                }

                if(j == limit - 1)
                    System.out.println(dynamicTable[i][j].value());
                else
                    System.out.print(dynamicTable[i][j].value() + " " + (Integer.toString(dynamicTable[i][j].value()).length() == 1 ? " " : ""));
            }

        System.out.println("\n\nThe computed solution for this problem is (using dynamic programming):");
        dynamicTable[objects.size() - 1][limit - 1].key().forEach(object -> System.out.print(" { weight: " + object.weight() +
                " }, { value: " + object.value() + " }"));

        return dynamicTable[objects.size() - 1][limit - 1].value();
    }

    public static void main(String[] args)
            throws Exception {
        Pair<List<KnapsackObject>, Integer> parsed = parseSetFromJSON("instances/instance1.json");
        greedyAlgorithm01Knapsack(parsed.key(), parsed.value());
        dynamic01Knapsack(parsed.key(), parsed.value());
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

        void setKey(K key) {
            this.key = key;
        }

        void setValue(V value) {
            this.value = value;
        }
    }
}
