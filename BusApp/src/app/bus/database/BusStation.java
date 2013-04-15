package app.bus.database;

public class BusStation {
	private String stationName;
	private String longitude;
	private String latitude;
	private int stationNum;
	private String BusLine;
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
	public int getStationNum() {
		return stationNum;
	}
	public void setStationNum(int stationNum) {
		this.stationNum = stationNum;
	}
	public String getBusLine() {
		return BusLine;
	}
	public void setBusLine(String busLine) {
		BusLine = busLine;
	}
	public BusStation() {
		super();
	}
	

}
