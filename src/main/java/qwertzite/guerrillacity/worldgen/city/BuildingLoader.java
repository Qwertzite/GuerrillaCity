package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.util.DoubleObjTuple;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.McUtil;
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
	
	private static void loadBuildingFiles() {
		Random rand = new Random(0);
		int minSize = Integer.MAX_VALUE;
		
		// XXX: load from actual resources, internal and external. see remiliaMarine.guerrillaCity.world.external.ExternalBuilding
		MarginSettings margin = new MarginSettings(1, 3, 1, 3);
		for (int i = 0; i < 10; i++) {
			int w = rand.nextInt(5, 10) + rand.nextInt(0, 10);
			int l = rand.nextInt(5, 10) + rand.nextInt(0, 10);
			String name = "tb_" + i;
			buildings.put(name, new BuildingType(name, w, l, margin, 10, McUtil.getColouredWool(rand.nextInt(16))));
			if (minSize > w) minSize = w;
			if (minSize > l) minSize = l;
		}
		
		CityConst.MIN_BUILDING_SIZE = minSize;
	}
	
	private static void generateBuildingSets() {
		List<BuildingType> loadedTypes = new ArrayList<>(buildings.values());
		
		loadedTypes.sort((e1, e2) -> e1.getTypeName().compareTo(e2.getTypeName())); // DEBUG remove
		
		List<BuildingType> types = new LinkedList<>();
		int typeNum = buildings.size();
		recursiveBuildingSet(0, types, loadedTypes, typeNum);
		
//		for (Entry<List<BuildingSet>> entry : buildingSets.int2ObjectEntrySet()) {
//			int width = entry.getIntKey();
//			List<BuildingSet> list = entry.getValue();
//			list.sort((e1, e2) -> Double.compare(e1.getMaxWeight(width), e2.getMaxWeight(width)));
//			int minLength = Integer.MAX_VALUE;
//			for (int i = list.size() - 1; i >= 0; i--) {
//				IntObjTuple<BuildingSet> tuple = list.get(i);
//				minLength = Math.min(minLength, tuple.getB().getMinLength());
//				tuple.setA(minLength);
//			}
//		}
		System.out.print("width distrib "); // DEBUG
		var listlist = new ArrayList<>(buildingSets.int2ObjectEntrySet());
		listlist.sort((e1, e2) -> Integer.compare(e1.getIntKey(), e2.getIntKey()));
		for (var list : listlist) {
			System.out.print(list.getIntKey() + ":" + list.getValue().size() + " ");
		}
		System.out.println();
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
			if (index < 4) recursiveBuildingSet(index + 1, types, loadedTypes, typeNum);
			types.remove(types.size() - 1);
		}
		
	}
	
	@Nonnull
	public static BuildingArrangement getBuildingSet(int width, int length, Random rand) {
		
		if (!buildingSets.containsKey(width)) return null; // When no BuildingSet with the given width exists.
		List<DoubleObjTuple<BuildingSet>> possibleBuildingSets = new ArrayList<>();
		
		for (var bd : buildingSets.get(width)) {
			double weight = bd.getWeightForLength(width, length);
			if (weight > 0) {
				possibleBuildingSets.add(new DoubleObjTuple<BuildingSet>(weight, bd));
			}
		}
		if (possibleBuildingSets.size() == 0) return null;
		
		BuildingSet buildingSet = GcUtil.selectWeightedRandom(possibleBuildingSets, e -> e.getDoubleA(), rand).getB();
		return buildingSet.selectArrangement(width, length, rand);
	}
}