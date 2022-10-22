package qwertzite.guerrillacity.worldgen.city;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import qwertzite.guerrillacity.core.util.IntObjTuple;
import qwertzite.guerrillacity.core.util.Vec2i;

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
			int len = types.get(0).getLength();
			for (int i = 1; i < posvePos.length; i++) {
				int length = types.get(i).getLength();
				if (length > len) {
					list.add(new Vec2i(negvePos[i], len));
					len = length;
				}
			}
			list.add(new Vec2i(posvePos[posvePos.length-1], len));
		}
		{
			var list = this.posveSideDecPos = new LinkedList<>();
			int len = types.get(types.size()-1).getLength();
			int maxPos = posvePos[posvePos.length-1];
			for (int i = posvePos.length-2; i > 0; i--) {
				int length = types.get(i).getLength();
				if (length > len) {
					list.add(new Vec2i(maxPos - posvePos[i], len));
					len = length;
				}
			}
			list.add(new Vec2i(maxPos - negvePos[0], len));
		}
		
		int decraction = 0;
		for (int i = 0; i < margins.length; i++) {
			decraction += margins[i] * margins[i];
		}
		this.baseDecraction = decraction;
		
		int maxLength = 0;
		double weight = 0;
		for (BuildingType type : types) {
			weight += type.getWeight();
			if (maxLength < type.getLength()) maxLength = type.getLength();
		}
		this.weightSum = weight;
		this.maxLength = maxLength;
		
//		StringBuilder sb = new StringBuilder();
//		sb.append("type   - ");
//		for (var e : this.posList) {
//			sb.append(String.format(" %d: %s (%d,%d)", e.getIntA(), e.getB().getTypeName(), e.getB().getWidth(), e.getB().getLength()));
//		}
//		sb.append("\n");
//		sb.append("margin - ");
//		for (int i : margins) {
//			sb.append(String.format(" %d", i));
//		}
//		sb.append("\n");
//		sb.append("posve  - ");
//		for (Vec2i v2i : posveSideDecPos) {
//			sb.append(String.format(" %s", v2i));
//		}
//		sb.append("\n");
//		sb.append("negve  - ");
//		for (Vec2i v2i : negveSideDecPos) {
//			sb.append(String.format(" %s", v2i));
//		}
//		sb.append("\n");
//		System.out.println(sb.toString());
	}
	
	public double getScore(int length) {
		return length >= this.maxLength ? this.posList.size() * 10.0d : 0.0d;
//		BuildingType bt = this.posList.get(0).getB();
//		return length >= bt.getLength() ? bt.getWeight() : 0;
//		int maxDec = 0;
//		for (Vec2i pos : this.negveSideDecPos) {
//			int dec = Math.min(pos.getX(), (length - pos.getY())*2) * (length - pos.getY());
//			if (dec > maxDec) maxDec = dec;
//		}
//		for (Vec2i pos : this.posveSideDecPos) {
//			int dec = Math.min(pos.getX(), (length - pos.getY())*2) * (length - pos.getY());
//			if (dec > maxDec) maxDec = dec;
//		}
//		maxDec += this.baseDecraction;
//		return this.weightSum * Math.pow(7.0d/8, maxDec); TODO
//		return 10.0 * 
	}
	
	public List<IntObjTuple<BuildingType>> getPositions() {
		return this.posList;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}