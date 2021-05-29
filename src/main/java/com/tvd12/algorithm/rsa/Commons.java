package com.tvd12.algorithm.rsa;

import java.util.concurrent.ThreadLocalRandom;

public final class Commons {

	private static final int[] PRIMES = list_prime_numbers(5, 100);
	
	private Commons() {}
	
	public static int[] findpq() {
		int pi = ThreadLocalRandom.current().nextInt(PRIMES.length);
		int qi = ThreadLocalRandom.current().nextInt(PRIMES.length);
		while(pi == qi)
			qi = ThreadLocalRandom.current().nextInt(PRIMES.length);
		return new int[] { PRIMES[pi], PRIMES[qi] };
	}
	
	public static int find_e(int p, int q) {
		int qn = (p - 1) * (q - 1);
		for(int i = 2 ; i < qn ; ++i) {
			int r = gcd(qn, i);
			if(r == 1)
				return i;
		}
		throw new IllegalArgumentException("can not find e of p = " + p + ", q = " + q);
	}
	
	public static int find_d(int p, int q, int e) {
		int x = 2;
		while(true) {
			long num = (x * (p - 1) * (q - 1) + 1);
			if(num % e == 0)
				return (int)(num / e);
			++x;
		}
	}
	
	public static int encrypt(int m, int e, int n) {
		return exponentiation(m, e, n);
	}
	
	public static int decrypt(int c, int d, int n) {
		return exponentiation(c, d, n);
	}
	
	public static int exponentiation(final int base, final int exponent, int n) {
		int x = base;
		int exp = exponent;
        long result = 1L;
        while (exp > 0) {
            if (exp % 2 != 0) {
                result = (result * x) % n;
            }
            x = (x * x) % n;
            exp /= 2;
        }
        return (int)result;
    }
	
	public static int exp_by_squaring(final int base, final int exponent) {
		long x = base;
		long n = exponent;
	    if (n < 0) {
	      x = 1 / x;
	      n = -n;
	    }
	    if (n == 0) {
	    	return 1;
	    }
	    long y = 1;
	    while (n > 1) {
	      if (n % 2 == 0) { 
	        x = x * x;
	        n = n / 2;
	      }
	      else {
	        y = x * y;
	        x = x * x;
	        n = (n - 1) / 2;
	      }
	    }
	    return (int)(x * y);
	}
	
	public static int gcd(final int a, final int b) {
		int answer = b;
		int next = a;
		while (answer > 0) {
			int remain = next % answer;
			if (remain == 0)
				break;
			next = answer;
			answer = remain;
		}
		return answer;
	}
	
	public static int[] list_prime_numbers(int from, int to) {
		int[] answer = null;
		for(int i = from ; i < to ; ++i) {
			if(is_prime(i)) {
				if(answer == null) {
					answer = new int[] { i };
				}
				else {
					int[] tmp = answer;
					answer = new int[tmp.length + 1];
					for(int k = 0 ; k < tmp.length ; ++k)
						answer[k] = tmp[k];
					answer[tmp.length] = i;
				}
			}
		}
		return answer;
	}
	
	public static boolean is_prime(int number) {
		if(number % 2 == 0)
			return false;
		int n = number / 2;
		for(int i = 3 ; i < n ; i += 2) {
			if(number % i == 0)
				return false;
		}
		return true;
	}
	
}
