package app.bus.activity;

import java.util.ArrayList;
import java.util.List;

import com.amap.mapapi.core.AMapException;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.core.OverlayItem;
import com.amap.mapapi.geocoder.Geocoder;
import com.amap.mapapi.map.ItemizedOverlay;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapController;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.MyLocationOverlay;
import com.amap.mapapi.map.Overlay;
import com.amap.mapapi.map.Projection;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
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
import app.bus.map.BusLineOverlay;
import app.bus.map.Constants;

public class MoreActivity extends MapActivity {
	
	private LocationManager locationManager;
    private MapController mapController;
    private GeoPoint geoPoint;
	private String msg;
	private String provider;
    private Location location;
    private Double Longitude;
    private Double Latitude;
    private Button getLocation;
    private Button search;
    private MapView mapView;
    private TextView myLocation;
    private Double la;
    private Double lon;
    private ArrayList<Station> NearbyStations;
    private StationItemizedOverlay itemOverlay;
	private List<Overlay> mOverlays ;
	private ProgressDialog progDialog;//�Ի���
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);	 
	    mapView=(MapView) this.findViewById(R.id.busstationmapview); //���mapview
	    Latitude = 34.250575;
	    Longitude = 108.983709;
	    getLocation = (Button)this.findViewById(R.id.getlocation);
	    search = (Button)this.findViewById(R.id.stationsearch);
	    myLocation = (TextView)this.findViewById(R.id.mylocation);
	    
	    mOverlays=new ArrayList<Overlay>();	 
	    Drawable drawable = this.getResources().getDrawable(R.drawable.da_marker_red);  
		itemOverlay = new StationItemizedOverlay(drawable, MoreActivity.this);
		
        mapView.setTraffic(true);//��ͼ����ʾ��ʽΪ��ͨͼ        
        mapView.setClickable(true);//���ÿɿ�
        mapView.setEnabled(true);
        mapView.setBuiltInZoomControls(true);//�õ�gps�豸�ķ���       
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);//����gps��λ����        
        Criteria criteria=new Criteria();//������ʾ����
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//�Ƿ��ú�������        
        criteria.setAltitudeRequired(false);//�Ƿ��÷�������        
        criteria.setBearingRequired(false);//�Ƿ�������Ӫ�̼Ʒ�        
        criteria.setCostAllowed(true);//���úĵ�̶�       
        criteria.setPowerRequirement(Criteria.POWER_LOW); //��÷���Ӧ��      
        provider=locationManager.getBestProvider(criteria, true);
        mapController=mapView.getController();       
        mapController.setZoom(9); //���õ�ͼ��ʾ��ʼ������
        GeoPoint point = new GeoPoint((int) (34.269701 * 1E6), (int) (108.934937 * 1E6));
		mapController.setCenter(point); // ���õ�ͼ���ĵ�
	    getLocation.setOnClickListener(new OnClickListener() {
	    	
	    	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub  
	    		mOverlays.clear();
	            mOverlays = mapView.getOverlays();
       // la = Double.parseDouble(Latitude.getText().toString())*1E6;
       // lon = Double.parseDouble(Longitude.getText().toString())*1E6;//���ݾ�γ�Ȼ�øĵ��ַ��Ϣ		
	            la = 34.250575*1E6;
	            lon = 108.983709*1E6;
		myLocation.setText(msg);//������Ƕ�λ��        
        geoPoint=new GeoPoint(la.intValue(),lon.intValue());//��õ�ͼ������      
        mapController.animateTo(geoPoint);
        mapController.setZoom(14);//ʵ�����Զ����ͼ��        
        OverlayItem myItem = new OverlayItem(geoPoint, "��ǰλ��",msg);
		  // MyOverlay demo =new MyOverlay(MoreActivity.this,sgeoPoint,temp.get(i).getStationName());
         itemOverlay.addOverlay(myItem);        
         mOverlays.add(itemOverlay);
	     mapView.postInvalidate();
        //����һ��final��TextView���Ա���������
     //  final TextView textView=(TextView) findViewById(R.id.textview);
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
        		Geocoder gc=new Geocoder(MoreActivity.this);
        		List<Address> addresses=gc.getFromLocation(latitude, longitude, 1);
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
				progDialog = ProgressDialog.show(MoreActivity.this, null, "��������...",
						true, false);
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						DatabaseHelper helper = new DatabaseHelper();
						Double lat = Latitude;
						Double lng = Longitude;			
				try {
					ArrayList<Station> temp = helper.searchNearbyBusStation(
							lat, lng);
					
					int count = nearbyStation(lat, lng, temp);
					Log.e("first", "" + temp.size());
					if (count == 0) {
						new AlertDialog.Builder(MoreActivity.this)
								.setMessage("����û��վ��")
								.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												//count=0;
											}

										}).show();
						
					}
					progDialog.dismiss();
				} catch (Exception e) {
					// TODO: handle exception
				}
				 
			}});
				t.start();
			}});		
				
    }
		   
	int nearbyStation(Double la,Double lon,ArrayList<Station> temp){
		
		    int count = 0;
			DatabaseHelper helper = new DatabaseHelper();
		    Log.e("juli",""+temp.size());					
		    		for(int i=0;i<temp.size();i++){
		    			String stationName = temp.get(i).getStationName();
		    			
		    			 Log.e("latitude", ""+temp.get(i).getLatitude());
						   Log.e("longetude", ""+temp.get(i).getLongitude());
		    		    double a1=la*Math.PI/180.0;
		    	        double a2=Double.parseDouble(temp.get(i).getLatitude())*Math.PI/180.0;
		    	        double a=a1-a2;
		    	        double b=(lon-Double.parseDouble(temp.get(i).getLongitude()))*Math.PI/180.0;
		    	        double s=2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(a1)*Math.cos(a2)*Math.pow(Math.sin(b/2),2)));
		    	        s=s*6371;//����뾶
		    	        Log.e("juli",""+s);
		    	        s = Math.round(s*10000)/10000;   
		    	        Log.e("juli",""+s);
		    	        if(s<0.2){
		    	        	   Double sla = Double.parseDouble(temp.get(i).getLatitude())*1E6;
						       Double slon = Double.parseDouble(temp.get(i).getLongitude())*1E6;
							   GeoPoint  sgeoPoint=new GeoPoint(sla.intValue(),slon.intValue());
						        //Ϊmapview��ӻ�ͼ��
						       // mOverlays.add(new MyOverlay(sgeoPoint));
							   Log.e("latitude", ""+sla);
							   Log.e("longetude", ""+slon);
                               OverlayItem oitem = new OverlayItem(sgeoPoint,stationName,stationName);
							  // MyOverlay demo =new MyOverlay(MoreActivity.this,sgeoPoint,temp.get(i).getStationName());
                               itemOverlay.addOverlay(oitem);
							   //mOverlays.add(demo);
		    	        count++;
		    	        }	 
		    	        
		    }
		    		Log.e("result", ""+mOverlays.size());
			     //   mapView.getOverlays().addAll(mOverlays);
		    		mOverlays.add(itemOverlay);
			        mapView.postInvalidate();
		    		Log.e("result", ""+count);
           return count;		
	}
	
	private Drawable resetBound(Drawable balloon) {
		if (balloon == null) {
			return null;
		}

		balloon.setBounds(0, 0, balloon.getIntrinsicWidth(),
				balloon.getIntrinsicHeight());

		Rect rect = balloon.getBounds();
		int w = rect.width() / 2;
		int ytop = -rect.height();
		int ydown = 0;
		balloon.setBounds(-w, ytop + 15, w, ydown + 15);
		return balloon;
	}
	/*
	 * ���д��ע�������ԣ�
	 */

}
class StationItemizedOverlay extends ItemizedOverlay<OverlayItem>
{
 private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
 private Context mContext;

 public StationItemizedOverlay(Drawable defaultMarker, Context context)
 {
 super(boundCenterBottom(defaultMarker));
 mContext = context;
 }

 public void addOverlay(OverlayItem overlay)
 {
 mOverlays.add(overlay);
 populate();
 }
 @Override
 protected OverlayItem createItem(int i)
 {
 return mOverlays.get(i);
 }
 @Override
 public int size()
 {
 return mOverlays.size();
 }

 public void draw(Canvas canvas,MapView mapView,boolean shadow){
	 super.draw(canvas, mapView, shadow);
	 Projection projection = mapView.getProjection();
	 //�������е�overlayItem
	 for(int index = this.size()-1;index >=0;index--){
		 OverlayItem oitem = getItem(index);
		 Point point = projection.toPixels(oitem.getPoint(),null);
		 Paint paintText = new Paint();
		 paintText.setColor(Color.RED);
		 paintText.setTextSize(13);
		 canvas.drawText(oitem.getTitle(), point.x+5, point.y-35, paintText);
		 
	 }
 }
 @Override
 protected boolean onTap(int index)
 {
 OverlayItem item = mOverlays.get(index);
 DatabaseHelper helper = new DatabaseHelper();
 ArrayList<Station> station = helper.searchBusStation(item.getSnippet());
	String info = "������վ�����·�У�";
	for(int j= 0 ;j<station.size();j++){
		info += station.get(j).getBusLine();
	}
 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext).setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
											}});
 dialog.setTitle(item.getTitle());
 dialog.setMessage(info);
 dialog.show();
 return true;
 }
}
