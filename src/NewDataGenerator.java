import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class NewDataGenerator {
	
	final static Random R = new Random();

	public static void generateData(int capacity, int numberOfItems, int maxWeight,
			int maxValue, int label) {
		
		try {
			File file = new File("instances/data_" + label + ".txt");
			file.createNewFile();
			
			PrintWriter pr = new PrintWriter(file);
			
			pr.print(Integer.toString(numberOfItems) + " "
			+ Integer.toString(capacity) + "\r\n");

			for(int i = 0; i < numberOfItems; i++) {
				int value = R.nextInt(maxValue) + 1;
				int weight = R.nextInt(maxWeight) + 1;
				
				pr.print(Integer.toString(weight) + " " + Integer.toString(value));
				if(i != numberOfItems - 1)
					pr.write("\r\n");
			}
			
			pr.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
