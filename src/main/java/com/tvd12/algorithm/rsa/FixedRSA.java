package com.tvd12.algorithm.rsa;

import static com.tvd12.algorithm.rsa.Commons.*;

public class FixedRSA {

	public static void main(String[] args) {
		int p = 61;
		int q = 53;
		int n = p * q;
		int e = 17;
		int d = 2753;
		
		System.out.println("p = " + p + ", q = " + q + ", n = " + n + ", e = " + e + ", d = " + d);
		
		int m = 456;
		int c = encrypt(m, e, n);
		int mp = decrypt(c, d, n);
		System.out.println("m = " + m + ", c = " + c + ", mp = " + mp);
	}
}
