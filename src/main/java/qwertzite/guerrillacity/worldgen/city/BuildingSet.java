package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.VariableDigitIterator;
import qwertzite.guerrillacity.core.util.math.DoubleObjTuple;
import qwertzite.guerrillacity.worldgen.city.BuildingType.MarginSettings;

public class BuildingSet {
	private final List<BuildingType> buildingTypes;
	private final int length; // the largest length of member building type.
	
	private final Int2ObjectMap<List<BuildingArrangement>> possibleMargins;
	
	public BuildingSet(List<BuildingType> buildingTypes, Int2ObjectMap<List<BuildingSet>> buildingSets) {
		this.buildingTypes = new ArrayList<>(buildingTypes);
		this.possibleMargins = new Int2ObjectOpenHashMap<>();
		
		int length = 0;
		int buildingWidthSum = 0;
		for (BuildingType bt : buildingTypes) {
			if (length < bt.getLength()) length = bt.getLength();
			buildingWidthSum += bt.getWidth();
		}
		this.length = length;
		
		int buildingCount = buildingTypes.size();
		int[] minMargin = new int[buildingCount - 1];
		int[] maxMargin = new int[buildingCount - 1];
		for (int i = 0; i < buildingCount-1; i++) {
			BuildingType bt0 = buildingTypes.get(i);
			BuildingType bt1 = buildingTypes.get(i+1);
			MarginSettings ms0 = bt0.getMarginRestriction();
			MarginSettings ms1 = bt1.getMarginRestriction();
			
			minMargin[i] = Math.max(ms0.posveSideMinMargin(), ms1.negveSideMinMargin());
			maxMargin[i] = Math.min(ms0.posveSideMaxMargin(), ms1.negveSideMaxMargin());
		}
		
		for (Iterator<int[]> iter = new VariableDigitIterator(minMargin, maxMargin); iter.hasNext();) {
			int[] margin = iter.next();
			int finalWidth = buildingWidthSum + GcUtil.sumArray(margin);
			this.possibleMargins.computeIfAbsent(finalWidth, i -> new ArrayList<>()).add(new BuildingArrangement(this, margin));
		}
		
		for (int width : this.possibleMargins.keySet()) {
			buildingSets.computeIfAbsent(width, i -> new ArrayList<>()).add(this);
		}
	}
	
	/**
	 * Returns a weighted randomly chosen array based on the given evaluation function, and the score of this BuildingSet.
	 * @param width the width of the arrangement to be returned.
	 * @param scoreFunc
	 * @param rand
	 * @return
	 */
	public DoubleObjTuple<BuildingArrangement> computeBuildingArrangement(int width, ToDoubleFunction<BuildingArrangement> scoreFunc, Random rand) {
		List<BuildingArrangement> arrangements = this.getApplicableArrangements(width);
		double bestArrScore = Double.MIN_VALUE;
		List<DoubleObjTuple<BuildingArrangement>> weightedArrangements = new ArrayList<>();
		for (BuildingArrangement arr : arrangements) {
			double arrScore = scoreFunc.applyAsDouble(arr);
			if (arrScore > bestArrScore) bestArrScore = arrScore;
			if (arrScore > 0) weightedArrangements.add(new DoubleObjTuple<BuildingArrangement>(arrScore, arr));
		}
		var selectedArr = GcUtil.selectWeightedRandom(weightedArrangements, e -> e.getDoubleA(), rand);
		return new DoubleObjTuple<>(bestArrScore, selectedArr.getB());
	
	}
	
	public int getBuildingSetLength() {
		return length;
	}
	
	public List<BuildingArrangement> getApplicableArrangements(int width) {
		return this.possibleMargins.getOrDefault(width, Collections.emptyList());
	}
	
	public List<BuildingType> getBuildings() {
		return this.buildingTypes;
	}
//	
//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		for (var bt : this.buildingTypes) {
//			sb.append(bt.getTypeName() + " ");
//		}
//		sb.append("\n");
//		for (var arr : this.possibleMargins.int2ObjectEntrySet()) {
//			sb.append("  width=" + arr.getIntKey() + "\n   ");
//			for (var a : arr.getValue()) {
//				sb.append(" " + a.getScore(20));
//			}
//		}
//		return sb.toString();
//	}
}