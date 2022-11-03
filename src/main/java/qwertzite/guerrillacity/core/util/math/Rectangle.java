package qwertzite.guerrillacity.core.util.math;

import net.minecraft.core.BlockPos;

/**
 * Expresses a rectangle with shape and position.
 * @author Qwertzite
 * @date 2022/10/30
 */
public record Rectangle(Vec2i position, Vec2i shape) {
	
	public Rectangle(int posX, int posY, int xSize, int ySize) {
		this(new Vec2i(posX, posY), new Vec2i(xSize, ySize));
	}
	
	public static Rectangle area(int x1, int y1, int x2, int y2) {
		int xSize = Math.abs(x2 - x1) + 1;
		int ySize = Math.abs(y2 - y1) + 1;
		int xPos = Math.min(x1,  x2);
		int yPos = Math.min(y1,  y2);
		return new Rectangle(xPos, yPos, xSize, ySize);
	}
	
	public int getMinX() {
		return this.position.getX();
	}
	
	/**
	 * The maximum x coordinate within this rectangle.<br>
	 * <i>{@code pos}</i><sub>{@code x}</sub> + <i>{@code width}</i> -1
	 * @return the max x coordinate.
	 */
	public int getMaxX() {
		return this.position.getX() + shape.getX() - 1;
	}
	
	public int getMinY() {
		return this.position.getY();
	}
	
	/**
	 * The maximum y coordinate within this rectangle.<br>
	 * <i>{@code pos}</i><sub>{@code y}</sub> + <i>{@code width}</i> -1
	 * @return the max y coordinate.
	 */
	public int getMaxY() {
		return this.position.getY() + this.shape.getY() - 1;
	}
	
	public int getXSpan() {
		return this.shape.getX();
	}
	
	public int getYSpan() {
		return this.shape.getY();
	}
	
	public boolean intersects(Rectangle other) {
		return this.getMaxX() >= other.getMinX() && this.getMinX() <= other.getMaxX() &&
				this.getMaxY() >= other.getMinY() && this.getMinY() <= other.getMaxY();
	}
	
	public boolean isInside(BlockPos pos) {
		return this.getMinX() <= pos.getX() && pos.getX() <= this.getMaxX() &&
				this.getMinY() <= pos.getZ() && pos.getZ() <= this.getMaxY();
	}
	
	public Vec2i getNorthWest() {
		return this.position;
	}
	
	public Vec2i getNorthEast() {
		return this.position.offset(this.getXSpan() - 1, 0);
	}
	
	public Vec2i getSouthEast() {
		return this.position.offset(this.getXSpan() - 1, this.getYSpan() - 1);
	}
	
	public Vec2i getSouthWest() {
		return this.position.offset(0, this.getYSpan() - 1);
	}
}
