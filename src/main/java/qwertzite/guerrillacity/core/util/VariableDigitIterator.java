package qwertzite.guerrillacity.core.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class VariableDigitIterator implements Iterator<int[]> {

	private int[] min;
	private int[] max;
	
	private int[] current;
	
	private boolean finished;
	
	/**
	 * 
	 * @param min
	 * @param max inclusive.
	 */
	public VariableDigitIterator(int[] min, int[] max) {
		this.min = min;
		this.max = max;
		this.current = Arrays.copyOf(min, min.length);
	}

	@Override
	public boolean hasNext() {
		return !this.finished;
	}

	@Override
	public int[] next() {
		if (this.finished) throw new NoSuchElementException();
		int length = this.current.length;
		int[] ret = Arrays.copyOf(this.current, length);
		
		for (int i = 0; i < length; i++) {
			this.current[i]++;
			if (this.current[i] > this.max[i]) {
				this.current[i] = this.min[i];
				if (i+1 >= length) {
					this.finished = true;
					break;
				}
			} else break;
		}
		if (length == 0) this.finished = true;
		return ret;
	}

}
