package com.tvd12.algorithm.rsa;

public class RSASimpleExample {

	public static void main(String[] args) {
		new RSASimpleExample().test();
	}
	
	char Ten[] = new char[50];
	int M[] = new int[50];
	int C[] = new int[50];
	int e=7, n=187, d =23;

	int mod(int m, int e, int n) {
		int a[] = new int[100];
		int k = 0;
		do {
			a[k] = e % 2;
			k++;
			e = e / 2;
		} while (e != 0);

		int kq = 1;
		for (int i = k - 1; i >= 0; i--) {
			kq = (kq * kq) % n;
			if (a[i] == 1)
				kq = (kq * m) % n;
		}
		return kq;
	}
	
	void test() { 
		Ten = "Dung".toCharArray();
	    System.out.println("Chuoi can ma hoa: " + new String(Ten));

	    for(int i=0; i < 4 ;i++) {
	        M[i] = Ten[i];
	        C[i] = mod(M[i],e,n); //C = M^e mod n
	    }
	 
	 
	    System.out.println("========================================================\n");
	    System.out.println("\t\t\tPHAN MA HOA\n\n");
	    System.out.println("Ban ro\tMa ASCII\t  Cong Thuc \t\tBan ma\n\n");
	    for(int i=0; i < 4 ;i++) {
	    	System.out.println("  " + Ten[i] + "  \t   " + M[i] + 
	    			"\t   C = " + M[i] + "^" + e + " mod " + n + 
	    			" = " + C[i] + "\t  " + C[i]);
	    }
	 
	 
	    System.out.println("========================================================\n");
	    System.out.println("\t\t\tPHAN GIAI MA\n\n");
	    System.out.println("Ban ma\tMa ASCII\t  Cong Thuc \t\tBan ro\n\n");
	    
	    for(int i=0; i < 4 ;i++) {
	        int m = mod(C[i],d,n); //m = C^d mod n
	        System.out.println("  " + C[i] + "\t   " + 
	        C[i] + "\t   M = " + C[i] + "^" + d + "mod " + n + " = " + m + 
	        "\t   " + Ten[i]);
	    }
	   
	   
	}

}
