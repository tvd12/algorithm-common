package com.tvd12.algorithm.similarity;

import static java.lang.Math.sqrt;


public class DoubleArrayCosineSimilarity {

	public double cosineSimilarity(double[] a, double[] b, int vectorLength)
	{
	    double dot = 0.0, denominatora = 0.0, denominatorb = 0.0 ;
	     for(int i = 0; i < vectorLength; ++i) {
	        dot += a[i] * b[i] ;
	        denominatora += a[i] * a[i] ;
	        denominatorb += b[i] * b[i] ;
	    }
	    return dot / (sqrt(denominatora) * sqrt(denominatorb)) ;
	}
	
	public static void main(String[] args) {
		DoubleArrayCosineSimilarity similarity = new DoubleArrayCosineSimilarity();
//		double[] a = new double[] {9.0D, 10.0D};
//		double[] b = new double[] {10.0D, 11.0D};
		
		double[] a = new double[] {484.62, 45.03, 70.5, 235.18, 302.98, 95.59, 104.19, 242.12, 403.94, 245.25, 274.58, 75.2, 459.73, 97.75, 57.34, 198.08, 203.64, 409.76, 491.65, 243.02, 71.57, 199.8, 179.81, 262.97, 359.43, 108.93, 141.74, 6.88, 491.51, 319.83, 22.63, 148.0, 276.22, 54.24, 387.71, 458.94, 253.15, 441.43, 351.72, 7.35, 44.88, 80.23, 263.03, 101.84, 173.91, 242.48, 219.17, 199.08, 340.72, 117.99, 208.19, 367.99, 161.36, 193.51, 87.39, 392.84, 290.8, 318.58, 27.22, 452.99, 224.55, 151.5, 112.35, 487.7, 195.55, 254.73, 483.34, 487.54, 442.68, 311.22, 492.86, 216.67, 104.24, 492.04, 11.94, 284.82, 65.39, 103.61, 122.15, 165.48, 25.07, 174.99, 146.06, 464.07, 121.41, 400.78, 477.42, 339.98, 149.55, 56.82, 417.1, 246.8, 85.99, 461.16, 84.3, 155.86, 250.9, 25.48, 180.31, 73.48, 145.98, 27.45, 382.97, 334.17, 446.53, 87.9, 83.02, 264.3, 491.58, 113.22, 232.6, 60.83, 469.63, 183.97, 268.02, 438.76, 440.87, 132.96, 319.71, 436.02, 334.5, 271.28, 434.7, 71.13, 294.44, 236.4, 71.9, 333.28, 181.31, 231.42, 145.09, 235.93, 456.66, 133.87, 425.33, 40.73, 97.83, 452.21, 31.95, 63.89, 383.42, 92.43, 55.01, 154.82, 293.84, 65.79, 2.79, 191.99, 334.22, 210.22, 296.23, 401.99, 308.55, 117.13, 140.01, 321.89, 197.1, 408.34, 216.37, 409.11, 62.06, 6.99, 97.15, 263.91, 408.65, 301.49, 39.72, 295.34, 371.6, 158.46, 388.45, 252.4, 202.89, 436.45, 452.19, 314.16, 98.94, 14.32, 261.37, 413.23, 25.36, 357.39, 150.89, 347.54, 183.7, 434.15, 85.92, 458.78, 492.43, 123.76, 482.58, 385.39, 309.07, 348.08, 12.06, 494.64, 47.33, 2.48, 448.41, 357.46};
		double[] b = new double[] {339.99, 80.71, 409.89, 414.11, 474.71, 382.82, 185.88, 229.56, 114.67, 350.72, 88.17, 77.47, 54.03, 244.8, 247.8, 480.71, 96.81, 55.46, 14.62, 143.95, 13.11, 25.53, 111.94, 259.45, 359.88, 378.8, 412.15, 191.86, 289.46, 137.32, 123.1, 2.08, 170.44, 474.07, 463.73, 255.44, 190.36, 193.1, 224.96, 246.19, 286.25, 407.78, 186.01, 378.56, 422.94, 225.84, 378.33, 287.04, 238.15, 34.9, 128.53, 185.03, 165.12, 76.01, 107.09, 27.17, 383.99, 112.93, 109.02, 355.46, 191.92, 288.08, 13.17, 3.94, 42.47, 488.46, 376.29, 323.89, 427.76, 469.0, 304.2, 130.46, 332.68, 27.84, 187.51, 92.45, 201.94, 281.01, 310.63, 64.89, 65.25, 357.92, 275.7, 237.2, 153.1, 253.57, 208.0, 62.15, 290.15, 268.52, 472.45, 104.21, 471.82, 15.93, 124.56, 71.54, 289.76, 24.95, 100.98, 289.89, 450.67, 267.75, 304.58, 292.98, 495.66, 8.55, 428.14, 493.96, 54.24, 341.9, 184.68, 488.98, 291.98, 104.52, 164.75, 6.69, 257.25, 320.4, 235.29, 112.0, 129.65, 238.2, 362.02, 79.1, 25.75, 163.89, 313.98, 200.71, 329.61, 425.55, 499.3, 244.97, 179.07, 461.26, 213.17, 303.67, 103.37, 285.39, 327.06, 327.61, 221.96, 431.32, 221.24, 121.27, 298.54, 49.68, 113.74, 174.18, 284.3, 132.3, 321.09, 409.27, 219.25, 108.19, 79.69, 264.44, 157.7, 302.94, 129.92, 440.37, 134.9, 53.96, 225.42, 304.2, 58.65, 39.32, 460.18, 44.58, 30.56, 274.74, 193.93, 316.96, 15.92, 331.65, 240.97, 11.32, 397.83, 353.61, 485.72, 475.31, 131.45, 490.2, 196.59, 371.64, 345.2, 145.73, 146.62, 106.03, 420.87, 343.47, 197.45, 27.34, 94.16, 429.23, 254.15, 288.56, 186.65, 48.63, 239.16, 43.96};
		
		System.out.println(similarity.cosineSimilarity(a, b, 2));
	}
	
}