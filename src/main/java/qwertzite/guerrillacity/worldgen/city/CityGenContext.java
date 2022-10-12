package qwertzite.guerrillacity.worldgen.city;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.util.Matrix4x4;

public class CityGenContext {
	
	private LevelAccessor level;
	private BoundingBox genArea;
	private Map<BlockPos, BlockState> resultMap = new HashMap<BlockPos, BlockState>();
	
	private Deque<Matrix4x4> stack = new LinkedList<>();
	private Matrix4x4 current = Matrix4x4.E;
	
	private Rotation rotation = Rotation.NONE;
	private boolean mirror = false;
	
	public CityGenContext(LevelAccessor level, BoundingBox genArea) {
		this.level = level;
		this.genArea = genArea;
	}
	
	public void setBlockState(BlockPos pos, BlockState state) {
		BlockPos translated = this.current.apply(pos);
		state = state.mirror(this.mirror ? Mirror.FRONT_BACK : Mirror.NONE).rotate(this.level, translated, this.rotation);
		if (this.genArea.isInside(translated)) {
			this.resultMap.put(translated, state);
		}
	}
	
	public void pushMatrix() {
		this.stack.push(this.current);
	}
	
	public void popMatrix() {
		this.current = this.stack.pop();
	}
	
	private void update() {
		int[] front = this.current.computeDirection(0, 0, 1);
		if (front[2] == 1) { // positive z 0
			this.rotation = Rotation.NONE;
		} else if (front[0] == 1) { // positive x 90CCW
			this.rotation = Rotation.COUNTERCLOCKWISE_90;
		} else if (front[2] == -1) { // negative z 180 
			this.rotation = Rotation.CLOCKWISE_180;
		} else if (front[0] == -1) { // negative x 90CW
			this.rotation = Rotation.CLOCKWISE_90;
		} else {
			assert(false);
		}
		int[] left = this.current.computeDirection(1, 0, 0); // left
		this.mirror = (front[2]*left[0] - left[2]*front[0]) < 0;
	}
	
	public void translate(int x, int y, int z) {
		this.current = this.current.mult(Matrix4x4.translate(x, y, z));
		this.update();
	}
	
	public void rotate(Rotation rotation) {
		this.current = this.current.mult(Matrix4x4.rotate(rotation));
		this.update();
	}
	
	public void mirror() {
		this.current = this.current.mult(Matrix4x4.mirror());
		this.update();
	}
	
	public Map<BlockPos, BlockState> getStateMap() { return this.resultMap; }
}
