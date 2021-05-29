package com.tvd12.algorithm.rsa;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import static com.tvd12.algorithm.rsa.BigNumber.*;
import static com.tvd12.algorithm.rsa.Keys.*;

public class RSA {

	public static final int RSA_MAX_MODULUS_BITS = 2048;
	public static final int RSA_MAX_MODULUS_LEN = ((RSA_MAX_MODULUS_BITS + 7) / 8);
	public static final int RSA_MAX_PRIME_BITS = ((RSA_MAX_MODULUS_BITS + 1) / 2);
	public static final int RSA_MAX_PRIME_LEN = ((RSA_MAX_PRIME_BITS + 7) / 8);

	// Error codes
	public static final int ERR_WRONG_DATA = 0x1001;
	public static final int ERR_WRONG_LEN = 0x1002;

	static int generate_rand() {
		return ThreadLocalRandom.current().nextInt(Byte.MAX_VALUE);
//		return 'a';
	}

	static int rsa_public_encrypt(int[] out, int[] out_len, int[] in, int in_len, rsa_pk_t pk) {
		System.out.printf("rsa_public_encrypt: "
				+ "out = %s"
				+ "\nout_len = %d"
				+ "\nin = %s"
				+ "\nin_len = %d"
				+ "\npk = %s\n",
				Arrays.toString(out), out_len[0], Arrays.toString(in), in_len, pk.toString());
		int[] pkcs_block = new int[RSA_MAX_MODULUS_LEN];

		int modulus_len = (pk.bits + 7) / 8;
		if (in_len + 11 > modulus_len) {
			return ERR_WRONG_LEN;
		}

		pkcs_block[0] = 0;
		pkcs_block[1] = 2;
		int i = 2, b = 0;
		for (; i < modulus_len - in_len - 1; i++) {
			do {
				b = generate_rand();
			} while (b == 0);
			pkcs_block[i] = b;
		}

		pkcs_block[i++] = 0;

		memcpy(pkcs_block, i, in, in_len);
		int status = public_block_operation(out, out_len, pkcs_block, modulus_len, pk);

		// Clear potentially sensitive information
		b = 0;
		Arrays.fill(pkcs_block, 0);

		return status;
	}

	private static void memcpy(int[] dest, int di, int[] src, int n) {
		for (int i = 0; i < n; ++i)
			dest[di + i] = src[i];
	}

	static int rsa_public_decrypt(int[] out, int[] out_len, int[] in, int in_len, rsa_pk_t pk) {
		int status;
		int[] pkcs_block = new int[RSA_MAX_MODULUS_LEN];
		int[] pkcs_block_len = new int[1];
		int modulus_len = (pk.bits + 7) / 8;
		if (in_len > modulus_len)
			return ERR_WRONG_LEN;

		status = public_block_operation(pkcs_block, pkcs_block_len, in, in_len, pk);
		if (status != 0)
			return status;

		if (pkcs_block_len[0] != modulus_len)
			return ERR_WRONG_LEN;

		if ((pkcs_block[0] != 0) || (pkcs_block[1] != 1))
			return ERR_WRONG_DATA;

		int i = 2;
		for (; i < modulus_len - 1; i++) {
			if (pkcs_block[i] != 0xFF)
				break;
		}

		if (pkcs_block[i++] != 0)
			return ERR_WRONG_DATA;

		out_len[0] = modulus_len - i;
		if (out_len[0] + 11 > modulus_len)
			return ERR_WRONG_DATA;

		System.arraycopy(pkcs_block, i, out, 0, out_len[0]);

		// Clear potentially sensitive information
		Arrays.fill(pkcs_block, 0);
		return status;
	}

	static int rsa_private_encrypt(int[] out, int[] out_len, int[] in, int in_len, rsa_sk_t sk) {
		int status;
		int[] pkcs_block = new int[RSA_MAX_MODULUS_LEN];
		int i, modulus_len;

		modulus_len = (sk.bits + 7) / 8;
		if (in_len + 11 > modulus_len)
			return ERR_WRONG_LEN;

		pkcs_block[0] = 0;
		pkcs_block[1] = 1;
		for (i = 2; i < modulus_len - in_len - 1; i++) {
			pkcs_block[i] = 0xFF;
		}

		pkcs_block[i++] = 0;

		memcpy(pkcs_block, i, in, in_len);

		status = private_block_operation(out, out_len, pkcs_block, modulus_len, sk);

		// Clear potentially sensitive information
		Arrays.fill(pkcs_block, 0);
		return status;
	}

	static int rsa_private_decrypt(int[] out, int[] out_len, int[] in, int in_len, rsa_sk_t sk) {
		int status;
		int[] pkcs_block = new int[RSA_MAX_MODULUS_LEN];
		int i, modulus_len;
		int[] pkcs_block_len = new int[1];

		modulus_len = (sk.bits + 7) / 8;
		if (in_len > modulus_len)
			return ERR_WRONG_LEN;

		status = private_block_operation(pkcs_block, pkcs_block_len, in, in_len, sk);
		if (status != 0)
			return status;

		if (pkcs_block_len[0] != modulus_len)
			return ERR_WRONG_LEN;

		if ((pkcs_block[0] != 0) || (pkcs_block[1] != 2))
			return ERR_WRONG_DATA;

		for (i = 2; i < modulus_len - 1; i++) {
			if (pkcs_block[i] == 0)
				break;
		}

		i++;
		if (i >= modulus_len)
			return ERR_WRONG_DATA;
		out_len[0] = modulus_len - i;
		if (out_len[0] + 11 > modulus_len)
			return ERR_WRONG_DATA;
		System.arraycopy(pkcs_block, i, out, 0, out_len[0]);

		// Clear potentially sensitive information
		Arrays.fill(pkcs_block, 0);
		return status;
	}

	static int public_block_operation(int[] out, int[] out_len, int[] in, int in_len, rsa_pk_t pk) {
		int edigits, ndigits;
		long[] c = new long[BN_MAX_DIGITS];
		long[] e = new long[BN_MAX_DIGITS];
		long[] m = new long[BN_MAX_DIGITS];
		long[] n = new long[BN_MAX_DIGITS];

		bn_decode(m, BN_MAX_DIGITS, in, in_len);
		bn_decode(n, BN_MAX_DIGITS, pk.modulus, RSA_MAX_MODULUS_LEN);
		bn_decode(e, BN_MAX_DIGITS, pk.exponent, RSA_MAX_MODULUS_LEN);

		ndigits = bn_digits(n, BN_MAX_DIGITS);
		edigits = bn_digits(e, BN_MAX_DIGITS);

		if (bn_cmp(m, n, ndigits) >= 0) {
			return ERR_WRONG_DATA;
		}

		bn_mod_exp(c, m, e, edigits, n, ndigits);

		out_len[0] = (pk.bits + 7) / 8;
		bn_encode(out, out_len[0], c, ndigits);

		// Clear potentially sensitive information
		Arrays.fill(c, 0);
		Arrays.fill(m, 0);

		return 0;
	}

	static int private_block_operation(int[] out, int[] out_len, int[] in, int in_len, rsa_sk_t sk) {
		int cdigits, ndigits, pdigits;
		long[] c = new long[BN_MAX_DIGITS];
		long[] cp = new long[BN_MAX_DIGITS];
		long[] cq = new long[BN_MAX_DIGITS];
		long[] dp = new long[BN_MAX_DIGITS];
		long[] dq = new long[BN_MAX_DIGITS];
		long[] mp = new long[BN_MAX_DIGITS];
		long[] mq = new long[BN_MAX_DIGITS];
		long[] n = new long[BN_MAX_DIGITS];
		long[] p = new long[BN_MAX_DIGITS];
		long[] q = new long[BN_MAX_DIGITS];
		long[] q_inv = new long[BN_MAX_DIGITS];
		long[] t = new long[BN_MAX_DIGITS];

		bn_decode(c, BN_MAX_DIGITS, in, in_len);
		bn_decode(n, BN_MAX_DIGITS, sk.modulus, RSA_MAX_MODULUS_LEN);
		bn_decode(p, BN_MAX_DIGITS, sk.prime1, RSA_MAX_PRIME_LEN);
		bn_decode(q, BN_MAX_DIGITS, sk.prime2, RSA_MAX_PRIME_LEN);
		bn_decode(dp, BN_MAX_DIGITS, sk.prime_exponent1, RSA_MAX_PRIME_LEN);
		bn_decode(dq, BN_MAX_DIGITS, sk.prime_exponent2, RSA_MAX_PRIME_LEN);
		bn_decode(q_inv, BN_MAX_DIGITS, sk.coefficient, RSA_MAX_PRIME_LEN);

		cdigits = bn_digits(c, BN_MAX_DIGITS);
		ndigits = bn_digits(n, BN_MAX_DIGITS);
		pdigits = bn_digits(p, BN_MAX_DIGITS);

		if (bn_cmp(c, n, ndigits) >= 0)
			return ERR_WRONG_DATA;

		bn_mod(cp, c, cdigits, p, pdigits);
		bn_mod(cq, c, cdigits, q, pdigits);
		bn_mod_exp(mp, cp, dp, pdigits, p, pdigits);
		bn_assign_zero(mq, ndigits);
		bn_mod_exp(mq, cq, dq, pdigits, q, pdigits);

		if (bn_cmp(mp, mq, pdigits) >= 0) {
			bn_sub(t, mp, mq, pdigits);
		} else {
			bn_sub(t, mq, mp, pdigits);
			bn_sub(t, p, t, pdigits);
		}

		bn_mod_mul(t, t, q_inv, p, pdigits);
		bn_mul(t, t, q, pdigits);
		bn_add(t, t, mq, ndigits);

		out_len[0] = (sk.bits + 7) / 8;
		bn_encode(out, out_len[0], t, ndigits);

		// Clear potentially sensitive information
		Arrays.fill(c, 0);
		Arrays.fill(cp, 0);
		Arrays.fill(cq, 0);
		Arrays.fill(dp, 0);
		Arrays.fill(dq, 0);
		Arrays.fill(mp, 0);
		Arrays.fill(mq, 0);
		Arrays.fill(p, 0);
		Arrays.fill(q, 0);
		Arrays.fill(q_inv, 0);
		Arrays.fill(t, 0);
		return 0;
	}

	private static class rsa_pk_t {
		int bits;
		int[] modulus = new int[RSA_MAX_MODULUS_LEN];
		int[] exponent = new int[RSA_MAX_MODULUS_LEN];
	}

	private static class rsa_sk_t {
		int bits;
		int[] modulus = new int[RSA_MAX_MODULUS_LEN];
		int[] public_exponet = new int[RSA_MAX_MODULUS_LEN];
		int[] exponent = new int[RSA_MAX_MODULUS_LEN];
		int[] prime1 = new int[RSA_MAX_PRIME_LEN];
		int[] prime2 = new int[RSA_MAX_PRIME_LEN];
		int[] prime_exponent1 = new int[RSA_MAX_PRIME_LEN];
		int[] prime_exponent2 = new int[RSA_MAX_PRIME_LEN];
		int[] coefficient = new int[RSA_MAX_PRIME_LEN];
	}

	public static void main(String[] args) {
		int ret;
	    rsa_pk_t pk = new rsa_pk_t();
	    rsa_sk_t sk = new rsa_sk_t();
	    
	    int[] output = new int[256];

	    // message to encrypt
	    int[] input = { 0x21,0x55,0x53,0x53,0x53,0x53};

	    int[] msg = new int[256];
	    int[] outputLen = new int[1]; 
	    int[] msg_len = new int[1];
	    int  inputLen;

	    // copy keys.h message about public key and private key to the flash RAM
	    pk.bits = KEY_M_BITS;
	    System.arraycopy(key_m, 0, pk.modulus, RSA_MAX_MODULUS_LEN - key_m.length, key_m.length);
	    System.arraycopy(key_e, 0, pk.exponent, RSA_MAX_MODULUS_LEN - key_e.length, key_e.length);
	    sk.bits = KEY_M_BITS;
	    System.arraycopy(key_m, 0, sk.modulus, RSA_MAX_MODULUS_LEN - key_m.length, key_m.length);
	    System.arraycopy(key_e, 0, sk.public_exponet, RSA_MAX_MODULUS_LEN - key_e.length, key_e.length);
	    System.arraycopy(key_pe, 0, sk.exponent, RSA_MAX_MODULUS_LEN - key_pe.length, key_pe.length);
	    System.arraycopy(key_p1, 0, sk.prime1, RSA_MAX_PRIME_LEN - key_p1.length, key_p1.length);
	    System.arraycopy(key_p2, 0, sk.prime2, RSA_MAX_PRIME_LEN - key_p2.length, key_p2.length);
	    System.arraycopy(key_e1, 0, sk.prime_exponent1, RSA_MAX_PRIME_LEN - key_e1.length, key_e1.length);
	    System.arraycopy(key_e2, 0, sk.prime_exponent2, RSA_MAX_PRIME_LEN - key_e2.length, key_e2.length);
	    System.arraycopy(key_c, 0, sk.coefficient, RSA_MAX_PRIME_LEN - key_c.length, key_c.length);

	    inputLen = input.length;

	    // public key encrypt
	    rsa_public_encrypt(output, outputLen, input, inputLen, pk);

	    // private key decrypt
	    rsa_private_decrypt(msg, msg_len, output, outputLen[0], sk);
//
//	    // private key encrypt
	    rsa_private_encrypt(output, outputLen, input, inputLen, sk);
//
//	    // public key decrypted
	    rsa_public_decrypt(msg, msg_len, output, outputLen[0], pk);
	    
	    System.out.printf("\nm: %s\ne = %s\nmd = %s\n", 
	    		Arrays.toString(input), 
	    		Arrays.toString(output), 
	    		Arrays.toString(msg)
	    );

	}
	
	public static String printIntArrayToHex(int[] array) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0 ; i < array.length ; ++i) {
			builder.append(String.format("%08X", array[i]));
		}
		return builder.toString();
	}
	
}
