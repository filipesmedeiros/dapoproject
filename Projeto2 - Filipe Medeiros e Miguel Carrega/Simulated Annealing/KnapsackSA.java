package SA;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class KnapsackSA {
	
	private static KnapsackObject ks;
	
	//USAGE: 7 parameters
	// arg[0] = coolingFactor
	// arg[1] = termination temperature
	// arg[2] = initial temperature
	// arg[3] = neighbourSamplingSize
	// arg[4] = mode initial solution (0 - start empty, 1 - start random, 2 - start greedy)
	// arg[5] = fileName to import (ex: data_1.txt)
	// arg[6] = number of iterations
	// ex: 0.99 0.2 100.0 100 0 data_1.txt 100
	public static void main(String[] args) {
		
		try {
			
			double cFac = Double.parseDouble(args[0]);
			double tTemp = Double.parseDouble(args[1]);
			double iTemp = Double.parseDouble(args[2]);
			int nrNeighbours = Integer.parseInt((args[3]));
			int initMode = Integer.parseInt((args[4]));
			
			importFile(args[5]);
			
			solveSA(cFac,tTemp,iTemp,nrNeighbours,initMode, Integer.parseInt(args[6]));
		} catch (FileNotFoundException e) {
			System.out.println("Error reading file");
			e.printStackTrace();
		}
	}
	
	private static void solveSA(double cFac, double tTemp, double iTemp, int nrNeighbours, int initMode,int iterations) {

		
		int it = iterations;
		int sumW = 0;
		int sumV = 0;
		int sumTakenTime = 0;
		for (int i = 0; i < it; i++) {
			
			Solver solver = new Solver(ks, iTemp, cFac, tTemp, nrNeighbours,initMode);
			Solution solution = solver.find();
			sumW += solution.getGainedWeight();
			sumV += solution.getGainedValue();
			sumTakenTime +=  solution.getTakenTime();
		}
		

		System.out.println("Total Value: " + sumV/it);
		System.out.println("Total Weigth: " + sumW/it);
        System.out.println("SA Time: " + sumTakenTime/it + " ms");
    }
	
	//imports file
	public static void importFile(String fileName) throws FileNotFoundException {
		
		File file = new File(FileSystems.getDefault().getPath(fileName).toAbsolutePath().toString());
		Scanner sc = new Scanner(file); 
		
		String line  = sc.nextLine();
	
		int maxCap = Integer.parseInt(line.split(" ")[1]);
		int numItems = Integer.parseInt(line.split(" ")[0]);
		List<Item> items = new ArrayList<Item>();
		
		for(int i = 0; i < numItems; i++) {
			
			line = sc.nextLine();
			
			Item item = new Item(Integer.parseInt(line.split(" ")[0]), Integer.parseInt(line.split(" ")[1]));
			items.add(item);
		}
		sc.close();
		ks = new KnapsackObject(maxCap,items);
	}


}
