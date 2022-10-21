package qwertzite.guerrillacity.core.util;

import java.util.stream.IntStream;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;

import net.minecraft.Util;
import net.minecraft.util.Mth;

public class Vec2i implements Comparable<Vec2i> {
	
	public static final Codec<Vec2i> CODEC = Codec.INT_STREAM.comapFlatMap(stream -> {
		return Util.fixedSize(stream, 2).map((arr) -> {
			return new Vec2i(arr[0], arr[1]);
		});
	}, (vec2i) -> {
		return IntStream.of(vec2i.getX(), vec2i.getY());
	});
	/** An immutable vector with zero as all coordinates. */
	public static final Vec2i ZERO = new Vec2i(0, 0);
	private final int x;
	private final int y;
	
	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2i(double pX, double pY) {
		this(Mth.floor(pX), Mth.floor(pY));
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof Vec2i vec2i)) {
			return false;
		} else {
			if (this.getX() != vec2i.getX()) {
				return false;
			} else {
				return this.getY() != vec2i.getY();
			}
		}
	}

	@Override
	public int hashCode() {
		return this.getY() * 31 + this.getX();
	}

	@Override
	public int compareTo(Vec2i other) {
		if (this.getY() == other.getY()) {
			return this.getX() - other.getX();
		} else {
			return this.getY() - other.getY();
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public Vec2i offset(double pDx, double pDy) {
		return pDx == 0.0D && pDy == 0.0 ? this : new Vec2i((double) this.getX() + pDx, (double) this.getY() + pDy);
	}

	public Vec2i offset(int pDx, int pDy) {
		return pDx == 0 && pDy == 0 ? this : new Vec2i(this.getX() + pDx, this.getY() + pDy);
	}

	public Vec2i offset(Vec2i pVector) {
		return this.offset(pVector.getX(), pVector.getY());
	}

	public Vec2i subtract(Vec2i pVector) {
		return this.offset(-pVector.getX(), -pVector.getY());
	}

	public Vec2i multiply(int pScalar) {
		if (pScalar == 1) {
			return this;
		} else {
			return pScalar == 0 ? ZERO : new Vec2i(this.getX() * pScalar, this.getY() * pScalar);
		}
	}

	public int distManhattan(Vec2i pVector) {
		float f = (float) Math.abs(pVector.getX() - this.getX());
		float f1 = (float) Math.abs(pVector.getY() - this.getY());
		return (int) (f + f1);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).toString();
	}

	public String toShortString() {
		return this.getX() + ", " + this.getY();
	}
}
