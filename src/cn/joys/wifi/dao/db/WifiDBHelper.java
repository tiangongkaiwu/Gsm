package cn.joys.wifi.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WifiDBHelper extends SQLiteOpenHelper {

	public WifiDBHelper(Context context) {
		super(context, "traffic.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table traffic(_id integer primary key autoincrement,mobile varchar(80),wifi varchar(80),date varchar(80),time varchar(80),month integer,day integer,flag integer)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
