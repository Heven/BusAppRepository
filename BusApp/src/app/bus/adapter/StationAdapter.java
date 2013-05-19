package app.bus.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import app.bus.activity.LineInfoActivity;
import app.bus.activity.MyGridView;
import app.bus.activity.R;
import app.bus.database.Station;


public class StationAdapter  extends BaseExpandableListAdapter implements
OnItemClickListener{
	public static final int ItemHeight = 30;// 每项的高度
	public static final int PaddingLeft = 40;// 每项的高度
	private int myPaddingLeft = 0;
	private MyGridView toolbarGrid;
	private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

	private Context parentContext;
	ArrayList<Station> stationList;
	
	private LayoutInflater layoutInflater;

	static public class TreeNode
	{
		private Object parent;
		private List<Object> childs = new ArrayList<Object>();
		public void setChilds(List<Object> childs) {
			this.childs = childs;
		}
		public List<Object> getChilds() {
			return childs;
		}
		public void setParent(Object parent) {
			this.parent = parent;
		}
		public Object getParent() {
			return parent;
		}
	}

	public StationAdapter(ArrayList<Station> stationList, Context view, int myPaddingLeft)
	{
		parentContext = view;
		this.myPaddingLeft = myPaddingLeft;
		this.stationList = stationList;
	}

	public List<TreeNode> GetTreeNode()
	{
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes)
	{
		treeNodes = nodes;
	}

	public void RemoveAll()
	{
		treeNodes.clear();
	}

	public Object getChild(int groupPosition, int childPosition)
	{
		return treeNodes.get(groupPosition).getChilds().get(childPosition);
	}

	public int getChildrenCount(int groupPosition)
	{
		return treeNodes.get(groupPosition).getChilds().size();
	}

	static public TextView getTextView(Context context)
	{
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		return textView;
	}

	/**
	 * 可自定义ExpandableListView
	 */
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			String result = "";
			//convertView = LayoutInflater.from(context).inflate(R.layout.station_info_item, null);
			Station temp = stationList.get(groupPosition);
			Vector vet = new Vector();
			Vector vet_img = new Vector();
			layoutInflater = (LayoutInflater) parentContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.station_view, null);
			toolbarGrid = (MyGridView) convertView
			.findViewById(R.id.GridView_toolbar);
			toolbarGrid.setNumColumns(4);// 设置每行列数
			toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
			toolbarGrid.setHorizontalSpacing(0);// 水平间隔
			//TextView list = (TextView) convertView.findViewById(R.id.station_list);
			for(int i=0;i<temp.getBusLine().size();i++){
				result = temp.getBusLine().get(i).toString();
				vet.add(result);
				vet_img.add(R.drawable.stationimage_2);
			}		
			int menu_toolbar_image_array[] = { };
			
			
			toolbarGrid.setAdapter(getMenuAdapter(vet,
					vet_img));// 设置菜单Adapter
			toolbarGrid.setOnItemClickListener(this);
		}
		return convertView;
	}

	/**
	 * 可自定义list
	 */
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		TextView textView = getTextView(this.parentContext);
		textView.setText(getGroup(groupPosition).toString());
		textView.setPadding(myPaddingLeft + PaddingLeft, 0, 0, 0);
		return textView;
	}

	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	public Object getGroup(int groupPosition)
	{
		return treeNodes.get(groupPosition).getParent();
	}

	public int getGroupCount()
	{
		return treeNodes.size();
	}

	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	public boolean hasStableIds()
	{
		return true;
	}

	/**
	 * 构造菜单Adapter
	 * 
	 * @param vet
	 *            名称
	 * @param vet_img
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter getMenuAdapter(Vector vet,         
			Vector vet_img)               //将静态数据映射到XML文件中定义好的视图
	{
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < vet.size(); i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", vet_img.get(i));
			map.put("itemText", vet.get(i));
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(parentContext, data,
				R.layout.station_item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		
		HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);  
		String busLine = map.get("itemText");  
        //跳转
		Intent intent = new Intent(parentContext,LineInfoActivity.class);
		intent.putExtra("busLineName", busLine);
		parentContext.startActivity(intent);

	}

	
	}

