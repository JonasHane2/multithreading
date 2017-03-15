/**
 *MaxWorker.java
 *
 *Finds the largest value in an int array.
 *
 *@author: Jonas Hanetho
 */

public class MaxWorker extends Thread {
	
    int max = 0, index, start, end;
    int[] array;
    
    MaxWorker(int[] a) {
	this.array = a;
    }
    
    public void run() {
	for (int i = 0; i < array.length; i++) if (array[i] > max) max = array[i];
    }
    
    public int getMax() {
	return max;
    }
}