package app.bus.activity;

import java.util.ArrayList;
import java.util.List;
import app.bus.map.*;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import app.bus.activity.R;

import com.amap.mapapi.busline.BusLineItem;
import com.amap.mapapi.busline.BusPagedResult;
import com.amap.mapapi.busline.BusQuery;
import com.amap.mapapi.busline.BusSearch;
import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;

public class BusLineSearch extends MapActivity implements
		OnItemSelectedListener, OnClickListener,
		BusLineOverlay.BusLineMsgHandler {
	private MapView mMapView = null;//MapView一个显示地图（数据来自MapABC地图服务）的视图
	private Button searchbynameBtn;//按线路名字搜索按钮
//	private Spinner selectCity;//下拉列表
	private EditText searchName;
	private String[] itemCitys = { "西安-029", "北京-010", "上海-021" };
	private String cityCode="029";
	
	//传值字符串
	private String fweiSearchName;
	
	private EditText pageSizeText;//搜索结果中显示的条目个数
	/*
	 * 随便写点注释吧！！！
	 */
	private Button searchbystationBtn;
	private ProgressDialog progDialog = null;//对话框
	
	private BusPagedResult result = null;
	//公交线路搜索结果是分页显示的，从第1页开始，每页最多20个BusLineItem。BusPagedResult封装了此分页结果，
	//并且会缓存已经检索到的页的搜索结果。
	//此类不可直接构造，只能通过调用类BusSearch的searchBusLine()方法得到。 
	private BusLineOverlay overlay = null;
	private int curPage = 1;

	private MapController mMapController;// 一个工具类，用于控制地图的缩放和平移。
	private GeoPoint point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);   
		setContentView(R.layout.bussearch);
		
		mMapView = ((MapView) findViewById(R.id.buslinesearchmapview));
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		
		Intent intent=getIntent();
		fweiSearchName=intent.getStringExtra("busname");
		Toast.makeText(this, fweiSearchName, Toast.LENGTH_LONG).show();

		
		

		mMapController = mMapView.getController();// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (34.269701 * 1E6), (int) (108.934937 * 1E6));
		mMapController.setCenter(point); // 设置地图中心点

//		searchbynameBtn = (Button) this.findViewById(R.id.searchbyname);
//		searchbynameBtn.setOnClickListener(this);//由于该类继承了view的监听，   
												//因此设置监听的参数只需传本类的对象即可   

		//关于上文的说明http://blog.csdn.net/dickren123/article/details/7216975
		
		
//		selectCity = (Spinner) findViewById(R.id.cityName);
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, itemCitys);
		//字符数组适配器
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//调用setDropDownViewResource方法,以XML的方式定义下拉菜单要显示的模样.
		
//		selectCity.setAdapter(adapter);
//		selectCity.setPrompt("请选择城市：");
//		selectCity.setOnItemSelectedListener(this);
		//A spinner does not support item click events. Calling this method will raise an exception
		
		

//		pageSizeText = (EditText) findViewById(R.id.pageSize);

//		searchbystationBtn = (Button) findViewById(R.id.searchbystation);
//		searchbystationBtn.setOnClickListener(this);
		
		
		
		
		progDialog = ProgressDialog.show(BusLineSearch.this, null, "正在搜索...",
				true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String search = fweiSearchName;
				BusQuery.SearchType type = BusQuery.SearchType.BY_LINE_NAME;
				//默认是线路搜索
			
				try {
					curPage = 1;
					BusSearch busSearch = new BusSearch(BusLineSearch.this,
							new BusQuery(search, type, cityCode)); // 设置搜索字符串
					
					//BusSearch类为公交线路搜索的“入口”类，定义此类，开始搜索。
					//在类BusSearch中，使用BusQuery类设定搜索参数。 
					
					busSearch.setPageSize(2);//默认显示四个
					
					String text = "2";
					
					if (text.length() > 0)
						busSearch.setPageSize(Integer.parseInt(text));
					
					result = busSearch.searchBusLine();//根据指定查询类型和关键字搜索公交线路结果。
					Log.d("AMAP POI search", "poi search page count = "
							+ result.getPageCount());
					
					buslineHandler.sendEmptyMessage(Constants.BUSLINE_RESULT);
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constants.BUSLINE_ERROR_RESULT;
					msg.obj = e.getErrorMessage();
					buslineHandler.sendMessage(msg);
				}
			}

		});
		t.start();

	}

	@Override
	protected void onDestroy() {
		if (overlay != null) {
			overlay.removeFromMap(mMapView);
		}
		super.onDestroy();
	}
/*
 * 画出公交路线函数
 */
	private void drawBusLine(BusLineItem busLine) {
		if (overlay != null) {//已经有图层了，则需要把从前的图层消除掉
			overlay.removeFromMap(mMapView);
		}
		overlay = new BusLineOverlay(this, busLine);
		overlay.registerBusLineMessage(BusLineSearch.this);
		
		overlay.addToMap(mMapView);
		
		ArrayList<GeoPoint> pts = new ArrayList<GeoPoint>();
		
		pts.add(busLine.getLowerLeftPoint());
		pts.add(busLine.getUpperRightPoint());
		
		mMapView.getController().setFitView(pts);// 调整地图显示范围
		mMapView.invalidate();//使无效
	}
/*
 * 显示结果列表
 */
	private void showResultList(List<BusLineItem> list) {
		BusSearchDialog dialog = new BusSearchDialog(BusLineSearch.this, list);

		dialog.setTitle("搜索结果:");
		dialog.setOnListClickListener(new OnListItemClick() {
			@Override
			public void onListItemClick(BusSearchDialog dialog,
					final BusLineItem busLineItem) {
				progDialog = ProgressDialog.show(BusLineSearch.this, null,
						"正在搜索...", true, false);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						String lineId = busLineItem.getmLineId();
						BusSearch busSearch = new BusSearch(BusLineSearch.this,
								new BusQuery(lineId, BusQuery.SearchType.BY_ID,
										cityCode)); // 设置搜索字符串
						try {
							result = busSearch.searchBusLine();
							buslineHandler
									.sendEmptyMessage(Constants.BUSLINE_DETAIL_RESULT);
						} catch (AMapException e) {
							Message msg = new Message();
							msg.what = Constants.BUSLINE_ERROR_RESULT;
							msg.obj = e.getErrorMessage();
							buslineHandler.sendMessage(msg);
						}
					}

				});
				t.start();
			}

		});
		dialog.show();
	}

	@Override
	
/*
 * onClick函数
 */
	public void onClick(View v) {
		final Button btn = (Button) v;
		progDialog = ProgressDialog.show(BusLineSearch.this, null, "正在搜索...",
				true, false);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String search = fweiSearchName.trim();
				BusQuery.SearchType type = BusQuery.SearchType.BY_LINE_NAME;
				//默认是线路搜索
				if (searchbynameBtn.equals(btn)) {
					if ("".equals(search)) {
						search = "45";//默认路线
					}
				} else if (searchbystationBtn.equals(btn)) {
					if ("".equals(search)) {
						search = "钟楼";
					}
					type = BusQuery.SearchType.BY_STATION_NAME;
					//BusQuery类定义了公交线路搜索的关键字，类别及城市
					//BusQuery.SearchType  定义公交线路搜索类型
				}
				try {
					curPage = 1;
					BusSearch busSearch = new BusSearch(BusLineSearch.this,
							new BusQuery(search, type, cityCode)); // 设置搜索字符串
					
					//BusSearch类为公交线路搜索的“入口”类，定义此类，开始搜索。
					//在类BusSearch中，使用BusQuery类设定搜索参数。 
					
					busSearch.setPageSize(4);//默认显示四个
					
					String text = pageSizeText.getText().toString();
					
					if (text.length() > 0)
						busSearch.setPageSize(Integer.parseInt(text));
					
					result = busSearch.searchBusLine();//根据指定查询类型和关键字搜索公交线路结果。
					Log.d("AMAP POI search", "poi search page count = "
							+ result.getPageCount());
					
					buslineHandler.sendEmptyMessage(Constants.BUSLINE_RESULT);
				} catch (AMapException e) {
					Message msg = new Message();
					msg.what = Constants.BUSLINE_ERROR_RESULT;
					msg.obj = e.getErrorMessage();
					buslineHandler.sendMessage(msg);
				}
			}

		});
		t.start();
	}

	private Handler buslineHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == Constants.BUSLINE_RESULT) {
				progDialog.dismiss();
				if (overlay != null) {
					overlay.removeFromMap(mMapView);
				}
				List<BusLineItem> items;
				try {
					if (result == null
							|| (items = result.getPage(curPage)) == null
							|| items.size() == 0) {
						Toast.makeText(getApplicationContext(), "没有找到！",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.d("AMAP busline search",
								"item number of 1st page = " + items.size());
						Log.d("AMAP busline search", items.toString());

						showResultList(items);
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}
			} else if (msg.what == Constants.BUSLINE_DETAIL_RESULT) {
				progDialog.dismiss();
				List<BusLineItem> list;
				try {
					if (result != null) {
						list = result.getPage(1);
						if (list != null && list.size() > 0) {
							drawBusLine(list.get(0));
						}
					}
				} catch (AMapException e) {
					e.printStackTrace();
				}
			} else if (msg.what == Constants.BUSLINE_ERROR_RESULT) {
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String cityString = itemCitys[0];
		cityCode = cityString.substring(cityString.indexOf("-") + 1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}

	interface OnListItemClick {
		/**
		 * 这个方法当对话框取消时被调用
		 * 
		 * @param dialog
		 *            The dialog that was canceled will be passed into the
		 *            method.
		 */
		public void onListItemClick(BusSearchDialog dialog, BusLineItem item);
	}

	public class BusSearchDialog extends Dialog implements OnItemClickListener,
			OnItemSelectedListener {
		private List<BusLineItem> busLineItems;
		private BusSearchAdapter adapter;
		protected OnListItemClick mOnClickListener;
		private Button preButton, nextButton;

		public BusSearchDialog(Context context) {
			this(context, android.R.style.Theme_Dialog);
		}

		public BusSearchDialog(Context context, int theme) {
			super(context, theme);
		}

		public BusSearchDialog(Context context, List<BusLineItem> busLineItems) {
			this(context, android.R.style.Theme_Dialog);
			this.busLineItems = busLineItems;
			adapter = new BusSearchAdapter(context, busLineItems);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.navsearch_list_busline);
			ListView listView = (ListView) findViewById(R.id.ListView_busline);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dismiss();
					mOnClickListener.onListItemClick(BusSearchDialog.this,
							busLineItems.get(position));
				}
			});

			onButtonClick listener = new onButtonClick();
			preButton = (Button) findViewById(R.id.preButton);
			if (curPage <= 1) {
				preButton.setEnabled(false);
			}
			preButton.setOnClickListener(listener);
			nextButton = (Button) findViewById(R.id.nextButton);
			if (curPage >= result.getPageCount()) {
				nextButton.setEnabled(false);
			}
			nextButton.setOnClickListener(listener);
		}

		class onButtonClick implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				BusSearchDialog.this.dismiss();
				if (v.equals(preButton)) {
					curPage--;
				} else if (v.equals(nextButton)) {
					curPage++;
				}

				progDialog = ProgressDialog.show(BusLineSearch.this, null,
						"正在搜索...", true, false);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							result.getPage(curPage);
							buslineHandler
									.sendEmptyMessage(Constants.BUSLINE_RESULT);
						} catch (AMapException e) {
							Message msg = new Message();
							msg.what = Constants.BUSLINE_ERROR_RESULT;
							msg.obj = e.getErrorMessage();
							buslineHandler.sendMessage(msg);
						}
					}

				});
				t.start();
			}
		}

		@Override
		public void onItemClick(AdapterView<?> view, View view1, int arg2,
				long arg3) {

		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

		public void setOnListClickListener(OnListItemClick l) {
			mOnClickListener = l;
		}
	}

	// Dialog list view adapter对话框列表适配器
	public class BusSearchAdapter extends BaseAdapter {
		private List<BusLineItem> busLineItems = null;
		private LayoutInflater mInflater;

		public BusSearchAdapter(Context context, List<BusLineItem> busLineItems) {
			this.busLineItems = busLineItems;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return busLineItems.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.bus_result_list, null);
			}

			TextView PoiName = ((TextView) convertView
					.findViewById(R.id.buslineName));
			TextView poiAddress = (TextView) convertView
					.findViewById(R.id.buslineLength);
			PoiName.setText(busLineItems.get(position).getmName());
			float length = busLineItems.get(position).getmLength();
			poiAddress.setText("全长:" + length + "公里");
			return convertView;
		}
	}

	@Override
	public boolean onStationClickEvent(MapView mapView, BusLineOverlay overlay,
			int index) {
		return false;
	}
}
