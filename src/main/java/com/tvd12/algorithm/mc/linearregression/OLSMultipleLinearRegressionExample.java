package com.tvd12.algorithm.mc.linearregression;

import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class OLSMultipleLinearRegressionExample {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		double[] y = new double[]{49, 50, 51,  54, 58, 59, 60, 62, 63, 64, 66, 67, 68};
		double[][] x = new double[][] {
			{147, 1}, {150, 2}, {153, 1}, {158, 2}, {163, 6}, {165, 2}, {168, 1}, {170, 6}, {173, 1}, {175, 2}, {178, 2}, {180, 1}, {183, 1}
		};
		regression.newSampleData(y, x);
		
		double[] beta = regression.estimateRegressionParameters();       
//		double[] residuals = regression.estimateResiduals();
//		double[][] parametersVariance = regression.estimateRegressionParametersVariance();
//		double regressandVariance = regression.estimateRegressandVariance();
//		double rSquared = regression.calculateRSquared();
//		double sigma = regression.estimateRegressionStandardError();
		System.out.println(Arrays.toString(beta));
		RealMatrix n = new Array2DRowRealMatrix(new double[][] {{beta[1], beta[2]}});
		RealMatrix test = new Array2DRowRealMatrix(new double[][] {{160.0D}, {2.0D}});
		RealMatrix result = n.multiply(test);
		System.out.println(Arrays.toString(result.getRow(0)));
		double guest = result.getEntry(0, 0) + beta[0];
		System.out.println(guest);
	}
	
	public static void test2() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		double[] y = new double[]{11.0, 12.0, 13.0, 14.0, 15.0, 16.0};
		double[][] x = new double[6][];
		x[0] = new double[]{0, 0, 0, 0, 0};
		x[1] = new double[]{2.0, 0, 0, 0, 0};
		x[2] = new double[]{0, 3.0, 0, 0, 0};
		x[3] = new double[]{0, 0, 4.0, 0, 0};
		x[4] = new double[]{0, 0, 0, 5.0, 0};
		x[5] = new double[]{0, 0, 0, 0, 6.0};          
		regression.newSampleData(y, x);
		
//		double[] beta = regression.estimateRegressionParameters();       
//		double[] residuals = regression.estimateResiduals();
//		double[][] parametersVariance = regression.estimateRegressionParametersVariance();
//		double regressandVariance = regression.estimateRegressandVariance();
//		double rSquared = regression.calculateRSquared();
		double sigma = regression.estimateRegressionStandardError();
		
		System.out.println("sigma = " + sigma);
	}
	
}
