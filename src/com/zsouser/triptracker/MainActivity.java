package com.zsouser.triptracker;

import android.os.Bundle;
import android.location.*;
import android.database.sqlite.*;
import android.database.Cursor;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.view.Menu;

import java.util.Date;

import android.database.DatabaseUtils;

import java.util.ArrayList;
import java.util.List;

import android.widget.*;
import android.view.*;
import android.content.*;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.*;

public class MainActivity extends Activity {
	SQLHelper helper;
	SQLiteDatabase db;
	public static final int SORT_DATE = 0;
	public static final int SORT_TIME = 1;
	public static int SORT_FLAG;
	protected double lat, lng;
	protected Context me;
	protected Location here, dest;
	protected int currentRouteId;
	protected int currentTripId;
	protected long start_time;
	protected ListView list;
	protected LocationListener listener;
	protected LocationManager manager;
	protected Button button;
	protected List<Destination> routes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		me = this;
		helper = new SQLHelper(this);
		db = helper.getWritableDatabase();
		list = (ListView) findViewById(R.id.destinationsList);
		getDestinations();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			Intent i2 = new Intent(MainActivity.this, CreateActivity.class);
			startActivity(i2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void deleteDestination(int id) {
		db.execSQL("DELETE FROM locations WHERE route IN (SELECT id FROM routes WHERE destination = "+id+")");
		db.execSQL("DELETE FROM routes WHERE destination = "+id);
		db.execSQL("DELETE FROM destinations WHERE id = "+id);
	}
	
	public void onResume(Bundle savedInstanceState) {
		getDestinations();
	}
	
	
	private void getDestinations() {
		routes = new ArrayList<Destination>();
		Cursor c = db.rawQuery("SELECT destinations.name, destinations.id, avg(routes.time) "
				+ "FROM destinations, routes "
				+ "GROUP BY destinations.id",null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				routes.add(new Destination(c.getString(0),c.getInt(1),c.getInt(2)));
			}
		} else Toast.makeText(this,(CharSequence)"No routes",0).show();
		list.setAdapter(new ArrayAdapter<Destination>(this,android.R.layout.simple_list_item_1,routes));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(MainActivity.this,ViewActivity.class);
				Bundle extras = new Bundle();
				extras.putInt("id",routes.get(position).id);
				i.putExtras(extras);
				startActivity(i,extras);
			}
		});
	}
}
	


