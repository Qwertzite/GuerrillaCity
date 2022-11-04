package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.SectionPos;

public class CityConst {
	public static final int WARD_SIZE_BIT = 4;
	public static final int WARD_SIZE_CHUNKS = 1 << WARD_SIZE_BIT;
	public static final int WARD_SIZE_BLOCKS = 1 << (WARD_SIZE_BIT + SectionPos.SECTION_BITS);

	public static int MIN_BUILDING_SIZE = 8;
	
	private static final int[] ROAD_WIDTH = new int[] { 20, 14, 10, 7, 5, 4 }; //  { 20, 16, 14, 12, 10, 8, 7, 6, 5, 4 };
	public static int getRoadWidthForLevel(int level) {
		int count = ROAD_WIDTH.length;
		if (level >= count) level = count - 1;
		return ROAD_WIDTH[level];
	}
}
