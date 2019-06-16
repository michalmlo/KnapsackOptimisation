package main;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {

		Knapsack kn = new Knapsack("C:\\Users\\micha\\Documents\\School\\sem4\\NAI\\Knapsack_data\\7");
		
		int[] result = kn.hillClimbing();
		
		System.out.println(Arrays.toString(result));
		
		
		kn.translateVector(result);

	
		
	}

}
