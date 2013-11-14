package com.zsouser.triptracker;
import java.util.Date;

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


public class TimerActivity extends Activity {
	public SQLHelper helper;
	public SQLiteDatabase db;
	public GoogleMap map;
	public LocationManager manager;
	public LocationListener listener;
	public LatLng here = null, last = null, dest = null;
	public int currentRouteId, currentDestinationId;
	public long start_time, end_time;
	public Chronometer chron;
	public PolylineOptions opts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);
		Double lat, lng;
		helper = new SQLHelper(this);
		db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT id FROM routes ORDER BY id DESC LIMIT 1",null);
		if (c.moveToFirst())
			currentRouteId = c.getInt(0) + 1;
		else currentRouteId = 1;
		if (getIntent().hasExtra("id")) {
			
			currentDestinationId = getIntent().getExtras().getInt("id");
			Cursor c1 = db.rawQuery("SELECT lat, lng FROM destinations WHERE id = "+currentDestinationId,null);
			if (c1.moveToFirst()) {
				lat = c1.getDouble(0);
				lng = c1.getDouble(1);
			} else {
				lat = null;
				lng = null;
			}
		} else if (getIntent().hasExtra("lat") && getIntent().hasExtra("lng")) {
			
			lat = getIntent().getExtras().getDouble("lat");
			lng = getIntent().getExtras().getDouble("lng");
		} else {
			lat = null;
			lng = null;
		}
		if (lat != null && lng != null) {
			dest = new LatLng(lat,lng);
			
			Cursor c2 = db.rawQuery("SELECT id FROM routes ORDER BY id DESC LIMIT 1",null);
			if (c2.moveToFirst())
				currentRouteId = c2.getInt(0) + 1;
			else currentRouteId = 1;
			
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			opts = new PolylineOptions();
			manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			map.addMarker(new MarkerOptions().position(dest));
			chron = (Chronometer) findViewById(R.id.chronometer);
			chron.start();
			start_time = System.currentTimeMillis();
			
			listener = new LocationListener() {
				public void onLocationChanged(Location location) {
					last = here;
					here = new LatLng(location.getLatitude(),location.getLongitude());
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(here,15));
					if (last == null) {
						map.addMarker(new MarkerOptions().position(here));
					} else {
						map.addPolyline(new PolylineOptions().add(last,here).color(Color.RED).width(10));
					}
					db.execSQL("INSERT INTO locations (lat, lng, route) VALUES (?1,?2,?3)", new String[] {""+location.getLatitude(),""+location.getLongitude(),""+currentRouteId});
					float[] results = new float[3];
					Location.distanceBetween(here.latitude, here.longitude, dest.latitude, dest.longitude, results);
					if (results[0] < 100) {
						
						chron.stop();
						manager.removeUpdates(this);
						Toast.makeText(getApplication(),(CharSequence)"You have arrived! Saving your route...",10).show();
						long time = System.currentTimeMillis() - start_time;
						long timeOfDay = System.currentTimeMillis();
						db.execSQL("INSERT INTO routes (time, time_of_day, destination) VALUES (?1,?2,?3)", new String[] {""+time,""+timeOfDay,""+currentDestinationId});
						Intent i = new Intent(TimerActivity.this, RouteActivity.class);
						Bundle extras = new Bundle();
						extras.putInt("route_id",currentRouteId);
						extras.putInt("destination_id",currentDestinationId);
						i.putExtras(extras);
						startActivity(i,extras);
					}
					
				}
				public void onStatusChanged(String provider, int status, Bundle extras) { }
				public void onProviderEnabled(String provider) {
					Toast.makeText(getApplication(),(CharSequence)("Provider " + provider + " enabled"),0).show();
				}
				public void onProviderDisabled(String provider) {
					Toast.makeText(getApplication(),(CharSequence)("Provider " + provider + " enabled"),0).show();
				}
			};
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			
		} else {
			Toast.makeText(this,(CharSequence)"An error occurred. No coordinates received.",25).show();
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel:
			db.execSQL("DELETE FROM locations WHERE route = " + currentRouteId);
			manager = null;
			listener = null;
			finish();
			return true;
		default:
			return true;
		}
	}

}
