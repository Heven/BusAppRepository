package app.bus.database;

import java.util.ArrayList;

public class BusLine {
	private String busLineName;
	private Integer busLineID;
	private String busLineTime;
	private String busLineCompany;
	private String busLineNote;
	private String busLineMoney;
	private ArrayList<BusStation> stationList;
	public String getBusLineName() {
		return busLineName;
	}
	public ArrayList<BusStation> getStationList() {
		return stationList;
	}
	public void setStationList(ArrayList<BusStation> stationList) {
		this.stationList = stationList;
	}
	public void setBusLineName(String busLineName) {
		this.busLineName = busLineName;
	}
	public Integer getBusLineID() {
		return busLineID;
	}
	public void setBusLineID(Integer busLineID) {
		this.busLineID = busLineID;
	}
	public String getBusLineTime() {
		return busLineTime;
	}
	public void setBusLineTime(String busLineTime) {
		this.busLineTime = busLineTime;
	}
	public String getBusLineCompany() {
		return busLineCompany;
	}
	public void setBusLineCompany(String busLineCompany) {
		this.busLineCompany = busLineCompany;
	}
	public String getBusLineNote() {
		return busLineNote;
	}
	public void setBusLineNote(String busLineNote) {
		this.busLineNote = busLineNote;
	}
	public String getBusLineMoney() {
		return busLineMoney;
	}
	public void setBusLineMoney(String busLineMoney) {
		this.busLineMoney = busLineMoney;
	}
	public BusLine() {
		super();
		this.busLineID=0;
		this.stationList = new ArrayList<BusStation>();
	}
	

}
