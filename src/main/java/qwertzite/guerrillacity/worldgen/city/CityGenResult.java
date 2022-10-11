package qwertzite.guerrillacity.worldgen.city;

import java.util.HashSet;
import java.util.Set;

public class CityGenResult {
	public static final CityGenResult EMPTY = new EmptyCityGenResult();
	
	private Set<RoadElement> roadElements = new HashSet<>();
	private int roadCount = 0;
	private Set<CityElement> buildings = new HashSet<>();
	private double score = 0.0d;
	
	public CityGenResult() {}
	
	public static CityGenResult integrate(CityGenResult result1, CityGenResult result2) {
		CityGenResult result = new CityGenResult();
		result.addRoadElements(result1.getRoadElements());
		result.addRoadElements(result2.getRoadElements());
		result.addRoadCount(result1.getRoadCount());
		result.addRoadCount(result2.getRoadCount());
		result.addBuildings(result1.getBuildings());
		result.addBuildings(result2.getBuildings());
		result.addScore(result1.getScore());
		result.addScore(result2.getScore());
		return result;
	}

	public void addRoadElement(RoadElement roadElement) { this.roadElements.add(roadElement); }
	public void addRoadElements(Set<RoadElement> roadElement) { this.roadElements.addAll(roadElement); } // road
	public Set<RoadElement> getRoadElements() { return this.roadElements; }
	
	public void incrementRoadCount() { this.roadCount++;}
	public void addRoadCount(int count) { this.roadCount += count; }
	public int getRoadCount() { return this.roadCount; }
	
	public void addBuilding(CityElement building) { this.buildings.add(building); }
	public void addBuildings(Set<CityElement> buildings) { this.buildings.addAll(buildings); }
	public Set<CityElement> getBuildings() { return this.buildings; }
	
	public void setScore(double score) { this.score = score; }
	public void addScore(double score) { this.score += score; }
	public double getScore() { return this.score; }
	
	private static class EmptyCityGenResult extends CityGenResult {
		@Override public void addRoadElement(RoadElement roadElement) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addRoadElements(Set<RoadElement> roadElement) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void incrementRoadCount() { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addRoadCount(int count) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addBuilding(CityElement building) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addBuildings(Set<CityElement> buildings) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void setScore(double score) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
		@Override public void addScore(double score) { throw new UnsupportedOperationException("EmptyCityGenResult cannot be modified."); }
	}
}
