package app.bus.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
 
import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBtool {
	private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME = "xian.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "app.bus.activity";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //在手机里存放数据库的位置
 
    private SQLiteDatabase database;
    private SQLiteDatabase userDatabase;
    private Context context;
    public static final String USER_DB = "user.db";
 
    public DBtool(Context context) {
        this.context = context;
    }
 
    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
        //建立用户数据库
        if(!new File(DB_PATH + "/" + USER_DB).exists()){
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + USER_DB,
                null);
       //收藏夹表格建立; 最好不要硬写代码，用sql文件的格式比较好。
        String sql = "create table linecollection(_id INTEGER PRIMARY KEY,linename varchar(100))";
 		//String sql = "drop table linecollection";
         try{
        	 db.execSQL(sql);
        	 Log.i("collection","createTable");
        	 }
         catch(SQLException e)
 		 {
        	 Log.i("collection","createTableFailed");
        	 
 		 }
         
        }
        this.userDatabase = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + USER_DB,null);
         
    }
 
    private SQLiteDatabase openDatabase(String dbfile) {
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
            	//FileInputStream is = new FileInputStream("../assets/busstation.db"); //欲导入的数据库
                AssetManager am = context.getAssets();
                InputStream is = am.open("xian.db");
            	FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }
    
    public void closeDatabase() {
        this.database.close();
        this.userDatabase.close();
    }

}
