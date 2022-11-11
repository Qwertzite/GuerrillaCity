package qwertzite.guerrillacity.worldgen.city;

import java.util.Collections;
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
import net.minecraft.util.Tuple;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.McUtil;
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
	private static final double MIN_SCORE = -Double.MAX_VALUE * (1.0d / (1 << 16));
	
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
		double finalScore = this.collectDivisionResult(ownScore); // returns "ownScore" if that is the best score.
		int iter  = 0;
		while (this.level == 0 && ownScore == finalScore) {
			this.submitChildCityBlockInit(rand, fjp); // begin computation of child city blocks.
			finalScore = this.collectDivisionResult(ownScore); // returns "ownScore" if that is the best score.
			iter++;
		}
		if (this.level == 0 && iter != 0) ModLog.info("Retried %d times to generate ward with seed %d", iter, this.seed);
		return finalScore;
	}
	
	// ==== * ==== create child city blocks. ==== * ====
	
	private void submitChildCityBlockInit(Random rand, ForkJoinPool fjp) {
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
		
		if (child1 == null && child2 == null) return;
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
		
		final Direction front = GcUtil.selectBestRandom(List.of(McUtil.horizontalDir()), d -> -this.roadLevel.get(d), rand); // The adjacent road with highest priority.
		final Direction side1 = this.roadLevel.get(front.getClockWise()) == this.roadLevel.get(front.getCounterClockWise()) ?
				rand.nextBoolean() ? front.getClockWise() : front.getCounterClockWise() :
					this.roadLevel.get(front.getClockWise()) < this.roadLevel.get(front.getCounterClockWise()) ? front.getClockWise() : front.getCounterClockWise();
		final Direction side2 = side1.getOpposite();
		final Direction back = front.getOpposite();
		if (roadLevel.get(front) >= 100) return MIN_SCORE; // If surrounded by the outermost roads, no buildings can be placed.
		final int blockWidth = getAreaWidthForDir(front);
		final int blockLength = getAreaLengthForDir(front);
		
		double bestScore = MIN_SCORE;
		
		{ // front only
			int width = blockWidth;
			int length = blockLength;
			var buildingsets = BuildingLoader.getApplicableBuildingSets(width, length);
			var arrangement = buildingsets.stream()
					.map(bs -> bs.computeBuildingArrangement(width, arr -> computeScoreForFrontOnly(arr, front, length), rand))
					.filter(e -> e != null)
					.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA())); // bs -> bs score -> arr
			if (arrangement.isPresent()) {
				var selectedArr = arrangement.get().getB();
				double arrScore = computeScoreForFrontOnly(selectedArr, front, length);
				if (bestScore <= arrScore) {
					bestScore = arrScore;
					this.arrangements.clear();
					this.arrangements.add(new ArrangementPlacement(selectedArr, this.getCornerPos(front, 0), front));
				}
			}
		}
		List<BuildingArrangement> fronts =
				GcUtil.selectWeightedMultipleRandom(
						BuildingLoader.getApplicableBuildingSets(blockWidth, blockLength)
						.stream()
						.map(bs -> bs.computeBuildingArrangement(blockWidth, arr -> computeScoreForFrontOnly(arr, front, arr.getMaxLength()), rand))
						.filter(e -> e != null)
						.toList(), e -> e.getDoubleA(), rand, 8)
				.stream().map(e -> e.getB()).toList();
		if (roadLevel.get(back) < 100) { // front and back
			int width = blockWidth;
			for (var frontArrangement : fronts) {
				int length = blockLength - frontArrangement.getMaxLength() - 1;
				double baseScore = frontArrangement.getBaseScore() * this.getRoadCoef(front); // - this.blockShape.getXSpan() * this.blockShape.getYSpan();
				var arrangement = BuildingLoader.getApplicableBuildingSets(width, length).stream()
						.map(bs -> bs.computeBuildingArrangement(width, arr -> {
								double arrScore = baseScore + arr.getBaseScore() * this.getRoadCoef(back);
								int negTotalOpening = blockLength - arr.getPositiveSideLength() - frontArrangement.getNegativeSideLength();
								int posTotalOpening = blockLength - arr.getNegativeSideLength() - frontArrangement.getPositiveSideLength();
								arrScore -= arr.getPositiveSideDecraction(negTotalOpening) * this.getRoadCoef(back.getClockWise());
								arrScore -= arr.getNegativeSideDecraction(posTotalOpening) * this.getRoadCoef(back.getCounterClockWise());
								arrScore -= frontArrangement.getPositiveSideDecraction(posTotalOpening) * this.getRoadCoef(front.getClockWise());
								arrScore -= frontArrangement.getNegativeSideDecraction(negTotalOpening) * this.getRoadCoef(front.getCounterClockWise());
								arrScore -= (blockLength - frontArrangement.getMaxLength() - arr.getMaxLength())
										* (2*posTotalOpening * this.getRoadCoef(front.getClockWise()) + 2*negTotalOpening * this.getRoadCoef(front.getCounterClockWise()));
								return arrScore;
						}, rand))
						.filter(e -> e != null)
						.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA()));
				if (arrangement.isPresent()) {
					if (bestScore <= arrangement.get().getDoubleA()) {
						bestScore = arrangement.get().getDoubleA();
						this.arrangements.clear();
						this.arrangements.add(new ArrangementPlacement(frontArrangement, this.getCornerPos(front, 0), front));
						this.arrangements.add(new ArrangementPlacement(arrangement.get().getB(), this.getCornerPos(back, 0), back));
					}
				}
			}
		}
		if (roadLevel.get(side1) < 100) { // front and side
			for (var frontArrangement : fronts) {
				int width = blockLength - frontArrangement.getMaxLength() - 1;
				int length = blockWidth;
				double baseScore = frontArrangement.getBaseScore() * this.getRoadCoef(front); // - this.blockShape.getXSpan() * this.blockShape.getYSpan();
				var arrangement = BuildingLoader.getApplicableBuildingSets(width, length).stream()
						.map(bs -> bs.computeBuildingArrangement(width, arr -> {
							double arrScore = baseScore + arr.getBaseScore() * this.getRoadCoef(side1);
							arrScore -= frontArrangement.getPositiveSideDecraction(frontArrangement.getMaxLength() - frontArrangement.getPositiveSideLength() + 1) * this.getRoadCoef(front.getClockWise());
							arrScore -= frontArrangement.getNegativeSideDecraction(frontArrangement.getMaxLength() - frontArrangement.getNegativeSideLength() + 1) * this.getRoadCoef(front.getCounterClockWise());
							arrScore -= side1 == front.getCounterClockWise() ?
									arr.getNegativeSideDecraction(length - arr.getNegativeSideLength()) * this.getRoadCoef(back):
									arr.getPositiveSideDecraction(length - arr.getPositiveSideLength()) * this.getRoadCoef(back);
							arrScore -= 2 * (side1 == front.getCounterClockWise() ?
									length - arr.getNegativeSideLength():
									length - arr.getPositiveSideLength()) * (blockWidth - arr.getMaxLength()) * this.getRoadCoef(back);
							return arrScore;
						}, rand))
						.filter(e -> e != null)
						.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA()));
				if (arrangement.isPresent()) {
					if (bestScore <= arrangement.get().getDoubleA()) {
						bestScore = arrangement.get().getDoubleA();
						this.arrangements.clear();
						this.arrangements.add(new ArrangementPlacement(frontArrangement, this.getCornerPos(front, 0), front));
						this.arrangements.add(new ArrangementPlacement(arrangement.get().getB(),
								this.getCornerPos(side1, side1 == front.getCounterClockWise() ? 0 : frontArrangement.getMaxLength()+1), side1));
					}
				}
			}
		}
		// Tuple<front, side>
		List<Tuple<BuildingArrangement, BuildingArrangement>> frontSideTuple = Collections.emptyList();
		if (roadLevel.get(side1) < 100) {
			frontSideTuple = fronts.stream()
					.flatMap(frontArr -> {
						final int width = blockLength - frontArr.getMaxLength() - 1;
						final int length = blockWidth;
						final double baseScore = frontArr.getBaseScore() * this.getRoadCoef(front); // - this.blockShape.getXSpan() * this.blockShape.getYSpan();
						var arrangement = GcUtil.selectWeightedMultipleRandom(
								BuildingLoader.getApplicableBuildingSets(width, length).stream()
								.map(bs -> bs.computeBuildingArrangement(width, arr -> {
									double arrScore = baseScore + arr.getBaseScore() * this.getRoadCoef(side1);
									arrScore -= frontArr.getNegativeSideDecraction(frontArr.getMaxLength() - frontArr.getNegativeSideLength() + 1) * this.getRoadCoef(front.getCounterClockWise());
									arrScore -= frontArr.getPositiveSideDecraction(frontArr.getMaxLength() - frontArr.getPositiveSideLength() + 1) * this.getRoadCoef(front.getClockWise());
									arrScore -= side1 == front.getCounterClockWise() ?
											arr.getNegativeSideDecraction(arr.getMaxLength() - arr.getNegativeSideLength()) * this.getRoadCoef(side1.getCounterClockWise()) :
												arr.getPositiveSideDecraction(arr.getMaxLength() - arr.getPositiveSideLength()) * this.getRoadCoef(side1.getClockWise());
									return arrScore;
								}, rand))
								.filter(e -> e != null)
								.toList(), e -> e.getDoubleA(), rand, 8);
						return arrangement.stream().map(arr -> new Tuple<>(frontArr, arr.getB()));
					}).toList();
		}
		if (frontSideTuple.size() > 0 && this.roadLevel.get(back) < 100) {	// front side back
			for (var frontSideArrangement : frontSideTuple) {
				var frontArr = frontSideArrangement.getA();
				var sideArr = frontSideArrangement.getB();
				int width = blockWidth - sideArr.getMaxLength() - 1;
				int length = blockLength - frontArr.getMaxLength() - 1;
				boolean isSideRight = side1 == front.getCounterClockWise();
				double baseScore =
						frontArr.getBaseScore() * this.getRoadCoef(front)
						+ sideArr.getBaseScore() * this.getRoadCoef(side1)
						- (isSideRight ?
								frontArr.getNegativeSideDecraction(frontArr.getMaxLength() - frontArr.getNegativeSideLength() + 1) * this.getRoadCoef(front.getCounterClockWise()):
								frontArr.getPositiveSideDecraction(frontArr.getMaxLength() - frontArr.getPositiveSideLength() + 1) * this.getRoadCoef(front.getClockWise()))
						- (isSideRight ?
								sideArr.getNegativeSideDecraction(sideArr.getMaxLength() - sideArr.getNegativeSideLength() + 1, blockLength - frontArr.getMaxLength()):
								sideArr.getPositiveSideDecraction(sideArr.getMaxLength() - sideArr.getPositiveSideLength() + 1, blockLength - frontArr.getMaxLength())) * this.getRoadCoef(back);
				var arrangement = BuildingLoader.getApplicableBuildingSets(width, length).stream()
						.map(bs -> bs.computeBuildingArrangement(width, arr -> {
							double arrScore = baseScore + arr.getBaseScore() * this.getRoadCoef(back);
							int opening = isSideRight ?
									blockLength - frontArr.getPositiveSideLength() - arr.getNegativeSideLength() :
									blockLength - frontArr.getNegativeSideLength() - arr.getPositiveSideLength();
							arrScore -= (isSideRight ?
									arr.getNegativeSideDecraction(opening):
									arr.getPositiveSideDecraction(opening)) * this.getRoadCoef(side2);
							arrScore -= (isSideRight ?
									frontArr.getPositiveSideDecraction(opening):
									frontArr.getNegativeSideDecraction(opening)) * this.getRoadCoef(side2);
							arrScore -= 2 * opening * (blockLength - frontArr.getMaxLength() - arr.getMaxLength()) * this.getRoadCoef(side2);
							return arrScore;
						}, rand))
						.filter(e -> e != null)
						.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA()));
				if (arrangement.isPresent()) {
					if (bestScore <= arrangement.get().getDoubleA()) {
						bestScore = arrangement.get().getDoubleA();
						this.arrangements.clear();
						this.arrangements.add(new ArrangementPlacement(frontArr, this.getCornerPos(front, 0), front));
						this.arrangements.add(new ArrangementPlacement(sideArr,
								this.getCornerPos(side1, isSideRight ? 0 : frontArr.getMaxLength()+1), side1));
						this.arrangements.add(new ArrangementPlacement(arrangement.get().getB(),
								this.getCornerPos(back, isSideRight ? 0 : sideArr.getMaxLength()+1), back));
					}
				}
			}
		}
		if (frontSideTuple.size() > 0 && this.roadLevel.get(side2) < 100) {	// front side side
			for (var frontSideArrangement : frontSideTuple) {
				var frontArr = frontSideArrangement.getA();
				var sideArr = frontSideArrangement.getB();
				int width = blockLength - frontArr.getMaxLength() - 1;
				int length = blockWidth - sideArr.getMaxLength() - 1;
				boolean isSideRight = side1 == front.getCounterClockWise();
				double baseScore =
						frontArr.getBaseScore() * this.getRoadCoef(front)
						+ sideArr.getBaseScore() * this.getRoadCoef(side1)
						- frontArr.getNegativeSideDecraction(frontArr.getMaxLength() - frontArr.getNegativeSideLength() + 1) * this.getRoadCoef(front.getCounterClockWise())
						- frontArr.getPositiveSideDecraction(frontArr.getMaxLength() - frontArr.getPositiveSideLength() + 1) * this.getRoadCoef(front.getClockWise());
				int depthLimit = (blockLength - frontArr.getMaxLength())*2;
				var arrangement = BuildingLoader.getApplicableBuildingSets(width, length).stream()
						.map(bs -> bs.computeBuildingArrangement(width, arr -> {
							double arrScore = baseScore + arr.getBaseScore() * this.getRoadCoef(side2);
							int opening = isSideRight ?
									blockWidth - sideArr.getNegativeSideLength() - arr.getPositiveSideLength():
									blockWidth - sideArr.getPositiveSideLength() - arr.getNegativeSideLength();
							arrScore -= (isSideRight ?
									sideArr.getNegativeSideDecraction(opening, depthLimit) + arr.getPositiveSideDecraction(opening, depthLimit):
									sideArr.getPositiveSideDecraction(opening, depthLimit) + arr.getNegativeSideDecraction(opening, depthLimit)) * this.getRoadCoef(back) * 2;
							arrScore -= 2 * Math.min(opening, depthLimit) * (blockWidth - sideArr.getMaxLength() - arr.getMaxLength()) * this.getRoadCoef(back);
							return arrScore;
						}, rand))
						.filter(e -> e != null)
						.max((e1, e2) -> Double.compare(e1.getDoubleA(), e2.getDoubleA()));
				if (arrangement.isPresent()) {
					if (bestScore <= arrangement.get().getDoubleA()) {
						bestScore = arrangement.get().getDoubleA();
						this.arrangements.clear();
						this.arrangements.add(new ArrangementPlacement(frontArr, this.getCornerPos(front, 0), front));
						this.arrangements.add(new ArrangementPlacement(sideArr,
								this.getCornerPos(side1, isSideRight ? 0 : frontArr.getMaxLength()+1), side1));
						this.arrangements.add(new ArrangementPlacement(arrangement.get().getB(),
								this.getCornerPos(side2, isSideRight ? frontArr.getMaxLength()+1 : 0), side2));
					}
				}
			}
		}
		// front side side back
		return bestScore;
	}
	
	private double computeScoreForFrontOnly(BuildingArrangement frontArr, Direction front, int baseLength) {
		double score = frontArr.getBaseScore() * this.getRoadCoef(front) - this.blockShape.getXSpan() * this.blockShape.getYSpan();
		score -= frontArr.getNegativeSideDecraction(baseLength - frontArr.getNegativeSideLength()) * this.getRoadCoef(front.getCounterClockWise());
		score -= frontArr.getPositiveSideDecraction(baseLength - frontArr.getPositiveSideLength()) * this.getRoadCoef(front.getClockWise());
		return score;
	}
	
	private int getRoadCoef(Direction dir) {
		return CityConst.getRoadWidthForLevel(this.roadLevel.get(dir));
	}
	
	/**
	 * Returns origin for BuildingArrangement with the given direction.
	 * Parameter {@code offset} will move Vec2i in positive direction of BuildingArrangement.
	 * @param dir direction of BuildingArrangement
	 * @param offset distance from a corner of this CityBlock.
	 * @return
	 */
	private Vec2i getCornerPos(Direction dir, int offset) {
		return switch (dir) {
		case EAST -> this.blockShape.getNorthEast().relative(dir.getClockWise(), offset);
		case NORTH -> this.blockShape.getNorthWest().relative(dir.getClockWise(), offset);
		case SOUTH -> this.blockShape.getSouthEast().relative(dir.getClockWise(), offset);
		case WEST -> this.blockShape.getSouthWest().relative(dir.getClockWise(), offset);
		default -> null;
		};
	}
	
	private int getAreaWidthForDir(Direction dir) {
		return dir.getAxis() == Axis.X ? this.blockShape.getYSpan() : this.blockShape.getXSpan();
	}
	
	private int getAreaLengthForDir(Direction dir) {
		return dir.getAxis() == Axis.X ? this.blockShape.getXSpan() : this.blockShape.getYSpan();
	}
	
	// ==== * ==== posit init ==== * ====
	
	public CityGenResult postInit(ForkJoinPool fjp) {
		
		if (this.division != null) {
			ForkJoinTask<CityGenResult> task1 = fjp.submit(() -> this.division.child1 != null ? this.division.child1.postInit(fjp) : CityGenResult.EMPTY);
			ForkJoinTask<CityGenResult> task2 = fjp.submit(() -> this.division.child2 != null ? this.division.child2.postInit(fjp) : CityGenResult.EMPTY);
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
			this(child1, child2,
					fjp.submit(() -> child1 != null ? child1.init(fjp) : 0.0d),
					fjp.submit(() -> child2 != null ? child2.init(fjp) : 0.0d), divDir, roadPos);
		}
		
		public double waitAndGetScore() {
			return this.task1.join() + this.task2.join();
		}
	}
	
	private static record ArrangementPlacement(BuildingArrangement arrangement, Vec2i relativePos, Direction front) {
		
	}
}
