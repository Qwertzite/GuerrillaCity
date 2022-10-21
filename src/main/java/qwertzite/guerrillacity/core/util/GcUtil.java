package qwertzite.guerrillacity.core.util;

import java.util.Random;
import java.util.function.IntPredicate;

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
}
