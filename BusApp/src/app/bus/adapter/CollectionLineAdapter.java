package app.bus.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import app.bus.activity.LineInfoActivity;
import app.bus.activity.R;
import app.bus.database.DatabaseHelper;

public class CollectionLineAdapter extends  BaseAdapter{
	
	Context context;
	ArrayList<String>collectionList;

	
	public CollectionLineAdapter(Context context,
			ArrayList<String> collectionList) {
		super();
		this.context = context;
		this.collectionList = collectionList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return collectionList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return collectionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = LayoutInflater.from(context).inflate(R.layout.collectionlistitem, null);
		TextView name = (TextView) convertView.findViewById(R.id.collectionlineitem);
		name.setText(collectionList.get(position));
		ImageButton searchCollectionLine = (ImageButton) convertView.findViewById(R.id.searchlineitem);
		searchCollectionLine.setOnClickListener(search);
		searchCollectionLine.setTag(collectionList.get(position));
		ImageButton deleteCollectionLine = (ImageButton) convertView.findViewById(R.id.deletelineitem);
		deleteCollectionLine.setOnClickListener(delete);
		deleteCollectionLine.setTag(collectionList.get(position));
		return convertView;
	}
	
	 private OnClickListener search = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,LineInfoActivity.class);
				intent.putExtra("busLineName", v.getTag().toString());
				
			    context.startActivity(intent);
				
			}
		};
		private OnClickListener delete= new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHelper dbHelper = new DatabaseHelper();
				dbHelper.deleteLineCollectionItem( v.getTag().toString());
				
			}
		};
	
	

}
