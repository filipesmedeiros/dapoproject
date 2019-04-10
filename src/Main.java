import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import org.json.*;

public class Main {

    public static void main(String[] args) {

        // All constant
        final int limit;
        Set<KnapsackObject> set = new HashSet<>();
        Set<KnapsackObject> solution = new HashSet<>();

        // Linear time and space complexity O(n) with n = number of objects
        // But this will not be considered for complexity calculations, as it it just reading the input
        // and not actually a part of the algorithm
        try {
            String inputString = new String(Files.readAllBytes(Path.of("instances/instance1.json")));

            JSONObject jsonObject = new JSONObject(inputString);

            limit = jsonObject.getInt("limit");

            JSONArray objects = jsonObject.getJSONArray("set");
            objects.forEach(object -> set.add(new KnapsackObject(((JSONObject) object).getInt("weight"),
                    ((JSONObject) object).getInt("value"))));
        } catch(IOException e) {
            System.out.println("Error reading JSON file");
            return;
        }

        // Constant time complexity
        List<Pair<Float, KnapsackObject>> sortedObjects = new ArrayList<>(set.size());

        // Linear time and space complexity - O(n) where n = number of objects
        set.forEach(object -> sortedObjects.add(new Pair<>(1.0f * object.value() / object.weight(), object)));

        // ???
        sortedObjects.sort((pair1, pair2) -> pair1.key() < pair2.key() ? 1 : pair1.key().equals(pair2.key()) ? 0 : -1);

        // Constant time and space complexity | these help with the forEach loop
        AtomicReference<Integer> currentWeight = new AtomicReference<>(0);
        AtomicReference<Integer> maxValue = new AtomicReference<>(0);
        AtomicReference<KnapsackObject> maxObject = new AtomicReference<>(null);

        // Linear time and space complexity - O(n) where n = number of objects
        // All operations inside are constant in time and space
        sortedObjects.forEach(pair -> {
            if(currentWeight.get() + pair.value().weight() <= limit) {
                currentWeight.accumulateAndGet(pair.value().weight(), (x, y) -> x + y);
                solution.add(pair.value());
            }

            if(pair.value().weight() <= limit && pair.value().value() > maxValue.get()) {
                maxValue.set(pair.value().value());
                maxObject.set(pair.value());
            }
        });

        if(maxValue.get() > currentWeight.get()) {
            System.out.println("The computed solution for this problem is:");
            System.out.print(" { weight: " + maxObject.get().weight() +
                    " }, { value: " + maxObject.get().value() + " }");
        } else {

            System.out.println("The computed solution for this problem is:");
            solution.forEach(object -> System.out.print(" { weight: " + object.weight() +
                    " }, { value: " + object.value() + " }"));
        }

        /*
        System.out.println("Max value:");
        System.out.print(" { weight: " + maxObject.get().weight() +
                " }, { value: " + maxObject.get().value() + " }");

        System.out.println("Set:");
        solution.forEach(object -> System.out.print(" { weight: " + object.weight() +
                " }, { value: " + object.value() + " }"));
        */
    }
}
