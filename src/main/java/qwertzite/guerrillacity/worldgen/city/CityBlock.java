package qwertzite.guerrillacity.worldgen.city;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.IntObjTuple;
import qwertzite.guerrillacity.core.util.McUtil;

/**
 * This class has nothing to do with {@link net.minecraft.world.level.block.Block}.<br>
 * This class divides city area and place buildings and roads.
 * 
 * @author Qwertzite
 * @date 2022/10/09
 */
public class CityBlock {
	
	private final long seed;
	private final int level;
	private final BoundingBox blockArea;
	private final Set<BoundingBox> validArea;
	private final Set<BoundingBox> forbiddenArea;
	private final int groundHeight;
	private final EnumMap<Direction, Integer> roadLevel;
	
	private boolean devide = false;
	private Supplier<CityGenResult> task1;
	private Supplier<CityGenResult> task2;
	private boolean isDivXdirection;
	private int roadPos;
	
	public CityBlock(long seed, int level, BoundingBox blockArea, Set<BoundingBox> validArea, Set<BoundingBox> forbiddenArea, int groundHeght, EnumMap<Direction, Integer> roadLevel) {
		this.seed = seed;
		this.level = level;
		this.blockArea = blockArea;
		this.validArea = validArea;
		this.forbiddenArea = forbiddenArea;
		this.groundHeight = groundHeght;
		this.roadLevel = roadLevel;
	}
	
	public CityGenResult init(ForkJoinPool fjp) {
		Random rand = new Random(this.seed);
		
		// 分割して建物を配置する
		// divide and place buildings.
		this.devideAndGenerate(rand, fjp);
		
		// 自前で建物を配置する
		CityGenResult result0 = this.arrangeBuildingSet(rand);
		if (!this.devide) return result0;
		
		//		// スコアを比較する
		CityGenResult result1 = task1.get();
		CityGenResult result2 = task2.get();
		double scoreDivision = result1.getScore() + result2.getScore();
		
		// 自前で配置したほうが成績が良かったらそのまま返す
		if (result0.getScore() > scoreDivision) return result0;

		// 分割したほうがスコアがいい場合
		return this.mergeDevidedBlocks(result1, result2);
	}
	
	private void devideAndGenerate(Random rand, ForkJoinPool fjp) {
		if (this.level >= 100) {
			this.task1 = () -> CityGenResult.EMPTY;
			this.task2 = () -> CityGenResult.EMPTY;
			return;
		}
		
		int roadWidth = CityConst.getRoadWidthForLevel(level);
		final int minBlocksize = CityConst.MIN_BUILDING_SIZE;
		int xSize = blockArea.getXSpan() - roadWidth - minBlocksize*2;
		int zSize = blockArea.getZSpan() - roadWidth - minBlocksize*2;
		
		if (xSize <= 0 && zSize <= 0) {
			this.task1 = () -> CityGenResult.EMPTY;
			this.task2 = () -> CityGenResult.EMPTY;
			return;
		}
		if (xSize < 0) xSize = 0;
		if (zSize < 0) zSize = 0;
		boolean isXdirection =  xSize*xSize > rand.nextInt(xSize*xSize + zSize*zSize); // 分割方向 true ならx方向の長さが小さくなる
		
		int possibleRange = isXdirection ? xSize : zSize;
		int roadPos = minBlocksize + rand.nextInt((possibleRange) / 2 + 1) + rand.nextInt((possibleRange + 1) / 2 + 1);
		
		
		int xPos11 = blockArea.minX();
		int xPos12 = isXdirection ? xPos11 + roadPos   -1 : blockArea.maxX();
		int xPos21 = isXdirection ? xPos12 + roadWidth +1 : blockArea.minX();
		int xPos22 = blockArea.maxX();
		int zPos11 = blockArea.minZ();
		int zPos12 =!isXdirection ? zPos11 + roadPos   -1 : blockArea.maxZ();
		int zPos21 =!isXdirection ? zPos12 + roadWidth +1 : blockArea.minZ();
		int zPos22 = blockArea.maxZ();
		int yPos1 = blockArea.minY();
		int yPos2 = blockArea.maxY();
		
		BoundingBox child1Area = new BoundingBox(xPos11, yPos1, zPos11, xPos12, yPos2, zPos12);
		CityBlock child1 = this.createChildCityBlock(rand.nextLong(), child1Area, isXdirection ? Direction.EAST : Direction.SOUTH);
		
		BoundingBox child2Area = new BoundingBox(xPos21, yPos1, zPos21, xPos22, yPos2, zPos22);
		CityBlock child2 = this.createChildCityBlock(rand.nextLong(), child2Area, isXdirection ? Direction.WEST : Direction.NORTH);
		
		if (child1 != null) {
			ForkJoinTask<CityGenResult> task1 = fjp.submit(() -> child1.init(fjp));
			this.task1 = () -> task1.join();
		} else this.task1 = () -> CityGenResult.EMPTY;
		
		if (child2 != null) {
			ForkJoinTask<CityGenResult> task2 = fjp.submit(() -> child2.init(fjp));
			this.task2 = () -> task2.join();
		} else this.task2 = () -> CityGenResult.EMPTY;
		
		this.roadPos = roadPos;
		this.isDivXdirection = isXdirection;
		this.devide = true;
	}
	
	private CityBlock createChildCityBlock(long seed, BoundingBox childArea, Direction newRoadDir) {
		Set<BoundingBox> valid = this.validArea.stream().filter(area -> area.intersects(childArea)).collect(Collectors.toSet());
		if (valid.size() <= 0) return null;
		Set<BoundingBox> forbidden = this.forbiddenArea.stream().filter(area -> area.intersects(childArea)).collect(Collectors.toSet());
		EnumMap<Direction, Integer> newRoadLevel = new EnumMap<>(this.roadLevel);
		newRoadLevel.put(newRoadDir, this.level);
		CityBlock child = new CityBlock(seed, this.level+1, childArea, valid, forbidden, this.groundHeight, newRoadLevel);
		return child;
	}
	
	private CityGenResult mergeDevidedBlocks(CityGenResult result1, CityGenResult result2) {
		CityGenResult result3 = CityGenResult.integrate(result1, result2);
		Axis roadDir = this.isDivXdirection ? Axis.Z : Axis.X;
		int roadWidth = CityConst.getRoadWidthForLevel(level);
		
		if (this.isDivXdirection) {
			int minX = this.blockArea.minX() + this.roadPos;
			for (int z4 = this.blockArea.minZ() - Mth.positiveModulo(this.blockArea.minZ(), 4); z4 <= this.blockArea.maxZ(); z4 += 4) {
				RoadElement element = new RoadElement(new BlockPos(minX, this.groundHeight, z4), roadDir, roadWidth);
				if (!this.forbiddenArea.stream().anyMatch(bb -> bb.intersects(element.getCircumBox()))) result3.addRoadElement(element);
			}
		} else {
			int minZ = this.blockArea.minZ() + this.roadPos;
			for (int x4 = this.blockArea.minX() - Mth.positiveModulo(this.blockArea.minX(), 4); x4 <= this.blockArea.maxX(); x4 += 4) {
				RoadElement element = new RoadElement(new BlockPos(x4, this.groundHeight, minZ), roadDir, roadWidth);
				if (!this.forbiddenArea.stream().anyMatch(bb -> bb.intersects(element.getCircumBox()))) result3.addRoadElement(element);
			}
		}
		result3.incrementRoadCount();
		
		return result3;
	}
	
	private CityGenResult arrangeBuildingSet(Random rand) {
		
		Direction[] priority;
		{ // To which direction this block is facing.
			int size = McUtil.horizontalDir().length;
			priority = Arrays.copyOf(McUtil.horizontalDir(), McUtil.horizontalDir().length);
			GcUtil.shuffleArray(priority, rand);
			for (int i = 0; i < size; i++) {
				int weight = this.roadLevel.get(priority[i]);
				int ii = i;
				for (int j = i+1; j < size; j++) {
					if (weight > this.roadLevel.get(priority[j])) {
						weight = this.roadLevel.get(priority[j]);
						ii = j;
					}
				}
				Direction tmp = priority[i];
				priority[i] = priority[ii];
				priority[ii] = tmp;
			}
		}
		
		if (priority.length < 1 || roadLevel.get(priority[0]) >= 100) return new CityGenResult(); // 全て最外周の道だった場合
		CityGenResult result0 = this.roadSetFrontOnly(priority[0], rand);
		// 様々な配置戦略に従って建物を配置する
		// この時点では幅と奥行きが分かっている
		
		// 裏側にも建物を配置する場合について，
		
		
		// XXX: add other placement patterns
		// compare results.
//		result0.addBuilding(new DummyBuilding(this.blockArea, groundHeight));
		return result0;
	}
	
	/**
	 * 正面のみに建物を配置する場合
	 * @return
	 */
	private CityGenResult roadSetFrontOnly(Direction dir, Random rand) {
		CityGenResult result = new CityGenResult();
		
		int width = dir.getAxis() == Axis.X ? this.blockArea.getZSpan() : this.blockArea.getXSpan();
		int length = dir.getAxis() == Axis.X ? this.blockArea.getXSpan() : this.blockArea.getZSpan();
		BuildingArrangement arrangement = BuildingLoader.getBuildingSet(width, length, rand);
		if (arrangement == null) return result;
		
		this.arrangeBuildings(result, arrangement, dir, rand);
		return result;
	}
	
	private void arrangeBuildings(CityGenResult result, BuildingArrangement arrangement, Direction dir, Random rand) {
		BlockPos origin = this.generationPoint(dir);
		Direction sideWays = dir.getClockWise();
		
//		BuildingType type = arrangement.getPositions().get(0).getB();
//		result.addBuilding(type.getBuildingInstance(origin, dir, rand.nextLong()));
		
		for (IntObjTuple<BuildingType> t : arrangement.getPositions()) {
			int pos = t.getIntA();
			BuildingType type = t.getB();
			result.addBuilding(type.getBuildingInstance(origin.relative(sideWays, pos), dir, rand.nextLong()));
		}
	}
	
	/**
	 * BuildingSetを配置する位置
	 * @param dir
	 * @return
	 */
	private BlockPos generationPoint(Direction dir) {
		BoundingBox bb = this.blockArea;
		return switch (dir) {
		case EAST  -> new BlockPos(bb.maxX(), this.groundHeight, bb.minZ());
		case WEST  -> new BlockPos(bb.minX(), this.groundHeight, bb.maxZ());
		case NORTH -> new BlockPos(bb.minX(), this.groundHeight, bb.minZ());
		case SOUTH -> new BlockPos(bb.maxX(), this.groundHeight, bb.maxZ());
		default -> {
			assert(false);
			yield null;
			}
		};
	}
}
