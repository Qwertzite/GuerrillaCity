package qwertzite.guerrillacity.worldgen.city;

import java.util.HashSet;
import java.util.Set;

public class CityGenResult {
	public static final CityGenResult EMPTY = new EmptyCityGenResult();
	
	private Set<BuildingEntry> buildings = new HashSet<>();
	private double score = 0.0d;
	
	public CityGenResult() {}
	
	public static CityGenResult integrate(CityGenResult result1, CityGenResult result2) {
		CityGenResult result = new CityGenResult();
		result.addBuildings(result1.getBuildings());
		result.addBuildings(result2.getBuildings());
		result.addScore(result1.getScore());
		result.addScore(result2.getScore());
		return result;
	}
	
	public void addBuilding(BuildingEntry building) { this.buildings.add(building); }
	public void addBuildings(Set<BuildingEntry> buildings) { this.buildings.addAll(buildings); }
	public Set<BuildingEntry> getBuildings() { return this.buildings; }
	
	public double setScore(double score) { return this.score = score; }
	public double addScore(double score) { return this.score += score; }
	public double getScore() { return this.score; }
	
	private static class EmptyCityGenResult extends CityGenResult {
		@Override public void addBuilding(BuildingEntry building) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addBuildings(Set<BuildingEntry> buildings) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
	}
}
