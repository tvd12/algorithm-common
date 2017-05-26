package com.tvd12.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateAllPosibleArrayWithLengthAndMaxValue {

	protected final int min;
	protected final int max;
	protected final int size;
	
	protected transient List<int[]> result;
	
	protected GenerateAllPosibleArrayWithLengthAndMaxValue(Builder builder) {
		this.min = builder.min;
		this.max = builder.max;
		this.size = builder.size;
	}
	
	public List<int[]> generate() {
		prepare();
		generate(newArray(), 0, min);
		return result;
	}
	
	protected void prepare() {
		this.result = new ArrayList<>();
	}
	
	protected void generate(int[] array, int index, int value) {
		if(index >= size)
			return;
		for(int v = min ; v <= max ; v++) {
			array[index] = v;
			generate(array, index + 1 , 0);
			if(index == size - 1)
				result.add(newArray(array));
		}
	}
	
	protected int[] newArray() {
		return newArray(min);
	}
	
	protected int[] newArray(int value) {
		int[] array = new int[size];
		Arrays.fill(array, value);
		return array;
	}
	
	protected int[] newArray(int[] array) {
		return Arrays.copyOf(array, array.length);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		protected int min = 0;
		protected int max = 3;
		protected int size = 5;
		
		public Builder min(int min) {
			this.min = min;
			return this;
		}
		public Builder max(int max) {
			this.max = max;
			return this;
		}
		public Builder size(int size) {
			this.size = size;
			return this;
		}
		
		public GenerateAllPosibleArrayWithLengthAndMaxValue build() {
			return new GenerateAllPosibleArrayWithLengthAndMaxValue(this);
		}
	}
	
	public static void main(String[] args) {
		GenerateAllPosibleArrayWithLengthAndMaxValue object = 
				GenerateAllPosibleArrayWithLengthAndMaxValue.builder()
				.min(0)
				.max(3)
				.size(5)
				.build();
		List<int[]> list = object.generate();
		System.err.println("result.size = " + list.size() + "\n");
		System.err.println(Strings.toString(list));
	}
	
}
