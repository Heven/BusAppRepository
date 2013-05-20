package app.bus.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.bus.activity.R;

import com.amap.mapapi.busline.BusLineItem;
import com.amap.mapapi.core.GeoPoint;
import com.amap.mapapi.map.MapActivity;
import com.amap.mapapi.map.MapView;
import com.amap.mapapi.map.Overlay;
import com.amap.mapapi.map.Projection;

public class BusLineOverlay extends Overlay {
	protected static MapActivity mContext;// ��ͼactivity
	protected BusLineItem busLine;// ������·�б�
	private MapView mapView = null;
	private Path path;// ·����
	private Paint mPaint;// Paint������λ��Ƽ���ͼ�Ρ��ı���λͼ�ķ�����ɫ��Ϣ
	private InfoWindow mCurPopup = null;// ��Ϣ�����ڲ���
	
	private boolean mPopEnabled = true;//Pop��־λ

	List<StationOverlay> mOverlays = null;// վ��ͼ���б�
	MsgHandler mMsgHandler = new MsgHandler();// ��Ϣhandler
	private List<BusLineMsgHandler> mMsgsHandlers = new ArrayList<BusLineMsgHandler>();// ������·��Ϣ�б�
	private boolean boHasInit = false;

	public BusLineOverlay(MapActivity cnt, BusLineItem busLine) {// ���캯��
		mContext = cnt;
		this.busLine = busLine;
		initPaint();// ��ʼ��
	}

	public BusLineItem getBusLine() {
		return busLine;
	}

	public boolean onTrackballEvent(MotionEvent event, MapView mapView) {// Trackball׷�����¼�����ʱ��
		return onTouchEvent(event, mapView);
	}

	// �����¼�����
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {// MotionEvent
																		// �ƶ��¼�
		boolean handled = false;// ��ʶ

		for (StationOverlay overlay : mOverlays) {// ��������վ��ͼ���б�
			handled = overlay.onMouseEvent(event, mapView);
			if (handled) {
				break;//��������򷵻�true
			}
		}

		return handled;//û�д����򷵻�false
	}

	//����·�ĺ���
	public void draw(android.graphics.Canvas canvas, MapView mapView,
			boolean shadow) {
		ArrayList<Point> stack = new ArrayList<Point>();//���ջ
		buildStack(mapView, stack);
		if (stack.size() > 0) {
			drawLines(canvas, mapView, stack);
			stack.clear();
		}
		for (StationOverlay ol : mOverlays) {
			ol.draw(canvas, mapView, shadow);//��վ�㻭����
		}
	}

	/*
	 * ��mapview���뵽��ͼ��
	 */
	public void addToMap(MapView mv) {
		this.mapView = mv;
		initOverlay(mapView);

		if (mapView.getOverlays().contains(this) == false) {//getOverlays��ȡOverlay�б�
			//�б��в����������
			mapView.getOverlays().add(this);
		}
	}
	/*
	 * ��mapview�ӵ�ͼ���Ƴ�
	 */
	public boolean removeFromMap(MapView mv) {
		boolean bremove = mv.getOverlays().remove(this);
		if (bremove) {
			closePopupWindow();
			this.mapView = null; // ��remove from map�ǣ���յ�ͼ����
		}

		return bremove;
	}

	public void setBusLinePaint(Paint paint) {
		if (paint != null) {
			if (!Paint.Style.STROKE.equals(paint.getStyle())) {
				paint.setStyle(Paint.Style.STROKE);
			}
			mPaint = paint;
		}
	}

	/*
	 * ��ʼ��Paint
	 */
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.rgb(54, 114, 227));
		mPaint.setAlpha(180);//�ı���ɫ(),ֻ��ָ����ɫ��alphaֵ
		mPaint.setStrokeWidth(5.5f);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setAntiAlias(true);
	}
/*
 * ��ʼ��ͼ��
 */
	private void initOverlay(MapView mv) {
		if (boHasInit == true) {
			return; // ��־�Ѿ���ʼ�����ˣ�Ϊ�˷�ֹ���ڶ��addtomap
		}

		mOverlays = new ArrayList<StationOverlay>();

		int count = busLine.getmStations().size();//getmStations����;����վ������ 
												//siez()���ش��б��е�Ԫ������
		
		for (int i = 0; i < count; i++) {
			GeoPoint pt = busLine.getmStations().get(i).getmCoord();//get(i)���ش��б���ָ��λ���ϵ�Ԫ�ء�
															//getmCoord()  ���س�վ��γ�����ꡣ
							
			mOverlays.add(new StationOverlay(this, i, pt, resetBound(mContext
					.getResources().getDrawable(R.drawable.da_marker_red)),
					mMsgHandler));
		}

		boHasInit = true; // ����Ѿ���ʼ������
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

	public void registerBusLineMessage(BusLineMsgHandler handler) {
		mMsgsHandlers.add(handler);
	}

	public void unregisterBusLineMessage(BusLineMsgHandler handler) {
		mMsgsHandlers.remove(handler);
	}

	static Point geoToPoint(MapView mapView, GeoPoint pt) {
		Projection pj = mapView.getProjection();
		return pj.toPixels(pt, null);
	}

	private boolean isNear(Point p0, Point p1) {
		final int MaxDis = 2;
		return Math.abs(p0.x - p1.x) <= MaxDis
				&& Math.abs(p0.y - p1.y) <= MaxDis;
	}

	private int buildStack(MapView mapView, ArrayList<Point> stack) {
		int index = 0;
		ArrayList<GeoPoint> geoPts = busLine.getmXys();
		Point p0 = BusLineOverlay.geoToPoint(mapView, geoPts.get(index));
		Point p1 = null;
		while (index < geoPts.size() - 1) {
			index++;
			p1 = BusLineOverlay.geoToPoint(mapView, geoPts.get(index));
			if (stack.size() == 0) {
				stack.add(p0);
				stack.add(p1);
			} else {
				// ���p0, p1�����Ƿ����ܽ�
				if (isNear(p0, p1)) {
					stack.set(stack.size() - 1, p1);
				} else {
					stack.add(p1);
				}
			}
			p0 = p1;
		}
		if (stack.size() > 2 && isNear(stack.get(0), stack.get(1))) {
			stack.remove(1);
		}
		return index;
	}

	private void drawLines(android.graphics.Canvas canvas, MapView mapView,
			ArrayList<Point> stack) {// ���ߺ���
		if (path == null) {
			path = new Path();
		}
		boolean isFirst = true;
		int count = stack.size();
		Point point = null;
		for (int i = 0; i < count; i++) {
			point = stack.get(i);
			if (isFirst) {
				path.moveTo(point.x, point.y);
				isFirst = false;
			} else {
				path.lineTo(point.x, point.y);
			}
		}
		canvas.drawPath(path, mPaint);
		path.reset();
	}

	protected View getInfoView(MapView mapView, int index) {
		if (index < 0 || index > busLine.getmStations().size()) {
			return null;
		}

		LinearLayout ll_parents = new LinearLayout(mContext);
		ll_parents.setOrientation(LinearLayout.VERTICAL);
		ll_parents.setBackgroundColor(Color.argb(255, 255, 255, 255));

		TextView titleVw = new TextView(mContext);
		titleVw.setBackgroundColor(Color.WHITE);
		titleVw.setTextColor(Color.BLACK);
		titleVw.setText(busLine.getmStations().get(index).getmName());
		titleVw.setPadding(3, 0, 0, 3);

		ll_parents.addView(titleVw, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		// �߷ֱ���
		DisplayMetrics dm = new DisplayMetrics();
		dm = mContext.getApplicationContext().getResources()
				.getDisplayMetrics();

		long ScreenPixels = dm.widthPixels * dm.heightPixels;
		if (ScreenPixels > (320 * 480)) {
			TextView addbottom = new TextView(mContext);
			addbottom.setText("");
			addbottom.setHeight(5);
			addbottom.setWidth(1);
			ll_parents.addView(addbottom);
		}

		return ll_parents;
	}

	static boolean isInIt(MapView vw, Point pt, int margin) {
		if (pt == null) {
			return false;
		}

		int left = margin;
		int right = vw.getWidth() - margin;

		int top = margin;
		int bottom = vw.getHeight() - margin;

		return pt.x > left && pt.x < right && pt.y > top && pt.y < bottom;
	}

	public boolean showPopupWindow(int index) {
		if (!mPopEnabled) {
			return false;
		}
		if (mCurPopup != null) {
			mCurPopup.close();
		}
		if (this.mapView == null) {
			throw new UnsupportedOperationException(
					"buslineoverlay must be added to map frist!");
		}

		View vw = getInfoView(mapView, index);
		if (vw == null) {
			return false;
		}

		GeoPoint pt = busLine.getmStations().get(index).getmCoord();

		mCurPopup = new InfoWindow(mapView, vw, pt, null, null);
		mCurPopup.open();

		return true;
	}

	public void closePopupWindow() {
		if (mCurPopup != null) {
			mCurPopup.close();
		}
		mCurPopup = null;
	}

	/*
	 * ������·��Ϣhandler
	 */
	public interface BusLineMsgHandler {
		public boolean onStationClickEvent(MapView mapView,
				BusLineOverlay overlay, int index);
	}

	/*
	 * ��Ϣhandler
	 */
	class MsgHandler implements BusLineMsgHandler {
		public boolean onStationClickEvent(MapView mapView,BusLineOverlay overlay, int index) 
		{// ���������վ����¼�
			boolean handled = false;
			for (BusLineMsgHandler handler : mMsgsHandlers) 
			{
				handled = handler.onStationClickEvent(mapView, overlay, index);
				if (handled)
				{
					break;
				}
			}
			if (!handled)
			{
				showPopupWindow(index);
			}
			return handled;
		}
	}

	/*
	 * ��Ϣ������
	 */

	static class InfoWindow {
		public static InfoWindow sActive = null;// ��Ϣ���ڵľ�̬����
		public static Drawable sDefaultBack = null;// ͼƬ

		public static Bitmap bitmap9patch = null;

		protected MapView mMap;// ��ͼ��ͼ
		protected View mContent;// ��ͼ
		protected GeoPoint mPoint;// �����
		protected long mOpenTime = -1;// û���õ������廹�д��о�

		protected MapView.LayoutParams mLayout;// ���ֲ���

		// ���캯��1
		public InfoWindow(MapView view, View content, GeoPoint geo) {
			this(view, content, geo, null, null);
		}

		// ���캯��2
		public InfoWindow(MapView view, View content, GeoPoint geo,
				Drawable back, MapView.LayoutParams layout) {
			mMap = view;
			mContent = content;
			mPoint = geo;
			mLayout = layout;
			initBack(back);// ��ʼ��ͼƬ

		}

		/*
		 * ��ʼ��ͼƬ����
		 */
		private void initBack(Drawable back) {// ��ʼ��
			if (back == null) {// �����������ͼƬ�ǿյ�
				if (sDefaultBack == null) {// Ĭ��ͼƬҲ�ǿյ�
					initDefaultBack(mMap.getContext());
				}

				back = sDefaultBack;
				back.setAlpha(255);
			}
			mContent.setBackgroundDrawable(back);
		}

		/*
		 * ��ʼ��Ĭ��ͼƬ
		 */
		private void initDefaultBack(Context cnt) {

			byte[] chunk = { 1, 2, 2, 9, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 0, 0,
					19, 0, 0, 0, 15, 0, 0, 0, 36, 0, 0, 0, 0, 0, 0, 0, 20, 0,
					0, 0, -117, 0, 0, 0, 15, 0, 0, 0, 29, 0, 0, 0, 1, 0, 0, 0,
					1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, -1, -1, -1, -14, 1, 0,
					0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 };
			Rect pad = new Rect(20, 15, 19, 36);
			bitmap9patch = BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.popup_bg);
			// ����ͼƬ

			sDefaultBack = new NinePatchDrawable(bitmap9patch, chunk, pad, null);
			// ������ͼ,

		}

		/*
		 * �رմ�����Դ
		 */

		public void closeWindowInfoRes() {
			if (bitmap9patch != null && bitmap9patch.isRecycled() == false) {
				bitmap9patch.recycle();
				bitmap9patch = null;
			}
		}

		/*
		 * �򿪺���
		 */
		public void open() {
			if (isThisOpen()) {// �ǿ���֤���Ѿ����ˣ����Է���
				return;
			}

			if (sActive != null) {// �ǿ�˵���Ѿ��д򿪵��ˣ���ر��Ѿ��򿪵�
				sActive.close();
			}

			sActive = this;// �ѵ�ǰ�ĸ�ֵ��ȥ

			if (mLayout == null) {
				// ����һ���µĲ��ֲ�������
				mLayout = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, mPoint, 25, 5,
						MapView.LayoutParams.BOTTOM
								| MapView.LayoutParams.RIGHT);
			}
			// ��ӵ���ͼ��ͼ��ȥ
			mMap.addView(mContent, mLayout);
		}

		/*
		 * �رպ���
		 */
		public void close() {
			if (!isThisOpen()) {
				return;
			}
			if (mMap == null) {// û�е�ͼ��ͼ���򷵻أ�˵���Ѿ��ǿյ���

				return;
			}
			// ����ͽ����еĶ���Ϊ��
			sActive = null;
			mMap.removeView(mContent);
		}

		/*
		 * �ж��Ƿ��Ѿ��򿪺���
		 */
		private boolean isThisOpen() {
			return sActive == this;
		}
	}
}

class StationOverlay {// վ��ͼ��
	protected BusLineOverlay mOverlay;
	protected int mIndex;// ����
	protected GeoPoint mPos;
	private Drawable mFlag;// ����ͼƬ�ĵط�
	private BusLineOverlay.BusLineMsgHandler mHandler;
	private boolean mHitDowm;// �Ƿ�����־λ

	
	//���캯��
	public StationOverlay(BusLineOverlay overlay, int index, GeoPoint pos,
			Drawable dr, BusLineOverlay.BusLineMsgHandler handler) {
		mOverlay = overlay;
		mIndex = index;
		mPos = pos;
		mFlag = dr;
		mHandler = handler;
		mHitDowm = false;
	}

	protected Point geoToPoint(MapView mapView, GeoPoint pt) {//
		return mapView.getProjection().toPixels(pt, null);
		// toPixels�Ѹ�����GeoPoint�任�������MapView���Ͻǵ���Ļ��������
	}

	protected GeoPoint pointToGeo(MapView mapView, Point pt) {
		return mapView.getProjection().fromPixels(pt.x, pt.y);
		// fromPixels�÷����ø������������괴��һ���µ�GeoPoint
	}

	/*
	 * ������ʶ�ﺯ��
	 */
	private void drawMarker(android.graphics.Canvas canvas, Drawable drawable,
			int x, int y) {
		Rect rect = drawable.getBounds();// ����
		// left top right bottom ���ε��ĸ�����
		drawable.setBounds(rect.left + x, rect.top + y, rect.right + x,
				rect.bottom + y);
		drawable.draw(canvas);
		drawable.setBounds(rect.left - x, rect.top - y, rect.right - x,
				rect.bottom - y);
	}

	/*
	 * �滭վ�㺯��
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mFlag == null || shadow) {
			return;
		}

		Point pt = geoToPoint(mapView, mPos);
		drawMarker(canvas, mFlag, pt.x, pt.y);
	}

	/*
	 * �������
	 */
	private boolean hitTest(MapView map, int x, int y) {
		Point pt = geoToPoint(map, mPos);
		Rect rect = mFlag.getBounds();
		return rect.contains(x - pt.x, y - pt.y);
		// contains ���� true if (x,y) is inside the rectangle.
	}

	public boolean onMouseEvent(MotionEvent event, MapView mapView) {
		// MotionEvent event��һ�������¼������Ƕ���Ļ�ļ������в������ᴥ���¼����������ſ��������ȡ�

		boolean handled = true;

		Point pt = new Point((int) event.getX(), (int) event.getY());// �����ĵ�

		if (event.getAction() == MotionEvent.ACTION_DOWN // �ڵ�һ���㱻����ʱ����
				&& hitTest(mapView, pt.x, pt.y)) {
			mHitDowm = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP && mHitDowm) {
			mHitDowm = false;// MotionEvent.ACTION_UP:����Ļ��Ψһ�ĵ㱻�ſ�ʱ����
			mHandler.onStationClickEvent(mapView, mOverlay, mIndex);
		} else {
			handled = false;
		}

		return handled;
	}

	public void setGeoPoint(GeoPoint pos) {
		mPos = pos;
	}

	public void setDrawable(Drawable dr) {
		mFlag = dr;
	}
}
