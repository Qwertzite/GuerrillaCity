package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import qwertzite.guerrillacity.core.util.GcUtil;
import qwertzite.guerrillacity.core.util.math.IntObjTuple;
import qwertzite.guerrillacity.core.util.math.Vec2i;

/**
 * 建物同士の間隔や並び順を含めたもの
 * @author Qwertzite
 * @date 2022/10/16
 */
public class BuildingArrangement {
	private final List<IntObjTuple<BuildingType>> posList;
	
	private final int maxLength;
	private final double weightSum;
	private final int baseDecraction; // 建物の隙間とか
	
	private final List<Vec2i> posveSideDecPos;
	private final List<Vec2i> negveSideDecPos;
	
	public BuildingArrangement(BuildingSet buildingSet, int[] margins) {
		List<BuildingType> types = buildingSet.getBuildings();
		int typeCnt = types.size();
		
		int[] negvePos = new int[typeCnt];
		int[] posvePos = new int[typeCnt];
		{
			int pos = 0;
			negvePos[0] = 0;
			pos += types.get(0).getWidth();
			posvePos[0] = pos;
			
			for (int i = 1; i < typeCnt; i++) {
				pos += margins[i-1];
				negvePos[i] = pos;
				pos += types.get(i).getWidth();
				posvePos[i] = pos;
			}
		}
		
		var posList = this.posList = new ArrayList<>(typeCnt);
		for (int i = 0; i < typeCnt; i++) {
			posList.add(new IntObjTuple<BuildingType>(negvePos[i], types.get(i)));
		}
		
		{
			var list = this.negveSideDecPos = new LinkedList<>();
			int len = 0;
			for (int i = 0; i < posvePos.length; i++) {
				int length = types.get(i).getLength();
				if (length > len) {
					list.add(new Vec2i(negvePos[i], length));
					len = length;
				}
			}
			list.add(new Vec2i(posvePos[posvePos.length-1], len));
		}
		{
			var list = this.posveSideDecPos = new LinkedList<>();
			int len = 0;
			int maxPos = posvePos[posvePos.length-1];
			for (int i = posvePos.length-1; i >= 0; i--) {
				int length = types.get(i).getLength();
				if (length > len) {
					list.add(new Vec2i(maxPos - posvePos[i], length));
					len = length;
				}
			}
			list.add(new Vec2i(maxPos - negvePos[0], len));
		}
		
		// ==== init front margin decraction ====
		int decraction = 0;
		for (int i = 0; i < margins.length; i++) {
			decraction += margins[i] * margins[i];
		}
		this.baseDecraction = decraction;
		
		// ==== init weight and length ====
		int maxLength = 0;
		double weight = 0;
		Object2IntMap<BuildingType> duplicate = new Object2IntOpenHashMap<>();
		for (BuildingType type : types) {
			int div = duplicate.computeInt(type, (t, i) -> i == null ? 0 : i+1);
			weight += type.getWeight() * GcUtil.pow(0.5, div);
			if (maxLength < type.getLength()) maxLength = type.getLength();
		}
		this.weightSum = weight;
		this.maxLength = maxLength;
	}
	
	public double getBaseScore() {
		return this.weightSum - this.baseDecraction;
	}
	
	public int getNegativeSideDecraction(int depth) {
		int sum = 0;
		int prevLength = 0;
		for (var pos : negveSideDecPos) {
			if (depth <= pos.getX()) break;
			sum += (pos.getY() - prevLength) * (depth - pos.getX());
		}
		return depth * this.getMaxLength() - sum;
	}
	
	public int getPositiveveSideDecraction(int depth) {
		int sum = 0;
		int prevLength = 0;
		for (var pos : posveSideDecPos) {
			if (depth <= pos.getX()) break;
			sum += (pos.getY() - prevLength) * (depth - pos.getX());
		}
		return depth * this.getMaxLength() - sum;
	}
	
	public int getNegativeSideOpening() {
		return this.maxLength - negveSideDecPos.get(0).getY();
	}
	
	public int getPositiveSideOpening() {
		return this.maxLength - posveSideDecPos.get(0).getY();
	}
	
	public List<IntObjTuple<BuildingType>> getPositions() {
		return this.posList;
	}
	
	public int getMaxLength() { return this.maxLength; }
}
