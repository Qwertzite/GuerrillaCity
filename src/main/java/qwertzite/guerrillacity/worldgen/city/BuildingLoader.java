package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.McUtil;
import qwertzite.guerrillacity.core.util.math.DoubleObjTuple;
import qwertzite.guerrillacity.worldgen.city.BuildingType.MarginSettings;

public class BuildingLoader {
	
	private static Map<String, BuildingType> buildings = new HashMap<>();
	private static Map<String, CityElement> components = new HashMap<String, CityElement>();
	
	/**
	 * 幅ごとのマップ
	 * 取りうる最大のウェイトが小さい順に並んでいる
	 * また，各Entry はそこより後のBuildingSetの中で最も小さな奥行きを持つ
	 */
	private static Int2ObjectMap<List<BuildingSet>> buildingSets = new Int2ObjectOpenHashMap<>();
	
	public static void loadResources() {
		buildings.clear();
		components.clear();
		buildingSets.clear();

		loadBuildingFiles();
		
		generateBuildingSets();
		
		ModLog.info("Loaded buildings and components.");
	}
	
	private static void loadBuildingFiles() {
		Random rand = new Random(0);
		int minSize = Integer.MAX_VALUE;
		
		// XXX: load from actual resources, internal and external. see remiliaMarine.guerrillaCity.world.external.ExternalBuilding
		MarginSettings margin = new MarginSettings(1, 3, 1, 3);
		for (int i = 0; i < 12; i++) {
			int w = rand.nextInt(10, 15) + rand.nextInt(0, 15);
			int l = rand.nextInt(10, 15) + rand.nextInt(0, 15);
			String name = "tb_" + i;
			buildings.put(name, new BuildingType(name, w, l, margin, 10, McUtil.getColouredWool(i)));
			if (minSize > w) minSize = w;
			if (minSize > l) minSize = l;
		}
		
		CityConst.MIN_BUILDING_SIZE = minSize;
	}
	
	private static void generateBuildingSets() {
		List<BuildingType> loadedTypes = new ArrayList<>(buildings.values());
		
		List<BuildingType> types = new LinkedList<>();
		int typeNum = buildings.size();
		recursiveBuildingSet(0, types, loadedTypes, typeNum);
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
			new BuildingSet(types, buildingSets);
			if (index < 3) recursiveBuildingSet(index + 1, types, loadedTypes, typeNum);
			types.remove(types.size() - 1);
		}
	}
	
	/**
	 * Returns a list of BuildingSet which has the same width and less length than given length.
	 * @param width
	 * @param maxLength
	 * @return
	 */
	public static List<BuildingSet> getApplicableBuildginSets(int width, int maxLength) {
		return buildingSets.getOrDefault(width, Collections.emptyList()).stream().filter(bs -> bs.getBuildingSetLength() <= maxLength).toList();
	}
	
//	@Nullable
//	public static List<BuildingArrangement> getRandomArrangement(int width, int length, Random rand, int count) {
//		
//		if (!buildingSets.containsKey(width)) return null; // When no BuildingSet with the given width exists.
//		List<DoubleObjTuple<BuildingSet>> possibleBuildingSets = new ArrayList<>();
//		
//		for (var bs : buildingSets.get(width)) {
//			double weight = bs.getMaxWeight(width);
//			if (weight > 0) {
//				possibleBuildingSets.add(new DoubleObjTuple<BuildingSet>(weight, bs));
//			}
//		}
//		if (possibleBuildingSets.size() == 0) return null;
//		
//		List<DoubleObjTuple<BuildingSet>> buildingSet = GcUtil.selectWeightedMultipleRandom(possibleBuildingSets, e -> e.getDoubleA(), rand, count);
//		return buildingSet.stream().map(e -> e.getB().selectArrangement(width, length, rand)).toList();
//	}
//	
//	public static BuildingArrangement getBestArrangement(int width, int length, Random rand) {
//		
//		if (!buildingSets.containsKey(width)) return null; // When no BuildingSet with the given width exists.
//		List<DoubleObjTuple<BuildingSet>> possibleBuildingSets = new ArrayList<>();
//		
//		for (var bs : buildingSets.get(width)) {
//			double weight = bs.getWeightForLength(width, length);
//			if (weight > 0) {
//				possibleBuildingSets.add(new DoubleObjTuple<BuildingSet>(weight, bs));
//			}
//		}
//		if (possibleBuildingSets.size() == 0) return null;
//		
//		BuildingSet buildingSet = GcUtil.selectWeightedRandom(possibleBuildingSets, e -> e.getDoubleA(), rand).getB();
//		return buildingSet.selectArrangement(width, length, rand);
//	}
}