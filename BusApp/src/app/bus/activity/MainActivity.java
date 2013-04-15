package app.bus.activity;

import static app.bus.commons.Commons.*;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import app.bus.database.DBtool;

public class MainActivity extends TabActivity {
	/** Called when the activity is first created. */
	private TabHost m_tabHost;
	private LayoutInflater mLayoutInflater;
	public DBtool dbtool;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		dbtool = new DBtool(this);
		dbtool.openDatabase();
		dbtool.closeDatabase();
		inti();

	}

	private void inti() {
		m_tabHost = getTabHost();
		mLayoutInflater = LayoutInflater.from(this);
		int count = mTabClassArray.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = m_tabHost.newTabSpec(mTextviewArray[i])
					.setIndicator(getTabItemView(i))
					.setContent(getTabItemIntent(i));
			m_tabHost.addTab(tabSpec);
			m_tabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.selector_tab_background);
		}
	}

	private Intent getTabItemIntent(int i) {
		Intent intent = new Intent(this, mTabClassArray[i]);
		return intent;
	}

	private View getTabItemView(int i) {
		View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);

		if (imageView != null) {
			// imageView.setImageResource(mImageViewArray[i]);
		}

		TextView textView = (TextView) view.findViewById(R.id.textview);

		textView.setText(mTextviewArray[i]);
		return view;
	}

}