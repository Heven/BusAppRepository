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
	  //���mapview
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
				

	  //��ͼ����ʾ��ʽΪ��ͨͼ
        mapView.setTraffic(true);
        //���ÿɿ�
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);
       //�õ�gps�豸�ķ���
         LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //����gps��λ����
        Criteria criteria=new Criteria();
         //������ʾ����
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        //�Ƿ��ú�������
        criteria.setAltitudeRequired(false);
        //�Ƿ��÷�������
        criteria.setBearingRequired(false);
        //�Ƿ�������Ӫ�̼Ʒ�
        criteria.setCostAllowed(true);
        //���úĵ�̶�
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //��÷���Ӧ��
        String provider=locationManager.getBestProvider(criteria, true);
        
        //��ȡ��һ����λ��
        /*ʵʱ��λ���벿��      
        location =locationManager.getLastKnownLocation(provider);    
      
        		Log.e("location", "66666666666");
                
      
        	
        //���gps��λ������Ϣ
        Double latitude=location.getLatitude()*1E6;
        Log.e("location", "66666666666");
        Double longitude=location.getLongitude()*1E6;
        Log.e("location", "66666666666");
          */
       
       la = Double.parseDouble(Latitude.getText().toString())*1E6;
       lon = Double.parseDouble(Longitude.getText().toString())*1E6;
		msg = "���ȣ�" + la + "\n";
		msg += "γ�ȣ�" + lon + "\n";
		//���ݾ�γ�Ȼ�øĵ��ַ��Ϣ
		
		myLocation.setText(msg);
        //������Ƕ�λ��
        geoPoint=new GeoPoint(la.intValue(),lon.intValue());
        Log.e("location", "7777777777");
        //��õ�ͼ������
        mapController=mapView.getController();
        //���õ�ͼ��ʾ��ʼ������
        mapController.setZoom(19);
        mapController.animateTo(geoPoint);
        //ʵ�����Զ����ͼ��
        Log.e("location", "8888888888888");
        MyOverlay myOverlay=new MyOverlay();
        //Ϊmapview��ӻ�ͼ��
        mapView.getOverlays().add(myOverlay);
        //����һ��final��TextView���Ա���������
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
        		//��þ���γ���ַ���
        		msg = "���ȣ�" + location.getLongitude() + "\n";
        		msg += "γ�ȣ�" + location.getLatitude() + "\n";
        		//���ݾ�γ�Ȼ�øĵ��ַ��Ϣ
        		Log.e("location", "aaaaaaaaaaaaaaaa");
        		Geocoder gc=new Geocoder(MoreActivity.this);
        		List<Address> addresses=gc.getFromLocation(latitude, longitude, 1);
        		Log.e("location", "bbbbbbbbbbbb");
        		if (addresses.size()>0) {
        		//��õ�ַ��Ϣ
        		msg+="AddressLine:"+addresses.get(0).getAddressLine(0)+"\n";
        						//��ù�����
        		msg += "CountryName��" + addresses.get(0).getCountryName()+"\n";
        		msg += "Locality��" + addresses.get(0).getLocality() + "\n";
        		msg += "FeatureName��" + addresses.get(0).getFeatureName();
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
    	//��֤�����¼����ظ�����
    	private int count=0;
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			//���廭��
			Paint paint=new Paint();
			paint.setColor(Color.RED);
			//������Ļ��
			Point screenPoint=new Point();
			//gps��ת��Ļ��
			mapView.getProjection().toPixels(geoPoint, screenPoint);
			//���gps��־��ͼƬ
			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.da_marker_red);
			//����gps��ͼƬ
			canvas.drawBitmap(bitmap, screenPoint.x,screenPoint.y, paint);
			//��������˵��
			canvas.drawText("��ǰλ��", screenPoint.x, screenPoint.y, paint);
			return super.draw(canvas, mapView, shadow, when);
		}

		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			//����һ����Ļ��
			Point screenPoint=new Point();
			//��gps������Ļ��
			mapView.getProjection().toPixels(geoPoint, screenPoint);
			//��ô�������
			int currentX=(int) e.getX();
			int currentY=(int) e.getY();
			//��50��30��Χ�ڴ�������ʾ��ǰ��γ��
			if ((currentX-screenPoint.x)>=0&&(currentX-screenPoint.x)<50
					&&(currentY-screenPoint.y>=0)&&(currentY-screenPoint.y)<30) {
				if (count==0) {
					new AlertDialog.Builder(MoreActivity.this).setMessage(msg)
					.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
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

