package app.bus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import app.bus.adapter.StationListAdapter;
import app.bus.database.BusLine;
import app.bus.database.BusStation;
import app.bus.database.DatabaseHelper;

public class LineInfoActivity extends Activity{
	private TextView busLineTime ;
	private TextView busLineResult ;
	private Button fweiMapBus ;
    private ImageButton busLineCollection,homePage;
    private StationListAdapter stationListAdapter;
    private String busLineName;
    private GridView stationGridView;

	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		setContentView(R.layout.lineinfo);
		
		busLineName = this.getIntent().getStringExtra("busLineName");
		
		Log.i("lineinfo", busLineName);
		busLineTime = (TextView) findViewById(R.id.linetimeinfo);
		busLineResult = (TextView) findViewById(R.id.lineresultinfo);	
	    busLineCollection =(ImageButton) findViewById(R.id.linecollectioninfo);
	    busLineCollection.setOnClickListener(collection);
	    homePage = (ImageButton) findViewById(R.id.homepageinfo);
	    homePage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LineInfoActivity.this, MainActivity.class);
				startActivity(intent);
				
			}
		});
	    
	    //�����ݿ��в�������
	    DatabaseHelper helper = new DatabaseHelper();
		BusLine temp = helper.searchBusLine(busLineName);
		
		//�жϸ���·�Ƿ��ղ�
		if(helper.searchLineCollectionItem(busLineName))
			busLineCollection.setImageResource(R.drawable.collection);
		
		//String result="";
		/*if(temp.getBusLineID()!=0) 
		{					
			result = "ID:"+temp.getBusLineID()+"Name:"+temp.getBusLineName()+"Note:"+temp.getBusLineNote();
            //����վ���б��Ƿ�õ���
			for(int i = 0;i<temp.getStationList().size();i++)
			{
				result += temp.getStationList().get(i).getStationName()+"-&-";
			}
		}*/
		
		busLineResult.setText(busLineName);
		busLineTime.setText(temp.getBusLineNote());

		//վ���б�����ݰ�
		stationGridView = (GridView) findViewById(R.id.linelistinfo);
		ArrayList<BusStation> stationListArray = temp.getStationList();
		stationListAdapter = new StationListAdapter(LineInfoActivity.this, stationListArray);
		stationGridView.setAdapter(stationListAdapter);
		
		//����GridView���ԣ�ΪGridView���л�����ʾ
		int size = stationListArray.size();  	
		int columnWidth = 30;
		LayoutParams params = new LayoutParams(size * columnWidth + 10 ,LayoutParams.WRAP_CONTENT);
		stationGridView.setLayoutParams(params);    //gridView��������
        stationGridView.setNumColumns(size);  
		stationGridView.setColumnWidth(columnWidth);
		stationGridView.setHorizontalSpacing(0);
	    
	    
	    
	    fweiMapBus=(Button) findViewById(R.id.fwei_map_businfo);
	    fweiMapBus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent gaodeIntent = new Intent();
				
				
				//��ֵ
				gaodeIntent.putExtra("busname", busLineName);
				gaodeIntent.setClass(LineInfoActivity.this, FweiBusLineSearch.class);
				startActivity(gaodeIntent);	
			}
		});

	    
	    
	}

		
	
	
	 private OnClickListener collection = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHelper dbHelper = new DatabaseHelper();
				if(!dbHelper.searchLineCollectionItem(busLineName))
				{
					dbHelper.addLineCollection(busLineResult.getText().toString());		
					busLineCollection.setImageResource(R.drawable.collection);
				}
				else
				{
					dbHelper.deleteLineCollectionItem(busLineName);
					busLineCollection.setImageResource(R.drawable.nocollection);
				}
				
			}
		};

}
