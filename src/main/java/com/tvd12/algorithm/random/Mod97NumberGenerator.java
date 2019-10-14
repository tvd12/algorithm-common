package com.tvd12.algorithm.random;

import java.util.Random;

public class Mod97NumberGenerator {

	public boolean validate(String uuid, int... template) {
		if(template.length < 2)
			throw new IllegalArgumentException("must has 2 digists groups");
		
		long lastMod = 0;
		int digistCount = 0;
		int maxIndex = template.length - 1;
		
		for(int i = 0 ; i < maxIndex ; ++i) {
			long digists = getNumber(uuid, digistCount, digistCount + template[i]);
			digistCount += template[i];
			lastMod = mod97(lastMod, digists);
		}
		
		int lastDigistCount = template[maxIndex];
		long lastDigists = getNumber(uuid, digistCount, digistCount + lastDigistCount);
		lastMod = mod97(lastMod, lastDigists, lastDigistCount);
		return lastMod == 1;
	}

	public String random(int... template) {
		if(template.length < 2)
			throw new IllegalArgumentException("must has 2 digists groups");
		
		long lastMod = 0;
		int maxIndex = template.length - 1;
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0 ; i < maxIndex ; ++i) {
			int digistCount = template[i];
			long digists = freeRandom(digistCount);
			lastMod = mod97(lastMod, digists);
			builder.append(digistsToString(digists, digistCount));
		}
		
		int lastDigistCount = template[maxIndex];
		long lastDigists = randomDigits(lastMod, lastDigistCount);
		builder.append(digistsToString(lastDigists, lastDigistCount));
		
		lastMod = mod97(lastMod, lastDigists, lastDigistCount);
		if (lastMod != 1)
			throw new IllegalArgumentException("algorithms error, please add an issue to: https://github.com/tvd12/algorithm-common/issues");
		return builder.toString();
	}

	private long mod97(long beforeMod, long digist) {
		return mod97(beforeMod, digist, getDigistCount(digist));
	}
	
	private long mod97(long beforeMod, long digist, int digistCount) {
		long pow = (int) Math.pow(10, digistCount);
		long num = beforeMod * pow + digist;
		long answer = num % 97;
		return answer;
	}

	private static long freeRandom(long digistCount) {
		Random random = new Random();
		long pow = (int) Math.pow(10, digistCount);
		long answer = random.nextInt((int) pow);
		return answer;
	}

	private static long randomDigits(long beforeMod, long digistCount) {
		Random random = new Random();
		long pow = (int) Math.pow(10, digistCount);
		long A = beforeMod * pow;
		long max = ((int) Math.pow(10, digistCount) + A - 1) / 97;
		long min = ((A - 1) / 97 + 1);
		long bound = (max - min);
		long m = random.nextInt((int) bound) + min;
		long x = (m * 97 + 1) - A;
		return x;
	}

	private static String digistsToString(long digist, long digistCount) {
		String digistStr = String.valueOf(digist);
		if (digistStr.length() >= digistCount)
			return digistStr;
		StringBuilder builder = new StringBuilder();
		for (int i = digistStr.length(); i < digistCount; ++i)
			builder.append("0");
		builder.append(digistStr);
		return builder.toString();
	}

	private static int getDigistCount(long digist) {
		int length = 0;
		long temp = 1;
		while (temp <= digist) {
			length++;
			temp *= 10;
		}
		return length;
	}
	
	private static long getNumber(String uuid, int from, int to) {
		StringBuilder b = new StringBuilder();
		for (int i = from; i < to; ++i)
			b.append(uuid.charAt(i));
		return Integer.valueOf(b.toString());
	}
	
	public static void main(String[] args) {
		Mod97NumberGenerator generator = new Mod97NumberGenerator();
		// System.out.println(digistsToString(randomDigits(0, 9), 9));
		for (int i = 0; i < 100000; ++i) {
			String value = generator.random(4, 4, 4);
			System.out.println(value + ", length = " + value.length());
			boolean validate = generator.validate(value, 4, 4, 4);
			if(!validate)
				throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < 100000; ++i) {
			String value = generator.random(5, 5, 5);
			System.out.println(value + ", length = " + value.length());
			boolean validate = generator.validate(value, 5, 5, 5);
			if(!validate)
				throw new IllegalArgumentException();
		}
	}
}