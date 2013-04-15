package app.bus.database;

import java.util.ArrayList;

public class Station {
	private String stationName;
	private String longitude;
	private String latitude;
	private ArrayList<String> busLine;
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public ArrayList<String> getBusLine() {
		return busLine;
	}
	public void setBusLine(ArrayList<String> busLine) {
		this.busLine = busLine;
	}
	public void addBusLine(String lineName){
		busLine.add(lineName);
	}
	public Station() {
		super();
		this.busLine = new ArrayList<String>();
	}

}
