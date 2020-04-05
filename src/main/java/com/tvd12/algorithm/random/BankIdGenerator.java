package com.tvd12.algorithm.random;

import java.util.Random;

public class BankIdGenerator {

	public static void main(String[] args) {
		// System.out.println(digistsToString(randomDigits(0, 9), 9));
		for (int i = 0; i < 100000; ++i) {
			String value = random();
			System.out.println(value + ", length = " + value.length());
			boolean validate = validate(value);
			if(!validate)
				throw new IllegalArgumentException();
		}
	}

	private static boolean validate(String uuid) {
		System.out.println("================= validate =================");
		long a = getNumber(uuid, 0, 9);
		long am = mod97(0, a);
		System.out.println("a = " + a + ", am = " + am);
		long b = getNumber(uuid, 9, 9 + 7);
		long bm = mod97(am, b);
		System.out.println("b = " + b + ", bm = " + bm);
		long c = getNumber(uuid, 9 + 7, 9 + 7 + 7);
		long cm = mod97(bm, c);
		System.out.println("c = " + c + ", cm = " + cm);
		long d = getNumber(uuid, 9 + 7 + 7, 9 + 7 + 7 + 5);
		long dm = mod97(cm, d, 5);
		System.out.println("d = " + d + ", dm = " + dm);
		return dm == 1;
	}

	private static long getNumber(String uuid, int from, int to) {
		StringBuilder b = new StringBuilder();
		for (int i = from; i < to; ++i)
			b.append(uuid.charAt(i));
		return Integer.valueOf(b.toString());
	}

	private static String random() {
		System.out.println("=================== random ================");
		StringBuilder builder = new StringBuilder();
		long a = freeRandom(9);
		long am = mod97(0, a);
		System.out.println("a = " + a + ", am = " + am);
		builder.append(digistsToString(a, 9));
		long b = freeRandom(7);
		long bm = mod97(am, b);
		System.out.println("b = " + b + ", bm = " + bm);
		builder.append(digistsToString(b, 7));
		long c = freeRandom(7);
		long cm = mod97(bm, c);
		System.out.println("c = " + c + ", cm = " + cm);
		builder.append(digistsToString(c, 7));
		long d = randomDigits(cm, 5);
		long dm = mod97(cm, d, 5);
		System.out.println("d = " + d + ", dm = " + dm);
		builder.append(digistsToString(d, 5));
		if (dm != 1)
			throw new IllegalArgumentException("dm = " + dm);
		return builder.toString();
	}

	private static long mod97(long beforeMod, long digist) {
		return mod97(beforeMod, digist, -1);
	}
	
	private static long mod97(long beforeMod, long digist, int dc) {
//		System.out.println("============== mod97 =======");
//		System.out.println("beforeMod = " + beforeMod + ", digist = " + digist);
		long digistCount = dc > 0 ? dc : getDigistCount(digist);
//		System.out.println("digistCount = " + digistCount);
		long pow = (int) Math.pow(10, digistCount);
//		System.out.println("pow = " + pow);
		long num = beforeMod * pow + digist;
//		System.out.println("num = " + num);
		long answer = num % 97;
//		System.out.println("answer = " + answer);
		return answer;
	}

	private static long freeRandom(long digistCount) {
		Random random = new Random();
		long pow = (int) Math.pow(10, digistCount);
		long answer = random.nextInt((int) pow);
		return answer;
	}

	private static long randomDigits(long beforeMod, long digistCount) {
//		System.out.println("================ randomDigits ===========");
//		System.out.println("beforeMod = " + beforeMod + ", digistCount = " + digistCount);
		Random random = new Random();
		long pow = (int) Math.pow(10, digistCount);
//		System.out.println("pow = " + pow);
		long A = beforeMod * pow;
//		System.out.println("A = " + A);
		long max = ((int) Math.pow(10, digistCount) + A - 1) / 97;
//		System.out.println("max = " + max);
		long min = ((A - 1) / 97 + 1);
//		System.out.println("min = " + min);
		long bound = (max - min);
//		System.out.println("bound = " + bound);
		long m = random.nextInt((int) bound) + min;
//		System.out.println("m = " + m);
		long x = (m * 97 + 1) - A;
//		System.out.println("x = " + x);
//		long Ax = A + x;
//		System.out.println("Ax = " + Ax + ", answer = " + (Ax % 97));
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
}