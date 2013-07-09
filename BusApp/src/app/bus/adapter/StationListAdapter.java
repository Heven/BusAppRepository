package app.bus.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.bus.activity.R;
import app.bus.database.BusStation;

public class StationListAdapter extends BaseAdapter{
	
	Context context;
	ArrayList<BusStation> stationList;
	
	

	public StationListAdapter(Context context,
			ArrayList<BusStation> stationList) {
		super();
		this.context = context;
		this.stationList = stationList;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return stationList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return stationList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.station_list_item, null);
		BusStation temp = stationList.get(position);
		TextView name = (TextView) convertView.findViewById(R.id.stationname);
		name.setText(temp.getStationName());
		
		return convertView;
	}

}
