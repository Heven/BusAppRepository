package app.bus.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.TextView;
import app.bus.adapter.StationAdapter;
import app.bus.database.DatabaseHelper;
import app.bus.database.Station;

public class BusStationActivity extends Activity {
	ExpandableListView expandableListView;
	private TextView busStationResult;
	private EditText busStationName;
	private Button busStationSearch;

	StationAdapter treeViewAdapter;
	public void onCreate(Bundle savedInstanceState)
	{
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
			String result="";
			int i = temp.size();
		   treeViewAdapter = new StationAdapter(temp,BusStationActivity.this,
					20);
			expandableListView = (ExpandableListView)BusStationActivity. this
			.findViewById(R.id.expandableListView);
			List<StationAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
			for (int j = 0; j < i; j++)
			{
				StationAdapter.TreeNode node = new StationAdapter.TreeNode();
				node.setParent(temp.get(j).getStationName());
					node.getChilds().add("");
				treeNode.add(node);
			}
			treeViewAdapter.UpdateTreeNode(treeNode);
			expandableListView.setAdapter(treeViewAdapter);
			
		}} );
		
	}

	class MyGridView extends GridView
	{
		public MyGridView(android.content.Context context,
				android.util.AttributeSet attrs)
		{
			super(context, attrs);
		}

		/**
		 * ÉèÖÃ²»¹ö¶¯
		 */
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
			MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}}
