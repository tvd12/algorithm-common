package com.tvd12.algorithm.max;

public class BinMax {

	public static int bin(int[] array) {
		return bin(array, 0, array.length);
	}
	
	public static int bin(int[] array, int from, int to) {
		int offset = to - from;
		if(offset == 0)
			throw new IllegalArgumentException("array must not be empty");
		if(offset == 1)
			return array[from];
		if(offset == 2)
			return max(array[from], array[from + 1]);
		int m = (from + to) / 2;
		return max(bin(array, from, m), bin(array, m, to));
	}
	
	public static int max(int a, int b) {
		return a > b ? a : b;
	}
	
	public static int normal(int[] array) {
		return normal(array, 0, array.length);
	}
	
	public static int normal(int[] array, int from, int to) {
		if(to <= from || array.length < to)
			throw new IllegalArgumentException("array must not be empty");
		int max = array[from];
		for(int i = 0 ; i < array.length ; i++) {
			if(max < array[i])
				max = array[i];
		}
		return max;
	}
	
	public static void main(String[] args) {
		long time1 = System.currentTimeMillis();
		for(int i = 0 ; i < 1000000 ; i++)
			bin(new int[] {2,3,3,4,2,2,5,6,7,2});
		long offset1 = System.currentTimeMillis() - time1;
		
		long time2 = System.currentTimeMillis();
		for(int i = 0 ; i < 1000000 ; i++)
			normal(new int[] {2,3,3,4,2,2,5,6,7,2});
		long offset2 = System.currentTimeMillis() - time2;
		
		System.out.println("bin time = " + offset1);
		System.out.println("normal time = " + offset2);
	}
	
}
