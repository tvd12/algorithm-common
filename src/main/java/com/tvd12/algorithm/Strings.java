package com.tvd12.algorithm;

import java.lang.reflect.Array;
import java.util.Collection;

public final class Strings {

	private Strings() {
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static String toString(Collection coll) {
		int index = 0;
		int size = coll.size();
		StringBuilder builder = new StringBuilder();
		for(Object object : coll) {
			builder.append(toString(object));
			if(index ++ < size)
				builder.append("\n");
		}
		return builder.toString();
	}
	
	public static String toString(Object object) {
		if(object.getClass().isArray())
			return arrayToString(object);
		return object.toString();
	}
	
	private static String arrayToString(Object array) {
		int length = Array.getLength(array);
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(int i = 0 ; i < length ; i++) {
			builder.append(Array.get(array, i));
			if(i < length - 1)
				builder.append(" ");
		}
		builder.append("]");
		return builder.toString();
	}
}
