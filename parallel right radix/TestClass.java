/**
 *TestClass.java
 *
 *Class that test and times the parallel right radix algorithm.
 *
 *@author: Jonas Hanetho
 */

import java.util.*;

public class TestClass {

    double startTime, endTime;
    int[] testArray, initArray;
    static ParaRightRadix test = new ParaRightRadix();
    
    /**
     *Initializes the test class. 
     *It takes 1 argument, which is the length of the array to be sorted.
     */
    public static void main(String[] args) {
	if (args.length == 1) new TestClass(Integer.parseInt(args[0]));
	else System.out.println("Correct usage: java ParaRadixSort <n>");
    }

    /**
     *Creates an instance of the ParaRightRadix class and runs the sorting algorithm.
     *It times the algorithm and checks that it is sorted correctly.
     *If it is the time the algorithm used is printed together with the results from the sorting.
     */
    TestClass(int n) {
	testArray = generateRandomArray(n);
	initArray = new int[n];
	System.arraycopy(testArray, 0, initArray, 0, n);

	startTime = System.nanoTime();
	testArray = test.paraMultiRadix(testArray);
	endTime = ((System.nanoTime()-startTime)/1000000.0);

	if (correctlySorted(testArray)) {
	    System.out.println("\n----------------\nCorrectly sorted array with "+n+" numbers in: "+endTime+" ms!");
	    System.out.println("\nBefore sorting:\n"+Arrays.toString(initArray));
	    System.out.println("\nAfter sorting:\n"+Arrays.toString(testArray));
	}
	else System.out.println("Something went wrong when sorting the array!");
    }
    
    /**
     *Generates a random array. Takes 1 argument:
     *<n> = length of array to be generated.
     *Returns the random generated array.
     */
    int[] generateRandomArray(int n) {
	int[] randomArray = new int[n];
	Random r = new Random(7361);
	for (int i = 0; i < n; i++) randomArray[i] = r.nextInt(n);
	return randomArray;
    }
    
    /**
     *Test whether an array is correctly sored. Takes 1 argument:
     *<testArray> = array to be tested.
     *Returns true if <testArray> is correctly sorted, or false if it isn't.
     */
    boolean correctlySorted(int[] testArray) {
	for (int i = 1; i < testArray.length; i++) {
	    if (testArray[i-1] > testArray[i]) return false;
	}
	return true;
    }
}