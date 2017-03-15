/**
 *ParaRightRadix.java
 *
 *Class that performs a parallel right radix algorithm.
 *
 *@author: Jonas Hanetho
 */

import java.util.*;
import java.util.concurrent.*;

public class ParaRightRadix {
    
    int max, numBits, numDigits, NUM_BIT = 9, numCores = Runtime.getRuntime().availableProcessors();
    int[] array, a, b, bits, sumCount;
    int[][] allCount;
    CyclicBarrier cb;

    /**
     *Radix sort for parallell solution. Takes 1 argument (<array>)
     *<array> = array to be sorted
     *
     */
    int[] paraMultiRadix (int[] usrArray) {

	this.array = usrArray;
	this.a = array;
	this.b = new int[array.length];
	this.max = paraFindMax(array);
	numBits = (((int)(Math.log(max)/Math.log(2)))+1);
	numDigits = (((numBits-1)/NUM_BIT)+1);
	bits = new int[numDigits];
	
	for (int i = 0, rest = (numBits%numDigits), sum = 0; i < bits.length; sum += bits[i], a = array, array = b, b = a, i++) {
	    bits[i] = (numBits/numDigits);
	    if (rest-- > 0) bits[i]++;
	    paraRadixSort(bits[i], sum);
	}	    
	if ((bits.length % 2) != 0) System.arraycopy(array, 0, b, 0, array.length);
	return array;
    }
    
    
    /**
     *This function is a parallel solution to radix sorting. It takes two arguments (<maskLen>, <shift>)
     *<maskLen> = masking used on the values sorted
     *<shift> = the ammount of shifting needed on the values sorted
     *
     *This class initializes the threads that performs the sorting.
     */
    void paraRadixSort(int maskLen, int shift) {
	
	BSortWorker[] bsw = new BSortWorker[numCores];	
	allCount = new int[numCores][(1 << maskLen)];
	sumCount = new int[(1 << maskLen)];
	cb = new CyclicBarrier(numCores);
	
	for (int i = 0; i < numCores; i++) (bsw[i] = new BSortWorker(i, maskLen, shift)).start();
	for (BSortWorker s : bsw) try { s.join(); } catch (Exception e) {}
    }
    
    /**
     *The thread class that is used to radix sort arrays.
     */
    public class BSortWorker extends Thread {
	
	int index, start, end, mask, shift;
	int[] count, localCount;
	
	BSortWorker(int index, int maskLen, int shift) {
	    this.index = index;
	    this.mask = (1 << maskLen) -1;
	    this.shift = shift;
	    this.count = new int[mask+1];
	    this.localCount = new int[mask+1];
	}
	
	public void run() {	    
	    b1();
	    try { cb.await(); } catch (Exception e) { return; }
	    b2();
	    try { cb.await(); } catch (Exception e) { return; }
	    c();
	    d();
	}
	
	/**
	 *Count up all values in this threads part of <array> to this threads part of <allCount>
	 */
	void b1() {
	    this.start = (index*(array.length/numCores));
	    this.end = ((index+1)*(array.length/numCores));
	    for (int i = start; i < end; i++) allCount[index][(array[i]>>> shift) & mask]++;
	}
	
	/**
	 *Add togheter all the values from all threads from <allCount> to <sumCount>
	 */
	void b2() {
	    this.start = (index*(count.length/numCores));
	    this.end = ((index+1)*(count.length/numCores));	
	    for (int i = start; i < end; i++) {
		for (int j = 0; j < allCount.length; j++) sumCount[i] += allCount[j][i];
	    }
	}
	
	/**
	 *Add the values together in an unique array <localCount> for each thread.
	 */
	void c() {	
	    int j = 0, acumVal = 0;
	    for (int i = 0; i < localCount.length; i++) {
		for (int k = 0; k < index; k++) j+= allCount[k][i];
		localCount[i] = (acumVal+j);
		j = 0;
		acumVal += sumCount[i];
	    }
	}
	
	/**
	 *Move elements from <a> to <b> using <localCount>
	 */
	void d() {
	    this.start = (index*(array.length/numCores));
	    this.end = ((index+1)*(array.length/numCores));
	    for (int i = start; i < end; i++) b[localCount[(array[i]>>>shift) & mask]++] = array[i];
	}
    }

    /**
     *Multithreaded solution to the findMax problem. 
     *Returns the largest value in the array <array> given in arguments.
     */
    int paraFindMax(int[] array) {
	int max = 0;
	MaxWorker[] workers = new MaxWorker[numCores];
	for (int i = 0; i < workers.length; i++) {
	    (workers[i] = new MaxWorker(Arrays.copyOfRange(array, (i*(array.length/numCores)), ((i+1)*(array.length/numCores))))).start();
	}
	for (MaxWorker w : workers) {
	    try { w.join(); } catch (Exception e) {}	
	    if (w.getMax() > max) max = w.getMax();
	}
	return max;
    }
}