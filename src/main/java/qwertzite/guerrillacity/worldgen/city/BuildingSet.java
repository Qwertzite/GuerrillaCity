package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import qwertzite.guerrillacity.core.util.DoubleObjTuple;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.VariableDigitIterator;
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
	 * no need to cache because this method is called only on reloading.
	 * @param width
	 * @return
	 */
	public double getMaxWeight(int width) {
		return this.getWeightForLength(width, this.getBuildingSetLength());
	}

	public int getBuildingSetLength() {
		return length;
	}
	
	public double getWeightForLength(int width, int length) {
		if (length < this.length) return 0.0d;
		return this.possibleMargins.get(width).stream().mapToDouble(e -> e.getScore(length)).max().getAsDouble(); // OPTIMISE: cache
	}
	
	public BuildingArrangement selectArrangement(int width, int length, Random rand) {
		
		List<DoubleObjTuple<BuildingArrangement>> list = new ArrayList<>();
		for (var arrangements : possibleMargins.get(width)) {
			double weight = arrangements.getScore(length);
			if (weight > 0) {
				list.add(new DoubleObjTuple<>(weight, arrangements));
			}
		}
		if (list.size() == 0) return null;
		
		var arrangement = GcUtil.selectWeightedRandom(list, e -> e.getDoubleA(), rand).getB();
		return arrangement;
	}
	
	public List<BuildingType> getBuildings() {
		return this.buildingTypes;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (var bt : this.buildingTypes) {
			sb.append(bt.getTypeName() + " ");
		}
		sb.append("\n");
		for (var arr : this.possibleMargins.int2ObjectEntrySet()) {
			sb.append("  width=" + arr.getIntKey() + "\n   ");
			for (var a : arr.getValue()) {
				sb.append(" " + a.getScore(20));
			}
		}
		return sb.toString();
	}
}