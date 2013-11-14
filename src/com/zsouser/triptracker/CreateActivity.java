package com.zsouser.triptracker;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.app.*;
import android.view.Menu;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.location.LocationClient;

import android.content.Intent;
import android.location.*;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.Color;
import android.app.Activity;
import android.view.Menu;

public class CreateActivity extends Activity {
	public GoogleMap map;
	public LatLng here;
	public EditText title;
	public LocationManager manager;
	public LocationListener listener;
	public SQLiteDatabase db;
	public SQLHelper helper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper = new SQLHelper(this);
		db = helper.getWritableDatabase();
		setContentView(R.layout.activity_create);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		title = (EditText) findViewById(R.id.title);
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
			public void onLocationChanged(Location location) {
				here = new LatLng(location.getLatitude(),location.getLongitude());
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(here,15));
				map.addMarker(new MarkerOptions().position(here).flat(true));
				manager.removeUpdates(this);
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) { }
			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplication(),(CharSequence)("Provider " + provider + " enabled"),0).show();
			}
			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplication(),(CharSequence)("Provider " + provider + " enabled"),0).show();
			}
		});
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				Toast.makeText(getApplication(),(CharSequence)"Saving Destination",0).show();
				String name = title.getText().toString();
				if (name == null || name.isEmpty()) {
					Toast.makeText(getApplication(),(CharSequence)"Please enter a name",0).show();
				} else {
					double lat = point.latitude;
					double lng = point.longitude;
				
					db.execSQL("INSERT INTO destinations (name, lat, lng) VALUES (?1,?2,?3)", new String[] {name,""+lat,""+lng});
					Bundle extras = new Bundle();
					extras.putDouble("lat",point.latitude);
					extras.putDouble("lng",point.longitude);
					Intent i = new Intent(CreateActivity.this, TimerActivity.class);
					i.putExtras(extras);
					startActivity(i,extras);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
