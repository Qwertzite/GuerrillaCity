package qwertzite.guerrillacity.core.util;

import net.minecraft.util.Tuple;

public class IntObjTuple<B> extends Tuple<Integer, B> {
	private int a; // VERSION: check superclass to see if this field is covered.

	public IntObjTuple(int pA, B pB) {
		super(pA, pB);
		this.a = pA;
	}
	
	/**
	 * Returns left value without parsing.
	 * @return
	 */
	public int getIntA() {
		return this.a;
	}
	
	@Override
	public Integer getA() {
		return this.a;
	}
	
	public void setA(int pA) {
		this.a = pA;
	}
	
	@Override
	public void setA(Integer pA) {
		this.a = pA;
	}
}
