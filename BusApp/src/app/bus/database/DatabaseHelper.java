package app.bus.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;



public class DatabaseHelper {
	
	public BusLine searchBusLine(String busLineName){
		//List<BusStation> busStationList = new List();
		BusLine busLine = new BusLine();
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/xian.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		//Cursor cur = mydb.query("cnbusw", null, "busw=?",new String[]{busLineName},null,null,null);
		Cursor cur = mydb.rawQuery("select * from cnbusw where busw like ?", new String[]{"%"+busLineName+"%"});
		if(cur!=null){
			if(cur.moveToFirst()){
				busLine.setBusLineID(cur.getInt(0));				
				busLine.setBusLineName(cur.getString(1));
				busLine.setBusLineNote(cur.getString(2));
				busLine.setStationList(searchBusLineStation(busLine.getBusLineID().toString()));
			}
		}
		
		return busLine;
	}
    
	public String searchBusLineByName(Integer id){
		String busLineName ="";
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/xian.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cur = mydb.rawQuery("select * from cnbusw where id = ? ",new String[]{id.toString()});
		if(cur!=null){
			if(cur.moveToFirst()){
				busLineName = cur.getString(1);
			}
		}
		return busLineName;
	}
	
	public ArrayList<Station> searchBusStation(String busStationName){
		ArrayList<Station> stationList = new ArrayList<Station>();
		ArrayList<BusStation> busStationList = new ArrayList();
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/xian.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cur = mydb.rawQuery("select * from cnbus where kind = ? and zhan like ?", new String[]{"1","%"+busStationName+"%"});
		if(cur != null)
        {
            if(cur.moveToFirst())
            {
            	Station stationTemp = new Station();          	  
                stationTemp.setLongitude(cur.getString(6));
                stationTemp.setLatitude(cur.getString(7));
                stationTemp.setStationName(cur.getString(2));
                stationTemp.addBusLine(searchBusLineByName(cur.getInt(0)));
                stationList.add(stationTemp);
                cur.moveToNext();
               do{
            	   boolean flag = true;
            	   for(int i =0;i<stationList.size();i++){
            		   if(cur.getString(6).equalsIgnoreCase(stationList.get(i).getLongitude()))
            		   {
            			   flag = false;
                           stationList.get(i).addBusLine(searchBusLineByName(cur.getInt(0)));
            		   }		   
            	   }
            	   if(flag)
            	   {
            		   Station temp = new Station();          	  
                       temp.setLongitude(cur.getString(6));
                       temp.setLatitude(cur.getString(7));
                       temp.setStationName(cur.getString(2));
                       temp.addBusLine(searchBusLineByName(cur.getInt(0)));
                       stationList.add(temp);
            	   }
/*            	                                       
                   BusStation busStationTemp = new BusStation();
                   busStationTemp.setStationNum(Integer.parseInt(cur.getString(1))); 
                   busStationTemp.setLongitude(cur.getString(6));
                    busStationTemp.setLatitude(cur.getString(7));
                    busStationTemp.setStationName(cur.getString(2));
                    busStationTemp.setBusLine(searchBusLineByName(cur.getInt(0)));
                    busStationList.add(busStationTemp);*/
                  }while(cur.moveToNext());
            }
        }
		cur.close();
		mydb.close();
		return stationList;
	}
	
	public ArrayList<Station> searchNearbyBusStation(Double la,Double lon){
		ArrayList<Station> stationList = new ArrayList<Station>();
		ArrayList<BusStation> busStationList = new ArrayList();
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/xian.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Double left = lon-0.01;
		Double right = lon+0.01;
		Double left2 = la-0.01;
		Double right2 = la+0.01;
		
		String query="select * from cnbus  where kind = ? and xzhanbd between "+left+" and "+right+" and yzhanbd between "+left2+" and "+right2+"";
		Log.e("query", query);
		Cursor cur = mydb.rawQuery(query,new String[]{"1"});
		if(cur != null)
        {
            if(cur.moveToFirst())
            {
            do{	Station stationTemp = new Station();          	  
                stationTemp.setLongitude(cur.getString(6));
                stationTemp.setLatitude(cur.getString(7));
                stationTemp.setStationName(cur.getString(2));
                stationTemp.addBusLine(searchBusLineByName(cur.getInt(0)));
                stationList.add(stationTemp);
            }while(cur.moveToNext());
           }
        }
		 cur.close();
		mydb.close();
		return stationList;
	}
	    
	public ArrayList<BusStation> searchBusLineStation(String busLineID){
		ArrayList<BusStation> busStation = new ArrayList();
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/xian.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cur = mydb.rawQuery("select * from cnbus where kind = ? and xid=? order by pm", new String[]{"1",busLineID});
		if(cur != null)
        {
            if(cur.moveToFirst())
            {
               do{
            	   BusStation busStationTemp = new BusStation();
                    busStationTemp.setStationNum(Integer.parseInt(cur.getString(1)));
                    busStationTemp.setLongitude(cur.getString(6));
                    busStationTemp.setLatitude(cur.getString(7));
                    busStationTemp.setStationName(cur.getString(2));
                    busStation.add(busStationTemp);
                  }while(cur.moveToNext());
            }
        }
		return busStation;
	}
	public Boolean addLineCollection(String name){
		
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/user.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
		//String sql = "create table linecollection(_id INTEGER PRIMARY KEY,linename varchar(100))";
		//mydb.execSQL(sql);
		if(!searchLineCollectionItem(name))
		{
			ContentValues values = new ContentValues();//通过ContentValues对象来传入参数
			values.put("linename", name);
			mydb.insert("linecollection", null, values);
			Log.i("addCollection","success");
		}
		
	    return true;
	}
	
	public ArrayList<String> getLineCollection(){
		ArrayList<String> lineCollectionList = new ArrayList<String>();
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/user.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cur = mydb.rawQuery("select * from linecollection",null);
		
		if(cur != null&&cur.moveToFirst())
        {
			do{
				String temp = cur.getString(1);
            	lineCollectionList.add(temp);
            	}while(cur.moveToNext());	
         }       
            
		return lineCollectionList;
	}
	
	public Boolean deleteLineCollectionItem(String name){
		Boolean temp = true;
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/user.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
		mydb.delete("linecollection", "linename=?", new String[]{name});
		return temp;
	}
	
	public Boolean searchLineCollectionItem(String name){
		Boolean temp = false;
		SQLiteDatabase mydb = null;
		String PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/app.bus.activity"
            + "/user.db";
		mydb = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
		Cursor cur = mydb.rawQuery("select * from linecollection where linename = '"+name+"'",null);		
		if(cur != null&&cur.moveToFirst())
        {
			temp=true;
         }       
		return temp;
		
		
	}
}
