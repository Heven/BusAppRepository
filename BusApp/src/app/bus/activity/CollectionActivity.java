package app.bus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import app.bus.adapter.CollectionLineAdapter;
import app.bus.database.DatabaseHelper;

public class CollectionActivity extends Activity{
	
	private ListView collectionList ;
	private CollectionLineAdapter collectionLineAdapter;
public void onCreate(Bundle savedInstanceState){
		
	
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection);
		collectionList = (ListView) findViewById(R.id.collectionlistview);
		DatabaseHelper dbHelper = new DatabaseHelper();	
		collectionLineAdapter = new CollectionLineAdapter(CollectionActivity.this, dbHelper.getLineCollection());
		collectionList.setAdapter(collectionLineAdapter);
	}

}

