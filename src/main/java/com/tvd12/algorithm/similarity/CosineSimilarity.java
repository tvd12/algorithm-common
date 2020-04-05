package com.tvd12.algorithm.similarity;

import static com.google.common.collect.Multisets.union;
import static java.lang.Math.sqrt;

import org.simmetrics.MultisetMetric;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * [Excluded for brevity]
 *
 * @param <T> type of the token
 * 
 */
public final class CosineSimilarity<T> implements MultisetMetric<T> {

	@Override
	public float compare(final Multiset<T> a, final Multiset<T> b) {

		if (a.isEmpty() && b.isEmpty()) {
			return 1.0f;
		}

		if (a.isEmpty() || b.isEmpty()) {
			return 0.0f;
		}

		if (a.size() >= b.size()) {
			return this.determineSimilarity(a, b);
		} else {
			return this.determineSimilarity(b, a);
		}
	}

	private float determineSimilarity(final Multiset<T> largerSet, final Multiset<T> smallerSet) {
		float dotProduct = 0;
		float magnitudeA = 0;
		float magnitudeB = 0;

		for (final T entry : union(largerSet, smallerSet).elementSet()) {
			final float aCount = largerSet.count(entry);
			final float bCount = smallerSet.count(entry);

			dotProduct += aCount * bCount;
			magnitudeA += aCount * aCount;
			magnitudeB += bCount * bCount;
		}

		// a·b / (||a|| * ||b||)
		return (float) (dotProduct / (sqrt(magnitudeA) * sqrt(magnitudeB)));
	}

	@Override
	public String toString() {
		return "CosineSimilarity";
	}
	
	public static void main(String[] args) {
		CosineSimilarity<Double> similarity = new CosineSimilarity<>();
		Multiset<Double> a = HashMultiset.create();
		Multiset<Double> b = HashMultiset.create();
		
		a.add(9.0D);
		a.add(10.0D);
		b.add(10.0D);
		b.add(11.0D);
		
		System.out.println(similarity.compare(a, b));
	}
}
