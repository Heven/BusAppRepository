package app.bus.activity;





import java.util.ArrayList;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import app.bus.adapter.StationListAdapter;
import app.bus.database.*;


public class LineActivity extends Activity{
	public DBtool dbtool;
    private EditText busLineName ;
    private TextView busLineTime ;
    private TextView busLineResult ;
    private Button busLineSearch,fweiMapBus ;
    private StationListAdapter stationListAdapter;
/*
 * wo shi da wang
 */
    private GridView stationGridView;

    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState){
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.line);
			busLineName = (EditText) findViewById(R.id.linename);
		    busLineTime = (TextView) findViewById(R.id.linetime);
		    busLineSearch = (Button) findViewById(R.id.linesearch);
		    busLineResult = (TextView) findViewById(R.id.lineresult);	
		    busLineSearch.setOnClickListener(search);  
		    //地图显示公交线路
		    fweiMapBus=(Button) findViewById(R.id.fwei_map_bus);
		    fweiMapBus.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent gaodeIntent = new Intent();
					String busLine = busLineName.getText().toString();
					
					//传值
					gaodeIntent.putExtra("busname", busLine);
					gaodeIntent.setClass(LineActivity.this, FweiBusLineSearch.class);
					startActivity(gaodeIntent);	
				}
			});
		    fweiMapBus.setVisibility(8);
		}
	 private OnClickListener search = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    String busLine = busLineName.getText().toString();
				DatabaseHelper helper = new DatabaseHelper();
				BusLine temp = helper.searchBusLine(busLine);
				String result="";
				if(temp.getBusLineID()!=0) 
				{					
					result = "ID:"+temp.getBusLineID()+"Name:"+temp.getBusLineName()+"Note:"+temp.getBusLineNote();
                    //测试站点列表是否得到；
					/*for(int i = 0;i<temp.getStationList().size();i++)
					{
						result += temp.getStationList().get(i).getStationName()+"-&-";
					}*/
				}
				
				busLineResult.setText(temp.getBusLineName());
				busLineTime.setText(temp.getBusLineNote());

				//站点列表的内容绑定
				stationGridView = (GridView) findViewById(R.id.linelist);
				ArrayList<BusStation> stationListArray = temp.getStationList();
				stationListAdapter = new StationListAdapter(LineActivity.this, stationListArray);
				stationGridView.setAdapter(stationListAdapter);
				
				//设置GridView属性，为GridView单行滑动显示
				int size = stationListArray.size();  	
				int columnWidth = 30;
				LayoutParams params = new LayoutParams(size * columnWidth + 10 ,LayoutParams.WRAP_CONTENT);
				stationGridView.setLayoutParams(params);    //gridView的整体宽度
		        stationGridView.setNumColumns(size);  
				stationGridView.setColumnWidth(columnWidth);
				stationGridView.setHorizontalSpacing(0);
				
				fweiMapBus.setVisibility(0);
				
					
				
			
				//Double r = distance(108.99409,34.24532,108.99406,34.24532);
				//r.toString();
			   // busLineInfo.setText(r.toString()); 
				
				
				
			}
	 };
	 
	 
		 
	 
}
