package app.bus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import app.bus.activity.R;
import app.bus.database.BusStation;
import app.bus.database.Station;

public class StationAdapter  extends BaseAdapter{
		
		Context context;
		ArrayList<Station> stationList;
	

		public StationAdapter(ArrayList<Station> stationList,Context context) {
			super();
			this.context = context;
			this.stationList = stationList;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return stationList.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return stationList.get(arg0);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String result = "";
			convertView = LayoutInflater.from(context).inflate(R.layout.station_info_item, null);
			Station temp = stationList.get(position);
			TextView list = (TextView) convertView.findViewById(R.id.station_list);
			for(int i=0;i<temp.getBusLine().size();i++){
				result = result+temp.getBusLine().get(i).toString();
			}
			list.setText(result);
			  Log.e("aaaaaaaaaaaa", "777777777777777");
			TextView station = (TextView) convertView.findViewById(R.id.station_name);
			Log.e("aaaaaaaaaaaa", "88888888888");
			station.setText(temp.getStationName());
			return convertView;
		}

	}

