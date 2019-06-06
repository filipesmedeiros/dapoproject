import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class Main {
	
	private static int capacity;
	private static int[] weights;
	private static int[] values;
	
	private static void lol() {
		
	}
	
	private static void readInput(String fileName) {
		try {
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
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final void main(String[] args) {
		readInput("instances/data_1.txt");
		
		int numberOfAnts = 1;
		
	}
}