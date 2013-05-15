package app.bus.activity;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import app.bus.adapter.StationAdapter;
import app.bus.adapter.StationListAdapter;
import app.bus.database.BusStation;
import app.bus.database.DBtool;
import app.bus.database.DatabaseHelper;
import app.bus.database.Station;

public class BusStationActivity extends Activity {
	public DBtool dbtool;
//	private TextView busStationList;
	private TextView busStationResult;
	private EditText busStationName;
	private Button busStationSearch;
	private ListView stationListView;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_station);
		busStationName = (EditText)findViewById(R.id.stationname_search);
		busStationResult = (TextView)findViewById(R.id.stationsearch);
		busStationSearch = (Button)findViewById(R.id.stationsearch);
		busStationSearch.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String busStation = busStationName.getText().toString();
			DatabaseHelper helper = new DatabaseHelper();
			ArrayList<Station> temp = helper.searchBusStation(busStation);
			  Log.e("aaaaaaaaaaaa", "jkkkkkkkkkkk;"+busStation);
			String result="";
			  Log.e("aaaaaaaaaaaa", "222222222222222");
			int i = temp.size();
			  Log.e("aaaaaaaaaaaa", String.valueOf(i));
		//	i--;
			if(i!=0){
			//	result =temp.get(0).getStationName().toString();
                Log.e("aaaaaaaaaaaa", "44444444444444444");
				//if (i == 0)break;
				//i--;
			}
			//busStationResult.setText(result);
			//busStationList.setText("经过"+busStation+"的线路有：");
			  Log.e("aaaaaaaaaaaa", "5555555555555555555");
	
			  Log.e("aaaaaaaaaaaa", "666666666666666666");
			stationListView = (ListView)findViewById(R.id.stationlist);
			StationAdapter StationListAdapter = new StationAdapter(temp,BusStationActivity.this);
			stationListView.setAdapter(StationListAdapter);
			
		}}); 

}}
