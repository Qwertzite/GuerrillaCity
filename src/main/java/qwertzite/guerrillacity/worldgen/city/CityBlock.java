package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.McUtil;
import qwertzite.guerrillacity.core.util.math.DoubleObjTuple;
import qwertzite.guerrillacity.core.util.math.IntObjTuple;
import qwertzite.guerrillacity.core.util.math.Rectangle;
import qwertzite.guerrillacity.core.util.math.Vec2i;

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
	private final Rectangle blockShape;
	private final Set<Rectangle> validArea;
	private final Set<Rectangle> forbiddenArea;
	private final int groundHeight;
	private final EnumMap<Direction, Integer> roadLevel;
	
	private DivEntry division;
	
	private Set<ArrangementPlacement> arrangements = new HashSet<>();
	
	
	public CityBlock(long seed, int level, Rectangle blockShape, Set<Rectangle> validArea, Set<Rectangle> forbiddenArea, int groundHeght, EnumMap<Direction, Integer> roadLevel) {
		this.seed = seed;
		this.level = level;
		this.blockShape = blockShape;
		this.validArea = validArea;
		this.forbiddenArea = forbiddenArea;
		this.groundHeight = groundHeght;
		this.roadLevel = roadLevel;
	}
	
	/**
	 * この段階ではスコアさえ返せばよい．配置予定の建物などは各CityBlockで保持しておく．
	 * とりあえず自前で用意するつもりで保持し，ダメならその時にnullを代入する
	 * もし子Blockを使わない場合はこれを破棄してしまう．
	 * @param fjp
	 * @return
	 */
	public double init(ForkJoinPool fjp) {
		Random rand = new Random(this.seed);
		
		this.submitChildCityBlockInit(rand, fjp); // begin computation of child city blocks.
		
		double ownScore = this.arrangeBuildingSet(rand); // arrange buildings on their own.
		return this.collectDivisionResult(ownScore); // returns "ownScore" if that is the best score.
	}
	
	// ==== * ==== create child city blocks. ==== * ====
	
	private void submitChildCityBlockInit(Random rand, ForkJoinPool fjp) {// XXX: try multiple division patterns.
		if (this.level >= 100) { return; } // limiter
		int roadWidth = CityConst.getRoadWidthForLevel(level);
		final int minBlocksize = CityConst.MIN_BUILDING_SIZE;
		int xSize = blockShape.getXSpan() - roadWidth - minBlocksize*2;
		int zSize = blockShape.getYSpan() - roadWidth - minBlocksize*2;
		if (xSize <= 0 && zSize <= 0) { return;}
		
		if (xSize < 0) xSize = 0;
		if (zSize < 0) zSize = 0;
		boolean isXdirection =  xSize*xSize > rand.nextInt(xSize*xSize + zSize*zSize); // if true, x direction size will decrease.
		
		int possibleRange = isXdirection ? xSize : zSize;
		int roadPos = minBlocksize + rand.nextInt((possibleRange) / 2 + 1) + rand.nextInt((possibleRange + 1) / 2 + 1);
		
		int xSize1 = isXdirection ? roadPos : blockShape.getXSpan();
		int xSize2 = isXdirection ? blockShape.getXSpan() - roadPos - roadWidth : blockShape.getXSpan();
		int zSize1 =!isXdirection ? roadPos : blockShape.getYSpan();
		int zSize2 =!isXdirection ? blockShape.getYSpan() - roadPos - roadWidth : blockShape.getYSpan();
		int xPos1 = this.blockShape.getMinX();
		int xPos2 = this.blockShape.getMinX() + ( isXdirection ? roadPos + roadWidth : 0);
		int zPos1 = this.blockShape.getMinY();
		int zPos2 = this.blockShape.getMinY() + (!isXdirection ? roadPos + roadWidth : 0);
		CityBlock child1 = this.createChildCityBlock(rand.nextLong(), new Rectangle(xPos1, zPos1, xSize1, zSize1), isXdirection ? Direction.EAST : Direction.SOUTH);
		CityBlock child2 = this.createChildCityBlock(rand.nextLong(), new Rectangle(xPos2, zPos2, xSize2, zSize2), isXdirection ? Direction.WEST : Direction.NORTH);
		
		if (child1 == null || child2 == null) return;
		this.division = new DivEntry(child1, child2, fjp, isXdirection, roadPos);
	}
	
	private CityBlock createChildCityBlock(long seed, Rectangle childShape, Direction newRoadDir) {
		Set<Rectangle> valid = this.validArea.stream().filter(area -> area.intersects(childShape)).collect(Collectors.toSet());
		if (valid.size() <= 0) return null;
		Set<Rectangle> forbidden = this.forbiddenArea.stream().filter(area -> area.intersects(childShape)).collect(Collectors.toSet());
		EnumMap<Direction, Integer> newRoadLevel = new EnumMap<>(this.roadLevel);
		newRoadLevel.put(newRoadDir, this.level);
		CityBlock child = new CityBlock(seed, this.level+1, childShape, valid, forbidden, this.groundHeight, newRoadLevel);
		return child;
	}
	
	private double collectDivisionResult(double ownScore) {
		if (this.division == null) return ownScore;
		double divScore = this.division.waitAndGetScore();
		if (ownScore >= divScore) {
			this.division = null;
			return ownScore;
		}
		this.arrangements.clear();
		return divScore;
	}
	
	// ==== * ==== place buildings on this own === * ====
	
	private double arrangeBuildingSet(Random rand) {
		
		Direction front = GcUtil.selectBestRandom(List.of(McUtil.horizontalDir()), d -> this.roadLevel.get(d), rand); // The adjacent road with highest priority.
		Direction side1 = this.roadLevel.get(front.getClockWise()) == this.roadLevel.get(front.getCounterClockWise()) ?
				rand.nextBoolean() ? front.getClockWise() : front.getCounterClockWise() :
					this.roadLevel.get(front.getClockWise()) < this.roadLevel.get(front.getCounterClockWise()) ? front.getClockWise() : front.getCounterClockWise();
		Direction side2 = side1.getOpposite();
		Direction back = front.getOpposite();
		if (roadLevel.get(front) >= 100) return 0.0d; // If surrounded by the outermost roads, no buildings can be placed.
		
		final int blockWidth = getAreaWidthForDir(front);
		final int blockLength = getAreaLengthForDir(front);
		
		double bestScore = Double.MIN_VALUE;
		
		{ // front only
			int width = blockWidth;
			int length = blockLength;
			var buildingsets = BuildingLoader.getApplicableBuildginSets(width, length);
			var arrangement = buildingsets.stream()
					.map(bs -> {
						var arrangements = bs.getApplicableArrangements(width);
						double bestArrScore = Double.MIN_VALUE;
						List<DoubleObjTuple<BuildingArrangement>> weightedArrangements = new ArrayList<>();
						for (BuildingArrangement arr : arrangements) {
							double arrScore = arr.getBaseScore() * this.getRoadCoef(front);
							arrScore -= arr.getNegativeSideDecraction((length - arr.getNegativeSideOpening())*2) * this.getRoadCoef(front.getCounterClockWise());
							arrScore -= arr.getPositiveveSideDecraction((length - arr.getPositiveSideOpening())*2) * this.getRoadCoef(front.getClockWise());
							if (arrScore > bestArrScore) bestArrScore = arrScore;
							if (arrScore > 0) weightedArrangements.add(new DoubleObjTuple<BuildingArrangement>(arrScore, arr));
						}
						var selectedArr = GcUtil.selectWeightedRandom(weightedArrangements, e -> e.getDoubleA(), rand);
						return new DoubleObjTuple<>(bestArrScore, selectedArr.getB());
					})
					.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA())); // bs -> bs score -> arr
			if (arrangement.isPresent()) {
				if (bestScore <= arrangement.get().getDoubleA()) {
					bestScore = arrangement.get().getDoubleA();
					this.arrangements.clear();
					this.arrangements.add(new ArrangementPlacement(arrangement.get().getB(), switch (front) {
					case EAST -> this.blockShape.getNorthEast();
					case NORTH -> this.blockShape.getNorthWest();
					case SOUTH -> this.blockShape.getSouthEast();
					case WEST -> this.blockShape.getSouthWest();
					default -> null;
					}, front));
				}
			}
		}
		
		// COMEBACK: とりあえず正面だけから順に実装する
		
		return bestScore;
	}
	
	private int getRoadCoef(Direction dir) {
		return CityConst.getRoadWidthForLevel(this.roadLevel.get(dir));
	}
	
//	/**
//	 * 正面のみに建物を配置する場合
//	 * @return
//	 */
//	private double roadSetFrontOnly(Direction dir, Random rand) {
//		
//		CityGenResult result = new CityGenResult();
//		
//		int width = getAreaWidthForDir(dir);
//		int length = getAreaLengthForDir(dir);
//		BuildingArrangement arrangement = BuildingLoader.getBestArrangement(width, length, rand);
//		if (arrangement == null) return result;
////		BuildingArrangement arrangement = BuildingLoader.getBuildingArrangement(width, length, rand);
////		if (arrangement == null) return result;
////		
////		this.arrangeBuildings(result, arrangement, dir, rand);
////		result.addScore(arrangement.getScore(length) * CityConst.getRoadWidthForLevel(this.roadLevel.get(dir)));
//		return result;
//	}
	
//	private CityGenResult roadSetFrontBack(final Direction frontDir, final Random rand) {
//		if (this.roadLevel.get(frontDir.getOpposite()) >= 100) return CityGenResult.EMPTY;
//		
//		final int width = getAreaWidthForDir(frontDir);
//		final int length = getAreaLengthForDir(frontDir);
//		if(length <= CityConst.MIN_BUILDING_SIZE*2+1) return CityGenResult.EMPTY;
//		final double fcoef = CityConst.getRoadWidthForLevel(this.roadLevel.get(frontDir));
//		final double bcoef = CityConst.getRoadWidthForLevel(this.roadLevel.get(frontDir.getOpposite()));
//		
//		CityGenResult result = CityGenResult.EMPTY;
////		double maxScore = 0.0d;
////		for (int i = 0; i < 8; i++) {
////			final int flen = rand.nextInt(length - CityConst.MIN_BUILDING_SIZE*2 - 1) + CityConst.MIN_BUILDING_SIZE;
////			BuildingArrangement front = BuildingLoader.getBuildingArrangement(width, flen, rand);
////			if (front == null) continue;
////			final int backLength = length - front.getMaxLength() - 1;
////			final BuildingArrangement back = BuildingLoader.getBuildingArrangement(width, backLength, rand);
////			if (back == null) continue;
////			final double score = front.getScore(flen) * fcoef + back.getScore(backLength) * bcoef;
////			if (score > maxScore || (score == maxScore && rand.nextBoolean())) {
////				maxScore = score;
////				result = new CityGenResult();
////				arrangeBuildings(result, front, frontDir, rand);
////				arrangeBuildings(result, back, frontDir.getOpposite(), rand);
////				result.addScore(score);
////			}
////		}
//		
//		return result;
//	}
	
//	private CityGenResult roadSetSides(final Direction[] dirPriprity, final Random rand) {
//		
//	}
	
	private int getAreaWidthForDir(Direction dir) {
		return dir.getAxis() == Axis.X ? this.blockShape.getYSpan() : this.blockShape.getXSpan();
	}
	
	private int getAreaLengthForDir(Direction dir) {
		return dir.getAxis() == Axis.X ? this.blockShape.getXSpan() : this.blockShape.getYSpan();
	}
	
	private void arrangeBuildings(CityGenResult result, BuildingArrangement arrangement, Direction dir, Random rand) {
		BlockPos origin = this.generationPoint(dir);
		Direction sideWays = dir.getClockWise();
		
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
		Rectangle rect = this.blockShape;
		return switch (dir) {
		case EAST  -> new BlockPos(rect.getMaxX(), this.groundHeight, rect.getMinY());
		case WEST  -> new BlockPos(rect.getMinX(), this.groundHeight, rect.getMaxY());
		case NORTH -> new BlockPos(rect.getMinX(), this.groundHeight, rect.getMinY());
		case SOUTH -> new BlockPos(rect.getMaxX(), this.groundHeight, rect.getMaxY());
		default -> {
			assert(false);
			yield null;
			}
		};
	}
	
	// ==== * ==== posit init ==== * ====
	
	public CityGenResult postInit(ForkJoinPool fjp) {
		
		if (this.division != null) {
			ForkJoinTask<CityGenResult> task1 = fjp.submit(() -> this.division.child1.postInit(fjp));
			ForkJoinTask<CityGenResult> task2 = fjp.submit(() -> this.division.child2.postInit(fjp));
			CityGenResult result1 = task1.join();
			CityGenResult result2 = task2.join();
			CityGenResult result3 = CityGenResult.integrate(result1, result2);
			Axis roadDir = this.division.divDir() ? Axis.Z : Axis.X;
			int roadWidth = CityConst.getRoadWidthForLevel(level);
			if (this.division.divDir()) {
				int minX = this.blockShape.getMinX() + this.division.roadPos();
				for (int z4 = this.blockShape.getMinY() - Mth.positiveModulo(this.blockShape.getMinY(), 4); z4 <= this.blockShape.getMaxY(); z4 += 4) {
					RoadElement element = new RoadElement(new BlockPos(minX, this.groundHeight, z4), roadDir, roadWidth);
					if (!this.forbiddenArea.stream().anyMatch(rect -> rect.intersects(element.getCircumBox()))) result3.addRoadElement(element);
				}
			} else {
				int minZ = this.blockShape.getMinY() + this.division.roadPos();
				for (int x4 = this.blockShape.getMinX() - Mth.positiveModulo(this.blockShape.getMinX(), 4); x4 <= this.blockShape.getMaxX(); x4 += 4) {
					RoadElement element = new RoadElement(new BlockPos(x4, this.groundHeight, minZ), roadDir, roadWidth);
					if (!this.forbiddenArea.stream().anyMatch(bb -> bb.intersects(element.getCircumBox()))) result3.addRoadElement(element);
				}
			}
			result3.incrementRoadCount();
			return result3;
		} else {
			Random rand = new Random(this.seed*31);
			CityGenResult result = new CityGenResult();
			for (ArrangementPlacement arr : this.arrangements) {
				Direction front = arr.front();
				Direction side = front.getClockWise();
				BlockPos pos = new BlockPos(arr.relativePos().getX(), this.groundHeight, arr.relativePos().getY());
				for (var entry : arr.arrangement.getPositions()) {
					BuildingType type = entry.getB();
					result.addBuilding(type.getBuildingInstance(pos.relative(side, entry.getIntA()), front, rand.nextLong()));
				}
			}
			return result;
		}
	}
	
	private static record DivEntry(CityBlock child1, CityBlock child2, ForkJoinTask<Double> task1, ForkJoinTask<Double> task2, boolean divDir, int roadPos) {
		
		public DivEntry(CityBlock child1, CityBlock child2, ForkJoinPool fjp, boolean divDir, int roadPos) {
			this(child1, child2, fjp.submit(() -> child1.init(fjp)), fjp.submit(() -> child2.init(fjp)), divDir, roadPos);
		}
		
		public double waitAndGetScore() {
			return this.task1.join() + this.task2.join();
		}
	}
	
	private static record ArrangementPlacement(BuildingArrangement arrangement, Vec2i relativePos, Direction front) {
		
	}
}
