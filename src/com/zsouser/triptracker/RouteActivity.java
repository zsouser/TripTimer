package com.zsouser.triptracker;

import android.os.Bundle;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.view.Menu;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;

import android.content.Intent;
import android.location.*;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.Color;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.view.Menu;

public class RouteActivity extends Activity {
	public SQLiteDatabase db;
	public GoogleMap map;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		SQLHelper helper = new SQLHelper(this);
		db = helper.getWritableDatabase();
		if (getIntent().hasExtra("route_id")) {
			Cursor c1 = db.rawQuery("SELECT time, time_of_day FROM routes WHERE id = " + getIntent().getExtras().getInt("route_id"), null);
			TextView timeText = (TextView) findViewById(R.id.time);
			TextView timeOfDayText = (TextView) findViewById(R.id.time_of_day);
			if (c1.moveToFirst()) {
				timeText.setText(timeText.getText() + " " + makeTime(c1.getLong(0)));
				timeOfDayText.setText(timeOfDayText.getText() + " " + new java.util.Date(c1.getLong(1)).toString());
			}
			Cursor c2 = db.rawQuery("SELECT lat, lng FROM locations WHERE route = "+getIntent().getExtras().getInt("route_id"),null);
			PolylineOptions opts = new PolylineOptions();
			
			if (c2.moveToFirst()) {
				LatLng current = new LatLng(c2.getDouble(0),c2.getDouble(1));

				map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,12));
				if (c2.getCount() == 1) {
					map.addMarker(new MarkerOptions().position(new LatLng(c2.getDouble(0),c2.getDouble(1))));
				}
				if (c2.getCount() > 1)
				while (c2.moveToNext()) {
					LatLng last = current;
					current = new LatLng(c2.getDouble(0),c2.getDouble(1));
					opts.add(last,current);
				}
				map.addPolyline(opts.width(5).color(Color.RED));
			} else {
				Toast.makeText(this,(CharSequence)"No locations found",10).show();
			}
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete:
			if (getIntent().hasExtra("route_id") && getIntent().hasExtra("destination_id")) {
				db.execSQL("DELETE FROM routes WHERE id = "+getIntent().getExtras().getInt("route_id"));
				db.execSQL("DELETE FROM locations WHERE route = "+getIntent().getExtras().getInt("route_id"));
				Intent i = new Intent(RouteActivity.this, ViewActivity.class);
				Bundle extras = new Bundle();
				extras.putInt("id", getIntent().getExtras().getInt("destination_id"));
				i.putExtras(extras);
				startActivity(i,extras);
				finish();
				return true;
			} else {
				Toast.makeText(this,(CharSequence)"Error: No route to delete",1).show();
				return true;
			}
		case R.id.back:
			Intent i = new Intent(RouteActivity.this, ViewActivity.class);
			Bundle extras = new Bundle();
			extras.putInt("id", getIntent().getExtras().getInt("destination_id"));
			i.putExtras(extras);
			startActivity(i,extras);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	private String makeTime(long milliseconds) {
		if (milliseconds < 1000) return "Less than a second";
		long seconds = (long) (milliseconds / 1000) % 60 ;
		long minutes = (long) ((milliseconds / (1000*60)) % 60);
		long hours   = (long) ((milliseconds / (1000*60*60)) % 24);
		String retVal = "";
		if (hours > 0) retVal = retVal + hours + " hours ";
		if (minutes > 0) retVal = retVal + minutes + " minutes ";
		if (seconds > 0) retVal = retVal + seconds + " seconds";
		return retVal;
	}
}
