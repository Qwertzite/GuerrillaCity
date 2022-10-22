package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.DoubleObjTuple;
import qwertzite.guerrillacity.core.util.IntObjTuple;
import qwertzite.guerrillacity.worldgen.city.BuildingType.MarginSettings;

public class BuildingLoader {
	
	private static Map<String, BuildingType> buildings = new HashMap<>();
	private static Map<String, CityElement> components = new HashMap<String, CityElement>();
	
	/**
	 * 幅ごとのマップ
	 * 取りうる最大のウェイトが小さい順に並んでいる
	 * また，各Entry はそこより後のBuildingSetの中で最も小さな奥行きを持つ
	 */
	private static Int2ObjectMap<List<IntObjTuple<BuildingSet>>> buildingSets = new Int2ObjectOpenHashMap<>();
	
	public static void loadResources() {
		buildings.clear();
		components.clear();
		buildingSets.clear();
		
		int minSize = Integer.MAX_VALUE;
		// XXX: load from resources, internal and external. see remiliaMarine.guerrillaCity.world.external.ExternalBuilding
		Random rand = new Random(0);
		MarginSettings margin = new MarginSettings(1, 2, 1, 2);
		for (int i = 0; i < 10; i++) {
			int w = rand.nextInt(5, 10) + rand.nextInt(0, 10);
			int l = rand.nextInt(5, 10) + rand.nextInt(0, 10);
			String name = "tb_" + i;
			buildings.put(name, new BuildingType(name, w, l, margin, 10));
			if (minSize > w) minSize = w;
			if (minSize > l) minSize = l;
		}
		CityConst.MIN_BUILDING_SIZE = minSize;
		
		generateBuildingSets();
		
		ModLog.info("Loaded buildings and components.");
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("min building size = " + minSize + "\n");
//		sb.append("loaded buildings " + buildings.size() + "\n");
//		for (var bt : buildings.values()) {
//			sb.append("  " + bt.getTypeName() + " w=" + bt.getWeight() + " :" + bt.getWidth() + " x " + bt.getLength() + "\n");
//		}
//		
//		for (var e : buildingSets.int2ObjectEntrySet()) {
//			sb.append("" + e.getIntKey() + "\n");
//			for (var ee : e.getValue()) {
//				sb.append("  " + ee.getIntA() + " " + ee.getB() + "\n");
//			}
//		}
//		System.out.println(sb);
	}
	
	private static void generateBuildingSets() {
//		List<BuildingType> loadedTypes = List.copyOf(buildings.values());
		List<BuildingType> loadedTypes = new ArrayList<>(buildings.values());
		loadedTypes.sort((e1, e2) -> e1.getTypeName().compareTo(e2.getTypeName()));
		List<BuildingType> types = new LinkedList<>();
		int typeNum = buildings.size();
//		for (int i0 = 0; i0 < typeNum; i0++) {
//			BuildingType t0 = loadedTypes.get(i0);
//			// XXX: ２週目以降ではここで隣り合うことが出来るかを判定する
//			types.add(t0);
//			BuildingSet bs0 = new BuildingSet(types);
//			IntSet possibleWidths = bs0.getPossibleWidths();
//			
//			for (IntIterator iter = possibleWidths.intIterator(); iter.hasNext();) {
//				int width = iter.nextInt();
//				buildingSets.computeIfAbsent(width, i -> new ArrayList<>()).add(new IntObjTuple<>(Integer.MAX_VALUE, bs0));
//			}
//			types.remove(types.size() - 1);
//		}
		recursiveBuildingSet(0, types, loadedTypes, typeNum);
		
		for (Entry<List<IntObjTuple<BuildingSet>>> entry : buildingSets.int2ObjectEntrySet()) {
			int width = entry.getIntKey();
			List<IntObjTuple<BuildingSet>> list = entry.getValue();
			list.sort((e1, e2) -> Double.compare(e1.getB().getMaxWeight(width), e2.getB().getMaxWeight(width)));
			int minLength = Integer.MAX_VALUE;
			for (int i = list.size() - 1; i >= 0; i--) {
				IntObjTuple<BuildingSet> tuple = list.get(i);
				minLength = Math.min(minLength, tuple.getB().getMinLength());
				tuple.setA(minLength);
			}
		}
	}
	
	private static void recursiveBuildingSet(int index, List<BuildingType> types, final List<BuildingType> loadedTypes, final int typeNum) {
		
		for (int i0 = 0; i0 < typeNum; i0++) {
			BuildingType t0 = loadedTypes.get(i0);
			if (index > 0) {
				BuildingType prev = types.get(types.size() - 1);
				int maxLim = Math.min(prev.getMarginRestriction().posveSideMaxMargin(), t0.getMarginRestriction().negveSideMaxMargin());
				int minLim = Math.max(prev.getMarginRestriction().posveSideMinMargin(), t0.getMarginRestriction().negveSideMinMargin());
				if (maxLim < minLim) continue;
			}
			
			types.add(t0);
			BuildingSet bs0 = new BuildingSet(types);
			IntSet possibleWidths = bs0.getPossibleWidths();
			
			for (IntIterator iter = possibleWidths.intIterator(); iter.hasNext();) {
				int width = iter.nextInt();
				buildingSets.computeIfAbsent(width, i -> new ArrayList<>()).add(new IntObjTuple<>(Integer.MAX_VALUE, bs0));
			}
			if (index < 2) recursiveBuildingSet(index + 1, types, loadedTypes, typeNum);
			types.remove(types.size() - 1);
		}
		
	}
	
	@Nonnull
	public static BuildingArrangement getBuildingSet(int width, int length, Random rand) {
		
		if (!buildingSets.containsKey(width)) return null; // When no BuildingSet with the given width exists.
		List<IntObjTuple<BuildingSet>> possibleBuildingSets = buildingSets.get(width);
		
		// Randomly select a BuildingSet.
		double sum = 0.0d;
		double maxWeight = 0;
		List<DoubleObjTuple<BuildingSet>> sets = new ArrayList<>();
//		int maxIndex = GcUtil.binarySearch(0, possibleBuildingSets.size()-1, i -> possibleBuildingSets.get(i).getIntA() > length);
		int maxIndex = possibleBuildingSets.size();
		//TODO: use weight
//		for (int i = maxIndex-1; i >= 0; i--) {
//			var tuple = possibleBuildingSets.get(i);
//			BuildingSet buildingSet = tuple.getB();
//			if (buildingSet.getMinLength() > length) continue; // If the length from front to back is longer than the given length
//			double weight = buildingSet.getWeightForLength(width, length);
//			if (weight < 0) System.out.println("** WARN ** NEGATIVE WEIGHT " + weight);
//			if (weight > maxWeight) maxWeight = weight;
//			else if (weight < maxWeight * 0.01) continue; // Selection weight is extremely small and the chance of being selected is less than 1%.
//			sum += weight;
//			sets.add(new DoubleObjTuple<>(sum, buildingSet));
//		}
		
//		if (sets.size() <= 0 || sum <= 0) return null; // 全て欲しい奥行きより大きい場合
//		double randThresh = rand.nextDouble(sum);
//		int index = GcUtil.binarySearch(0, sets.size()-1, (int i) -> sets.get(i).getA() > randThresh);
//		BuildingSet ret = sets.get(index).getB();
//		return ret.selectArrangement(width, length, rand);
		
		var list = new LinkedList<BuildingSet>();
		for (var e : possibleBuildingSets) {
			BuildingSet set = e.getB();
			if (set.getWeightForLength(width, length) > 0) list.add(set);
		}
		if (list.size() <= 0) return null;
		return list.get(rand.nextInt(list.size())).selectArrangement(width, length, rand);
	}
}