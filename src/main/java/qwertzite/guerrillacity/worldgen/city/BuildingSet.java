package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.VariableDigitIterator;
import qwertzite.guerrillacity.worldgen.city.BuildingType.MarginSettings;

public class BuildingSet {
	private final List<BuildingType> buildingTypes;
	private final int length; // the largest length of member building type.
	
	private final Int2ObjectMap<List<BuildingArrangement>> possibleMargins;
	
	public BuildingSet(List<BuildingType> buildingTypes) {
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
//		StringBuilder sb = new StringBuilder();
//		sb.append("margin min:");
//		for (int i : minMargin) { sb.append(String.format(" %d", i)); }
//		sb.append("\n");
//		sb.append("margin max:");
//		for (int i : maxMargin) { sb.append(String.format(" %d", i)); }
//		System.out.println(sb.toString());
		
		for (Iterator<int[]> iter = new VariableDigitIterator(minMargin, maxMargin); iter.hasNext();) {
			int[] margin = iter.next();
			int finalWidth = buildingWidthSum + GcUtil.sumArray(margin);
			this.possibleMargins.computeIfAbsent(finalWidth, i -> new ArrayList<>()).add(new BuildingArrangement(this, margin));
		}
	}
	
	public IntSet getPossibleWidths() {
		// TODO:
//		return IntSets.singleton(this.buildingTypes.get(0).getWidth());
		return possibleMargins.keySet();
	}
	
	/**
	 * no need to cache because this method is called only on reloading.
	 * @param width
	 * @return
	 */
	public double getMaxWeight(int width) {
		// TODO:
//		return this.buildingTypes.get(0).getWidth() == width ? 10.0d : 0;
		return this.getWeightForLength(width, this.getMinLength());
	}

	public int getMinLength() {
		return length;
	}
	
	public double getWeightForLength(int width, int length) {
		if (length < this.length) return 0.0d;
		// TODO:
		return this.buildingTypes.get(0).getWidth() == width ? 10.0d * this.buildingTypes.size() : 0.0d;
//		return this.possibleMargins.get(width).stream().mapToDouble(e -> e.getScore(length)).max().getAsDouble();
	}
	
	public BuildingArrangement selectArrangement(int width, int length, Random rand) {
//		return new BuildingArrangement(this, new int[] {0});
		
//		List<BuildingArrangement> arrangements = possibleMargins.get(width);
//		double sum = 0.0d;
//		DoubleList list = new DoubleArrayList();
//		for (BuildingArrangement arrangement : arrangements) {
//			double w = arrangement.getScore(length);
//			sum += w;
//			list.add(sum);
//		}
//		double thresh = rand.nextDouble(sum);
//		int index = GcUtil.binarySearch(0, list.size()-1, i -> list.getDouble(i) > thresh);
//		return arrangements.get(index); // TODO:
		
		var arrangements = possibleMargins.get(width);
		var list = new ArrayList<BuildingArrangement>();
		for (BuildingArrangement arr : arrangements) {
			if (arr.getScore(length) > 0) list.add(arr);
		}
		return arrangements.get(rand.nextInt(list.size()));
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