package qwertzite.guerrillacity.core.util.math;

import net.minecraft.util.Tuple;

public class DoubleObjTuple<B> extends Tuple<Double, B> {
	private double a; // VERSION: check superclass to see if this field is covered.

	public DoubleObjTuple(double pA, B pB) {
		super(pA, pB);
		this.a = pA;
	}
	
	/**
	 * Returns left value without parsing.
	 * @return
	 */
	public double getDoubleA() {
		return this.a;
	}
	
	@Override
	public Double getA() {
		return this.a;
	}
	
	public void setA(double pA) {
		this.a = pA;
	}
	
	@Override
	public void setA(Double pA) {
		this.a = pA;
	}
}
