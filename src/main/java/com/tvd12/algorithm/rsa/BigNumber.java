package com.tvd12.algorithm.rsa;

import java.math.BigInteger;
import java.util.Arrays;

public final class BigNumber {

	public static int BN_DIGIT_BITS = 32;
	public static int BN_MAX_DIGITS = 65;
	public static long BN_UINT8_DIGIT = 0xFF;
	public static long BN_MAX_DIGIT = 0xFFFFFFFFL;

	public BigNumber() {}

	public static int DIGIT_2MSB(long x) {
		return (int)(((x) >> (BN_DIGIT_BITS - 2)) & 0x03);
	}

	public static void BN_ASSIGN_DIGIT(long[] a, long b, int digits) {
		bn_assign_zero(a, digits);
		a[0] = b;
	}

	public static void bn_decode(long[] bn, int digits, int[] hexarr, int size) {
		int i, j, u;
		for (i = 0, j = size - 1; i < digits && j >= 0; i++) {
			long t = 0;
			for (u = 0; j >= 0 && u < BN_DIGIT_BITS; j--, u += 8) {
				t = unsign_int(t | unsign_int((hexarr[j]) << u));
			}
			bn[i] = t;
		}

		for (; i < digits; i++) {
			bn[i] = 0;
		}
	}

	public static void bn_encode(int[] hexarr, int size, long[] bn, long digits) {
		int i, j, u;

		for (i = 0, j = size - 1; i < digits && j >= 0; i++) {
			long t = bn[i];
			for (u = 0; j >= 0 && u < BN_DIGIT_BITS; j--, u += 8) {
				hexarr[j] = unsign_byte(t >> u);
			}
		}

		for (; j >= 0; j--) {
			hexarr[j] = 0;
		}
	}

	public static void bn_assign(long[] a, long[] b, long digits) {
		for (int i = 0; i < digits; i++) {
			a[i] = b[i];
		}
	}

	static void bn_assign_zero(long[] a, long digits) {
		for (int i = 0; i < digits; i++) {
			a[i] = 0;
		}
	}

	public static long bn_add(long[] a, long[] b, long[] c, int digits) {
		long ai, carry = 0;
		for (int i = 0; i < digits; i++) {
			if ((ai = unsign_int(b[i] + carry)) < carry) {
				ai = c[i];
			} else if ((ai = unsign_int(ai + c[i])) < c[i]) {
				carry = 1;
			} else {
				carry = 0;
			}
			a[i] = ai;
		}
		return carry;
	}
	
	public static long bn_sub(long[] a, long[] b, long[] c, long digits) {
		return bn_sub(a, 0, b, 0, c, digits);
	}

	public static long bn_sub(long[] a, int ai, long[] b, int bi, long[] c, long digits) {
		long aii, borrow = 0;
		for (int i = 0; i < digits; i++) {
			if ((aii = unsign_int(b[bi + i] - borrow)) > (BN_MAX_DIGIT - borrow)) {
				aii = BN_MAX_DIGIT - c[i];
			} else if ((aii = unsign_int(aii - c[i])) > (BN_MAX_DIGIT - c[i])) {
				borrow = 1;
			} else {
				borrow = 0;
			}
			a[ai + i] = aii;
		}

		return borrow;
	}

	public static void bn_mul(long[] a, long[] b, long[] c, int digits) {
		long[] t = new long[2 * BN_MAX_DIGITS];
		int bdigits, cdigits;

		bn_assign_zero(t, 2 * digits);
		bdigits = bn_digits(b, digits);
		cdigits = bn_digits(c, digits);

		for (int i = 0; i < bdigits; i++) {
			t[i + cdigits] = unsign_int(t[i + cdigits] + bn_add_digit_mul(t, i, t, i, b[i], c, cdigits));
		}

		bn_assign(a, t, 2 * digits);

		// Clear potentially sensitive information
		Arrays.fill(t, 0);
	}

	
	public static void bn_div(long[] a, long[] b, long[] c, int cdigits, long[] d, int ddigits) {
		BigInteger tmp = null;
		long ai, t;
		long[] cc = new long[2 * BN_MAX_DIGITS + 1];
		long[] dd = new long[BN_MAX_DIGITS];
		int i;
		int dddigits;

		dddigits = bn_digits(d, ddigits);
		if (dddigits == 0)
			return;

		long shift = BN_DIGIT_BITS - bn_digit_bits(d[dddigits - 1]);
		bn_assign_zero(cc, dddigits);
		cc[cdigits] = bn_shift_l(cc, c, shift, cdigits);
		bn_shift_l(dd, d, shift, dddigits);
		t = dd[dddigits - 1];

		bn_assign_zero(a, cdigits);
		i = cdigits - dddigits;
		for (; i >= 0; i--) {
			if (t == BN_MAX_DIGIT) {
				ai = cc[i + dddigits];
			} else {
				tmp = toUnsignedBigInteger(cc[i + dddigits - 1]);
				tmp = tmp.add(
					toUnsignedBigInteger(cc[i + dddigits]).shiftLeft(BN_DIGIT_BITS)
				);
				ai = tmp.divide(toUnsignedBigInteger((t + 1)))
						.and(toUnsignedBigInteger(BN_MAX_DIGIT))
						.longValue();
			}

			cc[i + dddigits] = unsign_int(cc[i + dddigits] - bn_sub_digit_mul(cc, i, cc, i, ai, dd, dddigits));
//			System.out.printf("cc[%d]: %08X\n", i, cc[i+dddigits]);
			while (cc[i + dddigits] > 0 || (bn_cmp(cc, i, dd, 0, dddigits) >= 0)) {
				ai++;
				cc[i + dddigits] = unsign_int(cc[i + dddigits]- bn_sub(cc, i, cc, i, dd, dddigits));
//				System.out.printf("cc1[%d]: %08X\n", i, cc[i+dddigits]);
			}
			a[i] = ai;
//			System.out.printf("ai[%d]: %08X, %s\n", i, ai, tmp.toString());
		}
		bn_assign_zero(b, ddigits);
		bn_shift_r(b, cc, shift, dddigits);

		// Clear potentially sensitive information
		Arrays.fill(cc, 0);
		Arrays.fill(dd, 0);
	}
	
	private static BigInteger toUnsignedBigInteger(long i) {
	    if (i >= 0L) {
	        return BigInteger.valueOf(i);
	    } else {
	        int upper = (int) (i >>> 32);
	        int lower = (int) i;
	         // return (upper << 32) + lower
	        return BigInteger.valueOf(Integer.toUnsignedLong(upper))
	                .shiftLeft(32)
	                .add(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
	    }
	}

	public static long bn_shift_l(long[] a, long[] b, long c, long digits) {
		if (c >= BN_DIGIT_BITS)
			return 0;

		long t = BN_DIGIT_BITS - c;
		long carry = 0;
		for (int i = 0; i < digits; i++) {
			long bi = b[i];
			a[i] = unsign_int(unsign_int(bi << c) | carry);
			carry = c != 0 ? unsign_int(bi >> t) : 0;
		}

		return carry;
	}

	public static long bn_shift_r(long[] a, long[] b, long c, int digits) {
		if (c >= BN_DIGIT_BITS)
			return 0;

		long t = BN_DIGIT_BITS - c;
		long carry = 0;
		int i = digits - 1;
		for (; i >= 0; i--) {
			long bi = b[i];
			a[i] = unsign_int(unsign_int(bi >> c) | carry);
			carry = c != 0 ? unsign_int(bi << t) : 0;
		}

		return carry;
	}

	public static void bn_mod(long[] a, long[] b, int bdigits, long[] c, int cdigits) {
		long[] t = new long[2 * BN_MAX_DIGITS];

		bn_div(t, a, b, bdigits, c, cdigits);

		// Clear potentially sensitive information
		Arrays.fill(t, 0);
	}

	public static void bn_mod_mul(long[] a, long[] b, long[] c, long[] d, int digits) {
		long[] t = new long[2 * BN_MAX_DIGITS];

		bn_mul(t, b, c, digits);
		bn_mod(a, t, 2 * digits, d, digits);

		// Clear potentially sensitive information
		Arrays.fill(t, 0);
	}

	public static void bn_mod_exp(long[] a, long[] b, long[] c, int cdigits, long[] d, int ddigits) {
	    long[][] bpower = new long[3][BN_MAX_DIGITS];
	    long[] t = new long[BN_MAX_DIGITS];

	    bn_assign(bpower[0], b, ddigits);
	    bn_mod_mul(bpower[1], bpower[0], b, d, ddigits);
	    bn_mod_mul(bpower[2], bpower[1], b, d, ddigits);

	    BN_ASSIGN_DIGIT(t, 1, ddigits);

	    cdigits = bn_digits(c, cdigits);
	    int i = cdigits - 1;
	    for(; i >= 0; -- i) {
	        long ci = c[i];
	        long ci_bits = BN_DIGIT_BITS;

	        if(i == (int)(cdigits - 1)) {
	            while(DIGIT_2MSB(ci) <= 0) {
	                ci <<= 2;
	                ci_bits -= 2;
	            }
	        }

	        for(int j = 0; j < ci_bits; j += 2) {
	            bn_mod_mul(t, t, t, d, ddigits);
	            bn_mod_mul(t, t, t, d, ddigits);
	            int s = DIGIT_2MSB(ci);
	            if(s != 0) {
	                bn_mod_mul(t, t, bpower[s-1], d, ddigits);
	            }
	            ci <<= 2;
	        }
	    }

	    bn_assign(a, t, ddigits);

	    // Clear potentially sensitive information
//	    Arrays.fill(bpower, 0);
//	    Arrays.fill(t, 0);
	}

	public static int bn_cmp(long[] a, long[] b, int digits) {
		return bn_cmp(a, 0, b, 0, digits);
	}
	
	public static int bn_cmp(long[] a, int ai, long[] b, int bi, int digits) {
		for (int i = digits - 1; i >= 0; i--) {
			if (a[ai + i] > b[bi + i])
				return 1;
			if (a[ai + i] < b[bi + i])
				return -1;
		}

		return 0;
	}

	public static int bn_digits(long[] a, int digits) {
		int i = digits - 1;
		for (; i >= 0; i--) {
			if (a[i] > 0)
				break;
		}
		return (i + 1);
	}

	static long bn_add_digit_mul(long[] a, int ai, long[] b, int bi, long c, long[] d, long digits) {
		if (c == 0)
			return 0;

		long carry = 0;
		for (int i = 0; i < digits; i++) {
			BigInteger result = toUnsignedBigInteger(c)
					.multiply(toUnsignedBigInteger(d[i]));
			long rl = result
					.and(toUnsignedBigInteger(BN_MAX_DIGIT))
					.longValue();
			long rh = result
					.shiftRight(BN_DIGIT_BITS)
					.and(toUnsignedBigInteger(BN_MAX_DIGIT))
					.longValue();
			
			if ((a[ai + i] = unsign_int(b[bi + i] + carry)) < carry) {
				carry = 1;
			} else {
				carry = 0;
			}
			if ((a[ai + i] = unsign_int(a[ai + i] + rl)) < rl) {
				carry ++;
			}
			carry = unsign_int(carry + rh);
		}

		return carry;
	}
	
	static long unsign_int(long number) {
		return number & BN_MAX_DIGIT;
	}
	
	static int unsign_byte(long number) {
		return (int)(number & BN_UINT8_DIGIT);
	}
	
	private static final BigInteger UNSIGNED_LONG_MASK = 
			BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);

	static long bn_sub_digit_mul(long[] a, int ai, long[] b, int bi, long c, long[] d, long digits) {
		if (c == 0)
			return 0;

		long borrow = 0;
		for (int i = 0; i < digits; i++) {
			BigInteger result = BigInteger.valueOf(c)
					.multiply(BigInteger.valueOf(d[i]));
			long rl = result
					.and(BigInteger.valueOf(BN_MAX_DIGIT))
					.longValue();
			long rh = result
					.shiftRight(BN_DIGIT_BITS)
					.and(BigInteger.valueOf(BN_MAX_DIGIT))
					.longValue();
			if ((a[ai + i] = unsign_int(b[bi + i] - borrow)) > (BN_MAX_DIGIT - borrow)) {
				borrow = 1;
			} else {
				borrow = 0;
			}
			if ((a[ai + i] = unsign_int(a[ai + i] - rl)) > (BN_MAX_DIGIT - rl)) {
				borrow ++;
			}
			borrow = unsign_int(borrow + rh);
		}

		return borrow;
	}

	static long bn_digit_bits(long a) {
		long i;
		for (i = 0; i < BN_DIGIT_BITS; i++) {
			if (a == 0)
				break;
			a >>= 1;
		}

		return i;
	}
}
