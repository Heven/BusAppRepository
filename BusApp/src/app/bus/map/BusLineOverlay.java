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
	protected static MapActivity mContext;// 地图activity
	protected BusLineItem busLine;// 公交线路列表
	private MapView mapView = null;
	private Path path;// 路线类
	private Paint mPaint;// Paint类有如何绘制几何图形、文本和位图的风格和颜色信息
	private InfoWindow mCurPopup = null;// 消息窗口内部类
	
	private boolean mPopEnabled = true;//Pop标志位

	List<StationOverlay> mOverlays = null;// 站点图层列表
	MsgHandler mMsgHandler = new MsgHandler();// 消息handler
	private List<BusLineMsgHandler> mMsgsHandlers = new ArrayList<BusLineMsgHandler>();// 公交线路消息列表
	private boolean boHasInit = false;

	public BusLineOverlay(MapActivity cnt, BusLineItem busLine) {// 构造函数
		mContext = cnt;
		this.busLine = busLine;
		initPaint();// 初始化
	}

	public BusLineItem getBusLine() {
		return busLine;
	}

	public boolean onTrackballEvent(MotionEvent event, MapView mapView) {// Trackball追踪球事件触发时！
		return onTouchEvent(event, mapView);
	}

	// 触摸事件函数
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {// MotionEvent
																		// 移动事件
		boolean handled = false;// 标识

		for (StationOverlay overlay : mOverlays) {// 遍历整个站点图层列表
			handled = overlay.onMouseEvent(event, mapView);
			if (handled) {
				break;//如果触发则返回true
			}
		}

		return handled;//没有触发则返回false
	}

	//画线路的函数
	public void draw(android.graphics.Canvas canvas, MapView mapView,
			boolean shadow) {
		ArrayList<Point> stack = new ArrayList<Point>();//点的栈
		buildStack(mapView, stack);
		if (stack.size() > 0) {
			drawLines(canvas, mapView, stack);
			stack.clear();
		}
		for (StationOverlay ol : mOverlays) {
			ol.draw(canvas, mapView, shadow);//将站点画出来
		}
	}

	/*
	 * 将mapview加入到地图中
	 */
	public void addToMap(MapView mv) {
		this.mapView = mv;
		initOverlay(mapView);

		if (mapView.getOverlays().contains(this) == false) {//getOverlays获取Overlay列表。
			//列表中不包括则加入
			mapView.getOverlays().add(this);
		}
	}
	/*
	 * 将mapview从地图中移除
	 */
	public boolean removeFromMap(MapView mv) {
		boolean bremove = mv.getOverlays().remove(this);
		if (bremove) {
			closePopupWindow();
			this.mapView = null; // 当remove from map是，清空地图对象
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
	 * 初始化Paint
	 */
	private void initPaint() {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.rgb(54, 114, 227));
		mPaint.setAlpha(180);//改变颜色(),只有指定颜色的alpha值
		mPaint.setStrokeWidth(5.5f);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setAntiAlias(true);
	}
/*
 * 初始化图层
 */
	private void initOverlay(MapView mv) {
		if (boHasInit == true) {
			return; // 标志已经初始化过了，为了防止用于多次addtomap
		}

		mOverlays = new ArrayList<StationOverlay>();

		int count = busLine.getmStations().size();//getmStations返回途经车站描述。 
												//siez()返回此列表中的元素数。
		
		for (int i = 0; i < count; i++) {
			GeoPoint pt = busLine.getmStations().get(i).getmCoord();//get(i)返回此列表中指定位置上的元素。
															//getmCoord()  返回车站经纬度坐标。
							
			mOverlays.add(new StationOverlay(this, i, pt, resetBound(mContext
					.getResources().getDrawable(R.drawable.da_marker_red)),
					mMsgHandler));
		}

		boHasInit = true; // 标记已经初始化过了
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
				// 检查p0, p1两点是否距离很近
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
			ArrayList<Point> stack) {// 画线函数
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

		// 高分辨率
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
	 * 公交线路消息handler
	 */
	public interface BusLineMsgHandler {
		public boolean onStationClickEvent(MapView mapView,
				BusLineOverlay overlay, int index);
	}

	/*
	 * 消息handler
	 */
	class MsgHandler implements BusLineMsgHandler {
		public boolean onStationClickEvent(MapView mapView,BusLineOverlay overlay, int index) 
		{// 点击到公交站点的事件
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
	 * 消息窗口类
	 */

	static class InfoWindow {
		public static InfoWindow sActive = null;// 消息窗口的静态变量
		public static Drawable sDefaultBack = null;// 图片

		public static Bitmap bitmap9patch = null;

		protected MapView mMap;// 地图视图
		protected View mContent;// 视图
		protected GeoPoint mPoint;// 地理点
		protected long mOpenTime = -1;// 没有用到，具体还有待研究

		protected MapView.LayoutParams mLayout;// 布局参数

		// 构造函数1
		public InfoWindow(MapView view, View content, GeoPoint geo) {
			this(view, content, geo, null, null);
		}

		// 构造函数2
		public InfoWindow(MapView view, View content, GeoPoint geo,
				Drawable back, MapView.LayoutParams layout) {
			mMap = view;
			mContent = content;
			mPoint = geo;
			mLayout = layout;
			initBack(back);// 初始化图片

		}

		/*
		 * 初始化图片函数
		 */
		private void initBack(Drawable back) {// 初始化
			if (back == null) {// 如果传进来的图片是空的
				if (sDefaultBack == null) {// 默认图片也是空的
					initDefaultBack(mMap.getContext());
				}

				back = sDefaultBack;
				back.setAlpha(255);
			}
			mContent.setBackgroundDrawable(back);
		}

		/*
		 * 初始化默认图片
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
			// 解码图片

			sDefaultBack = new NinePatchDrawable(bitmap9patch, chunk, pad, null);
			// 创建绘图,

		}

		/*
		 * 关闭窗口资源
		 */

		public void closeWindowInfoRes() {
			if (bitmap9patch != null && bitmap9patch.isRecycled() == false) {
				bitmap9patch.recycle();
				bitmap9patch = null;
			}
		}

		/*
		 * 打开函数
		 */
		public void open() {
			if (isThisOpen()) {// 非空则证明已经打开了，所以返回
				return;
			}

			if (sActive != null) {// 非空说明已经有打开的了，则关闭已经打开的
				sActive.close();
			}

			sActive = this;// 把当前的赋值过去

			if (mLayout == null) {
				// 创建一个新的布局参数集合
				mLayout = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, mPoint, 25, 5,
						MapView.LayoutParams.BOTTOM
								| MapView.LayoutParams.RIGHT);
			}
			// 添加到地图视图中去
			mMap.addView(mContent, mLayout);
		}

		/*
		 * 关闭函数
		 */
		public void close() {
			if (!isThisOpen()) {
				return;
			}
			if (mMap == null) {// 没有地图视图，则返回，说明已经是空的了

				return;
			}
			// 否则就将所有的都置为空
			sActive = null;
			mMap.removeView(mContent);
		}

		/*
		 * 判断是否已经打开函数
		 */
		private boolean isThisOpen() {
			return sActive == this;
		}
	}
}

class StationOverlay {// 站点图层
	protected BusLineOverlay mOverlay;
	protected int mIndex;// 索引
	protected GeoPoint mPos;
	private Drawable mFlag;// 放置图片的地方
	private BusLineOverlay.BusLineMsgHandler mHandler;
	private boolean mHitDowm;// 是否点击标志位

	
	//构造函数
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
		// toPixels把给定的GeoPoint变换到相对于MapView左上角的屏幕像素坐标
	}

	protected GeoPoint pointToGeo(MapView mapView, Point pt) {
		return mapView.getProjection().fromPixels(pt.x, pt.y);
		// fromPixels该方法用给定的像素坐标创建一个新的GeoPoint
	}

	/*
	 * 画出标识物函数
	 */
	private void drawMarker(android.graphics.Canvas canvas, Drawable drawable,
			int x, int y) {
		Rect rect = drawable.getBounds();// 矩形
		// left top right bottom 矩形的四个坐标
		drawable.setBounds(rect.left + x, rect.top + y, rect.right + x,
				rect.bottom + y);
		drawable.draw(canvas);
		drawable.setBounds(rect.left - x, rect.top - y, rect.right - x,
				rect.bottom - y);
	}

	/*
	 * 绘画站点函数
	 */
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (mFlag == null || shadow) {
			return;
		}

		Point pt = geoToPoint(mapView, mPos);
		drawMarker(canvas, mFlag, pt.x, pt.y);
	}

	/*
	 * 点击函数
	 */
	private boolean hitTest(MapView map, int x, int y) {
		Point pt = geoToPoint(map, mPos);
		Rect rect = mFlag.getBounds();
		return rect.contains(x - pt.x, y - pt.y);
		// contains 返回 true if (x,y) is inside the rectangle.
	}

	public boolean onMouseEvent(MotionEvent event, MapView mapView) {
		// MotionEvent event即一个触摸事件。我们对屏幕的几乎所有操作都会触发事件，如点击、放开、滑动等。

		boolean handled = true;

		Point pt = new Point((int) event.getX(), (int) event.getY());// 触摸的点

		if (event.getAction() == MotionEvent.ACTION_DOWN // 在第一个点被按下时触发
				&& hitTest(mapView, pt.x, pt.y)) {
			mHitDowm = true;
		} else if (event.getAction() == MotionEvent.ACTION_UP && mHitDowm) {
			mHitDowm = false;// MotionEvent.ACTION_UP:当屏幕上唯一的点被放开时触发
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
