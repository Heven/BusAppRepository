package app.bus.activity;

import java.util.ArrayList;
import java.util.List;

import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.geocoder.Geocoder;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;
import com.amap.mapapi.map.Overlay;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import app.bus.adapter.StationAdapter;
import app.bus.database.DatabaseHelper;
import app.bus.database.Station;

public class MoreActivity extends MapActivity {
	
	private LocationManager locationManager;
    private MapController mapController;
    private GeoPoint geoPoint;
	private String msg;
    private Location location;
    private EditText Longitude;
    private EditText Latitude;
    private Button getLocation;
    private Button search;
    private MapView mapView;
    private TextView myLocation;
    private Double la;
    private Double lon;
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);
	  //获得mapview
	    mapView=(MapView) this.findViewById(R.id.busstationmapview);
	    Latitude = (EditText)this.findViewById(R.id.Latitude);
	    Longitude = (EditText)this.findViewById(R.id.Longitude);
	    getLocation = (Button)this.findViewById(R.id.getlocation);
	    search = (Button)this.findViewById(R.id.stationsearch);
	    myLocation = (TextView)this.findViewById(R.id.mylocation);
	    getLocation.setOnClickListener(new OnClickListener() {
	    	
	    	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				

	  //地图的显示格式为交通图
        mapView.setTraffic(true);
        //设置可控
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);
       //得到gps设备的访问
         LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //设置gps定位配置
        Criteria criteria=new Criteria();
         //设置显示精度
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //是否获得海拔数据
        criteria.setAltitudeRequired(false);
        //是否获得方向数据
        criteria.setBearingRequired(false);
        //是否允许运营商计费
        criteria.setCostAllowed(true);
        //设置耗电程度
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //获得服务供应商
        String provider=locationManager.getBestProvider(criteria, true);
        
        //获取上一个定位点
        /*实时定位代码部分      
        location =locationManager.getLastKnownLocation(provider);    
      
        		Log.e("location", "66666666666");
                
      
        	
        //获得gps定位坐标信息
        Double latitude=location.getLatitude()*1E6;
        Log.e("location", "66666666666");
        Double longitude=location.getLongitude()*1E6;
        Log.e("location", "66666666666");
          */
       
       la = Double.parseDouble(Latitude.getText().toString())*1E6;
       lon = Double.parseDouble(Longitude.getText().toString())*1E6;
		msg = "经度：" + la + "\n";
		msg += "纬度：" + lon + "\n";
		//根据经纬度获得改点地址信息
		
		myLocation.setText(msg);
        //获得卫星定位点
        geoPoint=new GeoPoint(la.intValue(),lon.intValue());
        Log.e("location", "7777777777");
        //获得地图控制器
        mapController=mapView.getController();
        //设置地图显示初始化精度
        mapController.setZoom(19);
        mapController.animateTo(geoPoint);
        //实例化自定义绘图层
        Log.e("location", "8888888888888");
        MyOverlay myOverlay=new MyOverlay();
        //为mapview添加绘图层
        mapView.getOverlays().add(myOverlay);
        //定义一个final，TextView，以备子类引用
     //  final TextView textView=(TextView) findViewById(R.id.textview);
        Log.e("location", "999999999999");
        LocationListener locationListener=new LocationListener() {
			
        	@Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
        				      				
        	}
        			
        	@Override
        	public void onProviderEnabled(String provider) {
        	// TODO Auto-generated method stub
        				
        	}
        			
        	@Override
        	public void onProviderDisabled(String provider) {
        	// TODO Auto-generated method stub
        				
        	}
        			
        	@Override
        	public void onLocationChanged(Location location) {
        	MoreActivity.this.location=location; 
        			
        	Double latitude=location.getLatitude()*1E6;
        	Double longitude=location.getLongitude()*1E6;
        	try {
        		//获得精度纬度字符串
        		msg = "经度：" + location.getLongitude() + "\n";
        		msg += "纬度：" + location.getLatitude() + "\n";
        		//根据经纬度获得改点地址信息
        		Log.e("location", "aaaaaaaaaaaaaaaa");
        		Geocoder gc=new Geocoder(MoreActivity.this);
        		List<Address> addresses=gc.getFromLocation(latitude, longitude, 1);
        		Log.e("location", "bbbbbbbbbbbb");
        		if (addresses.size()>0) {
        		//获得地址信息
        		msg+="AddressLine:"+addresses.get(0).getAddressLine(0)+"\n";
        						//获得国家名
        		msg += "CountryName：" + addresses.get(0).getCountryName()+"\n";
        		msg += "Locality：" + addresses.get(0).getLocality() + "\n";
        		msg += "FeatureName：" + addresses.get(0).getFeatureName();
        		Log.e("location", msg);
        		}
        		myLocation.setText(msg);
        		} catch (Exception e) {
        		e.printStackTrace();
        		}
        				
        			}
        		};
        		 locationManager.requestLocationUpdates(provider, 60000, 1, locationListener);
 				
 			}}
 			);
	    
	    search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHelper helper = new DatabaseHelper();
				ArrayList<Station> temp = helper.searchNearbyBusStation(la, lon);
				String result="";
				int i = temp.size();
				
			}

		});
    }
		   
	class MyOverlay extends Overlay{
    	//保证触控事件不重复操作
    	private int count=0;
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			//定义画笔
			Paint paint=new Paint();
			paint.setColor(Color.RED);
			//定义屏幕点
			Point screenPoint=new Point();
			//gps点转屏幕点
			mapView.getProjection().toPixels(geoPoint, screenPoint);
			//获得gps标志点图片
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.da_marker_red);
			//绘制gps点图片
			canvas.drawBitmap(bitmap, screenPoint.x,screenPoint.y, paint);
			//绘制文字说明
			canvas.drawText("当前位置", screenPoint.x, screenPoint.y, paint);
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			//定义一个屏幕点
			Point screenPoint=new Point();
			//把gps点变成屏幕点
			mapView.getProjection().toPixels(geoPoint, screenPoint);
			//获得触点坐标
			int currentX=(int) e.getX();
			int currentY=(int) e.getY();
			//在50，30范围内触碰，显示当前经纬度
			if ((currentX-screenPoint.x)>=0&&(currentX-screenPoint.x)<50
					&&(currentY-screenPoint.y>=0)&&(currentY-screenPoint.y)<30) {
				if (count==0) {
					new AlertDialog.Builder(MoreActivity.this).setMessage(msg)
					.setPositiveButton("确定",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							count=0;
						}
						
					}).show();
				}
				count++;
			}
			return super.onTouchEvent(e, mapView);
		}
    	
    }
    
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}

