package com.tvd12.algorithm.rsa;

public class GCD {

	public static int gcd(int a, int b) {
		while (b != 0) {
			int r = a % b;
			a = b;
			b = r;
		}
		return a;
	}

	public static int[] gcd_extended(int a, int b) {
		int xa = 1, ya = 0;
		int xb = 0, yb = 1;
		while (b != 0) {
			int q = a / b;
			int r = a % b;
			a = b;
			b = r;
			int xr = xa - q * xb;
			int yr = ya - q * yb;
			xa = xb;
			ya = yb;
			xb = xr;
			yb = yr;
		}
		return new int[] { xa, ya };
	}

	public static int[] euclid_extended(int a, int b) {
		int x = 0, x0 = 1, x1 = 0;
		int y = 0, y0 = 0, y1 = 1;
		while (b > 0) {
			int r = a % b;
			if (r == 0)
				break;
			int q = a / b;
			x = x0 - x1 * q;
			y = y0 - y1 * q;
			a = b;
			b = r;
			x0 = x1;
			x1 = x;
			y0 = y1;
			y1 = y;
		}
		int d = b;
		return new int[] {d, x, y};
	}

}
