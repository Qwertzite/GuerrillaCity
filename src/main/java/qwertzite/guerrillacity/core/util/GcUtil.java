package qwertzite.guerrillacity.core.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.IntPredicate;
import java.util.function.ToDoubleFunction;

public class GcUtil {
	
	/**
	 * Fisher–Yates shuffle. For Objects.
	 * @param <T>
	 * @param array
	 */
	public static <T> void shuffleArray(T[] array, Random rand) {
		if (array.length <= 1) { return; }
		for (int i = array.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			var tmp = array[index];
			array[index] = array[i];
			array[i] = tmp;
		}
	}
	
	/**
	 * Fisher–Yates shuffle. For int.
	 * @param array
	 */
	public static void shuffleArray(int[] array, Random rand) {
		if (array.length <= 1) { return; }
		for (int i = array.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			var tmp = array[index];
			array[index] = array[i];
			array[i] = tmp;
		}
	}
	
	/**
	 * Returns the minimum int value that is "true".<br>
	 * data must be [ false, false, ..., false, true, true, ... ]
	 * @param min
	 * @param max inclusive
	 * @param predicate Must return false for all values below threshold, and always return true if not．
	 * @return returns max+1 if not exist.
	 */
	public static int binarySearch(int min, int max, IntPredicate predicate) {
		if (predicate.test(min)) return min;
		if (!predicate.test(max)) return max+1;
		while (min < max - 1) {
			var mid = (min >> 1) + (max >> 1) + ((min&0b1) & (max&0b1));
			if (!predicate.test(mid)) { min = mid; }
			else { max = mid; }
		}
		return max;
	}
	
	public static int sumArray(int[] array) {
		var sum = 0;
		for (var i : array) sum += i;
		return sum;
	}
	
	/**
	 * Select randomly with give weight function.
	 * @param <T> type to be returned.
	 * @param list
	 * @param weight must return positive weight associated with each entries.
	 * @param rand
	 * @return
	 */
	public static <T> T selectWeightedRandom(List<T> list, ToDoubleFunction<T> weight, Random rand) {
		final int size = list.size();
		if (list.size() == 0.0d) return null;
		double sum = 0.0d;
		double[] scoreList = new double[size];
		for (int i = 0; i < size; i++) {
			sum += weight.applyAsDouble(list.get(i));
			scoreList[i] = sum;
		}
		final double thresh = rand.nextDouble(sum);
		int index = binarySearch(0, size-1, i -> scoreList[i] > thresh);
		return list.get(index);
	}
	
	/**
	 * Select randomly with give weight function.
	 * @param <T> type to be returned.
	 * @param list
	 * @param weight must return positive weight associated with each entries.
	 * @param rand
	 * @return
	 */
	public static <T> List<T> selectWeightedMultipleRandom(List<T> list, ToDoubleFunction<T> weight, Random rand, int count) {
		final int size = list.size();
		double scoreSum = 0.0d;
		double[] scoreList = new double[size];
		for (int i = 0; i < size; i++) {
			scoreList[i] = weight.applyAsDouble(list.get(i));
			scoreSum += scoreList[i];
		}
		count = Math.min(list.size(), count);
		List<T> ret = new ArrayList<>(count);
		outer: for (int i = 0; i < count; i++) {
			final double thresh = rand.nextDouble(scoreSum);
			double sum = 0.0d;
			for (int j = 0; j < list.size(); j++) {
				sum += scoreList[j];
				if (sum > thresh) {
					ret.add(list.get(j));
					scoreSum -= scoreList[j];
					scoreList[j] = 0;
					continue outer;
				}
			}
			ret.add(list.get(scoreList.length-1));
			scoreSum -= scoreList[scoreList.length-1];
			scoreList[scoreList.length-1] = 0;
		}
		return ret;
	}
	
	public static <T> T selectBestRandom(List<T> list, ToDoubleFunction<T> weight, Random rand) {
		List<T> bests = new LinkedList<>();
		double best = -Double.MAX_VALUE;
		for (T e : list) {
			double score = weight.applyAsDouble(e);
			if (score > best) {
				bests.clear();
				bests.add(e);
				best = score;
			} else if (score == best) {
				bests.add(e);
			}
		}
		return bests.get(rand.nextInt(bests.size()));
	}
	
	public static double pow(double base, int exponent) {
		assert(exponent > 0);
		if (exponent == 0) return 1;
		if (exponent == 1) return base;
		if (base == 1) return 1;
		if (base == 0) return 0; // 0^n == delta(n), 
		double res = 1;
		for (int i = 31; i >= 0; i--) { // 31: number of bits of integer.
			res *= res;
			if ((exponent & (1 << i)) != 0) { res *= base; }
		}
		return res;
	}
	
	public static long power(long x, int n) {
		assert(n >= 0);
		if (n == 0) return 1;
		if (n == 1) return x;
		long res = 1;
		for (int i = 31; i >= 0; i--) { // 31: number of bits of integer.
			res *= res;
			if ((n & (1<<i)) != 0) {res *= x;}
		}
		return res;
	}
}
