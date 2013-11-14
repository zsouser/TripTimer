package com.zsouser.triptracker;

import android.database.sqlite.*;
import android.content.Context;

public class SQLHelper extends SQLiteOpenHelper {
	public static String DB_NAME = "production.db";
	public SQLHelper(Context context) {
		super(context, DB_NAME, null, 1);
	}
	
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE routes ( "
				+ "id integer primary key, "
				+ "time integer not null, "
				+ "time_of_day integer not null, "
				+ "destination integer not null,"
				+ "FOREIGN KEY (destination) REFERENCES destinations(id))");
		database.execSQL("CREATE TABLE locations ( "
				+ "id integer primary key autoincrement, "
				+ "lat double not null, "
				+ "lng double not null, "
				+ "route integer not null, "
				+ "FOREIGN KEY (route) REFERENCES routes(id))");

		database.execSQL("CREATE TABLE destinations ( "
				+ "id integer primary key autoincrement, "
				+ "lat double not null, "
				+ "lng double not null, "
				+ "name varchar(50) not null)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {	}

}
