import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class InstanceGenerator {

    private int size;
    private int weightBound;
    private int valueBound;
    private int label;

    private int instance;

    private final Random r = new Random();

    InstanceGenerator(int size, int weightBound, int valueBound, int label) {
        this.size = size;
        this.weightBound = weightBound;
        this.valueBound = valueBound;
        this.label = label;

        instance = 0;
    }

    Pair<Integer, List<KnapsackObject>> generate()
            throws IOException  {
        List<KnapsackObject> objects = new ArrayList<>(size);

        int limit = r.nextInt(weightBound - 1) + 1;

        PrintWriter writer = new PrintWriter("gen" + label + "instance" + instance + ".json");

        writer.println("{");
        writer.println("  \"limit\":" + limit + ",");
        writer.println("  \"objectNumber\":" + size + ",");
        writer.println("  \"set\": [");

        for(int i = 0; i < size; i++) {
            KnapsackObject object = new KnapsackObject(r.nextInt(limit - 1) + 1, Math.abs(r.nextInt( valueBound)));
            objects.add(object);

            writer.println("    {\n" +
                    "      \"weight\":" + object.weight() + ",\n" +
                    "      \"value\":" + object.value() + "\n" +
                    "    }" + (i == size - 1 ? "" : ","));
        }

        writer.println("  ]");
        writer.println("}");

        writer.close();

        instance++;

        return new Pair<>(limit, objects);
    }
}
