package com.tvd12.algorithm.rsa;

import static com.tvd12.algorithm.rsa.Commons.*;

public class SimpleRSA {

	public static void main(String[] args) {
		int[] pq = findpq();
		int p = pq[0];
		int q = pq[1];
		int n = p * q;
		int e = find_e(p, q);
		int d = find_d(p, q, e);
		
		System.out.println("p = " + p + ", q = " + q + ", n = " + n + ", e = " + e + ", d = " + d);
		
		int m = 256;
		int c = encrypt(m, e, n);
		int mp = decrypt(c, d, n);
		System.out.println("m = " + m + ", c = " + c + ", mp = " + mp);
	}
	
}
