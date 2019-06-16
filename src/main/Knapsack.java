package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class Knapsack {
	
	private Pair<Integer,Integer>[] entries;
	private int limit;
	
	public Knapsack(String dir) {
		loadEntries(dir);
	}
	
	@SuppressWarnings("unchecked")
	private void loadEntries(String dir) {
		
		if(!Files.exists(Paths.get(dir)) 
				|| Files.isDirectory(Paths.get(dir)) 
				|| !Files.isReadable(Paths.get(dir))) {
			System.err.println("Wrong input file!");
			System.exit(1);
		}
	
		try(BufferedReader br = new BufferedReader(
				new FileReader (dir))) {
						
			Path path = Paths.get(dir);
			int lineCount =(int)Files.lines(path).count();
			
			System.out.println(lineCount);
			
			entries = new Pair[lineCount - 1];
			String line = "";
				
			limit = Integer.parseInt(br.readLine());
			System.out.println(limit);
						
			for(int i = 0; (line = br.readLine()) != null; i++ ){
				entries[i] = new Pair<Integer, Integer>(Integer.parseInt(line.split(" ")[1]),
														Integer.parseInt(line.split(" ")[0]));
			}
			
			System.out.println(Arrays.toString(entries));
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}	
	
	public int[] bruteForce() {

		int entriesSize = entries.length;
		
		int [] currVect = new int [entriesSize],
				bestVect = new int [entriesSize];
		
		int currVal = 0, bestVal = 0, 
				currWeight = 0, bestWeight = 0;
		
		for(int i = 0; i < (1<<entriesSize); i++) {
			
			for(int j = 0; j < entriesSize; j++) {
				
				if((i & (1<<j)) > 0)
					currVect[j] = 1;
				else
					currVect[j] = 0;
			}

			for(int k = 0; k < currVect.length; k++) {
				
				if(currVect[k] == 1) {
					currVal += entries[k].getValue();
					currWeight += entries[k].getWeight();
				}
				if(currWeight > limit)
					break;
			}
			
			if(currVal > bestVal && currWeight <= limit) {
				bestVal = currVal;
				bestWeight = currWeight;
				System.arraycopy(currVect, 0, bestVect, 0, bestVect.length);
			}
			currVal = 0;
			currWeight = 0;
		}
		
		System.out.println();
		System.out.println("Biggest value = " + bestVal);
		System.out.println("Corresponding weight = " + bestWeight);
		
		for(int i = 0; i < entriesSize; i++) {
			if(bestVect[i] == 1)
				System.out.print(entries[i] + " ");
		}
		
		 return bestVect;
	}
	
	private int[][] getNeighbours(int[] currentVect){
		
		int entriesLen = entries.length;
		int [][] neighbours = new int [entriesLen][entriesLen];		
		int[] neighbCandidate = new int [entriesLen];
		
		
		for(int i = 0; i < entriesLen; i++) {
			if(currentVect[i] == 1) {
				System.arraycopy(currentVect, 0, neighbCandidate, 0, entriesLen);
				neighbCandidate[i] = 0;
				if(acceptableWeight(neighbCandidate))
					System.arraycopy(neighbCandidate, 0, neighbours[i], 0, entriesLen);
			}
			else {
				System.arraycopy(currentVect, 0, neighbCandidate, 0, entriesLen);
				neighbCandidate[i] = 1;
				if(acceptableWeight(neighbCandidate))
					System.arraycopy(neighbCandidate, 0, neighbours[i], 0, entriesLen);
			}	
		}
		
		return neighbours;
	}
	
	private int getValue (int[] currentVect) {
		int val = 0;
		for(int i = 0; i < currentVect.length; i++) {
			if(currentVect[i] == 1)
				val += entries[i].getValue();
		}
		return val;
	}
	
	private int[] randSolution() {
		
		int [] randVect = new int[entries.length];
		Random rand = new Random();
		boolean randBool;
		int currWeight = 0;
		for(int i = 0; i < randVect.length; i++) {
			if(currWeight <= limit) {
				randBool = rand.nextBoolean();
				if(randBool && currWeight + entries[i].getWeight() <= limit) {
					randVect[i] = randBool ? 1 : 0;
					currWeight += entries[i].getWeight();
				}
				else
					randVect[i] = 0;
			}
			else
				randVect[i] = 0;
		}

		System.out.println(Arrays.toString(randVect));
		
		return randVect;
	}

	private boolean acceptableWeight (int[] currentVect) {
		int weight = 0;
		for(int i = 0; i < currentVect.length; i++) {
			if(currentVect[i] == 1)
				weight += entries[i].getWeight();
		}
		return weight <= limit;
		
	}
	
	private boolean hasBetterNeighbours(int[] currentVect) {
		
		int currentVal = getValue(currentVect);
		
		for(int[] vect: getNeighbours(currentVect)) 
			if(getValue(vect) > currentVal)
				return true;
		
		return false;
	}
	
	private int[] randomNeighbour(int[] currentVect) {	
		Random rand = new Random();
		
		return getNeighbours(currentVect)
				[rand.nextInt(currentVect.length)];
	}

	private double transitionProb(int[] current, int[] candid, double temp) {
		return Math.pow(Math.E,
				-1.0 * Math.abs(getValue(current) - getValue(candid))/temp);
		

	}
	
	public boolean transition() {return false;}
	
	public int[] hillClimbing() {

		int [] current = randSolution();
		int [][] neighbours = new int [current.length][current.length];
		int bestVal  = getValue(current), bestValIdx = -1;

		while(hasBetterNeighbours(current)) {
					
			neighbours = getNeighbours(current);
			
			for(int i = 0; i < neighbours.length; i++) {
				
				if(getValue(neighbours[i]) > bestVal) {		
					bestVal = getValue(neighbours[i]);
					bestValIdx = i;
				}	
			}
			
			current = neighbours[bestValIdx];
			/*
			for(int [] neighbour : getNeighbours(current)) {
				//the best one not any better
				if(getValue(neighbour) > currentVal) {
					current = neighbour;
					break;
				}
			}*/
		}
		return current;
	}
		
	public void translateVector(int[] charactVect) {
		
		int weight = 0, val = 0;
		
		for(int i = 0; i < charactVect.length; i++) {
			if(charactVect[i] == 1) {
				weight += entries[i].getWeight();
				val += entries[i].getValue();
				System.out.print(entries[i] + " ");
			}
		}
		System.out.println();
		System.out.println("Total weight: " + weight);
		System.out.println("Total value: " + val);
	}

	public int[] simulatedAnnealing() {
		
		int[] current = randSolution();
		int[] candidate;
		while(true /*insert stop condition here*/) {
			
			candidate = randomNeighbour(current);
			if(acceptableWeight(candidate)) {
				if(getValue(candidate) > getValue(current))
					current = candidate;
				//else if()
					
				
			}
			
		}
	}
}	

class Pair<W extends Comparable<W> ,V extends Comparable<V>> implements Comparable<Pair<W,V>>{
	
	static int seed = 0;
	private W w;
	private V v;
	private int id;
	
	Pair(W w, V v){
		this.w = w;
		this.v = v;
		id = seed++;
	}
	
	public W getWeight() {
		return w;
	}
	
	public V getValue() {
		return v;
	}
	
	public int getId() {
		return id;
	}
	
	@Override 
	public int compareTo(Pair<W, V> other) {
		return v.compareTo(other.v);
	}

	@Override
	public String toString() {
		return "(" + w + ", " + v + ")";
	}

	

	
}

