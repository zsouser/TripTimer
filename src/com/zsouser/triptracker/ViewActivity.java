package com.zsouser.triptracker;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import android.app.Activity;
import android.view.Menu;
import android.database.*;
import android.database.sqlite.*;
import android.database.DatabaseUtils;
import android.content.Intent;

public class ViewActivity extends Activity {
	public ArrayList<Route> routes;
	public RadioGroup rg;
	public ListView list;
	public SQLHelper helper;
	public SQLiteDatabase db;
	public static final int SORT_DURATION = 0, SORT_TIMEOFDAY = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		helper = new SQLHelper(this);
		db = helper.getWritableDatabase();
		list = (ListView) findViewById(R.id.routeList);
		if (getIntent().hasExtra("id")) {
			getRoutes(SORT_DURATION);
			rg = (RadioGroup) findViewById(R.id.radio);
			rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId) {
					case R.id.radio0:
						getRoutes(SORT_DURATION);
						break;
					case R.id.radio1:
						getRoutes(SORT_TIMEOFDAY);
						break;
					}
				}
			});
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view, menu);
		return true;
	}
	
	
	public void onResume(Bundle savedInstanceState) {
		getRoutes(SORT_DURATION);
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
			Intent i = null;
			switch (item.getItemId()) {
			case R.id.retime:
				i = new Intent(ViewActivity.this,TimerActivity.class);
				Bundle extras = new Bundle();
				if (getIntent().hasExtra("id")) {
					extras.putInt("id", getIntent().getExtras().getInt("id"));
				}
				i.putExtras(extras);
				startActivity(i,extras);
				return true;
			case R.id.destinations:
				i = new Intent(ViewActivity.this, MainActivity.class);
				startActivity(i,null);
				finish();
				return true;
			case R.id.delete:
				if (getIntent().hasExtra("id")) {
					int id = getIntent().getExtras().getInt("id");
					Cursor c = db.rawQuery("SELECT id FROM routes WHERE destination = " + id, null);
					if (c.moveToFirst()) {
						db.execSQL("delete FROM locations WHERE route = " + c.getInt(0));
						
						while (c.moveToNext()) {
							db.execSQL("delete FROM locations WHERE route = " + c.getInt(0));
						}
					}
					db.execSQL("DELETE FROM destinations WHERE destinations.id = " + id);
					db.execSQL("DELETE FROM routes  WHERE routes.destination = " + id);
					i = new Intent(ViewActivity.this, MainActivity.class);
					startActivity(i,null);
					finish();
				} else Toast.makeText(this,(CharSequence)"No id",10).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	private void getRoutes(int sort) {
		routes = new ArrayList<Route>();
		String query = "select id, time, time_of_day FROM routes order by ";
		if (sort == SORT_DURATION) query = query + "time ASC";
		else if (sort == SORT_TIMEOFDAY) query = query + "time_of_day ASC";
		Cursor c = db.rawQuery(query,null);
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				routes.add(new Route(c.getInt(0),c.getLong(1),c.getLong(2)));
			}
		} else Toast.makeText(this,(CharSequence)"No routes",0).show();
		list.setAdapter(new ArrayAdapter<Route>(this,android.R.layout.simple_list_item_1,routes));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(ViewActivity.this,RouteActivity.class);
				Bundle extras = new Bundle();
				extras.putInt("route_id",routes.get(position).id);
				extras.putInt("destination_id", getIntent().getExtras().getInt("id"));
				i.putExtras(extras);
				startActivity(i,extras);
			}
		});
	}
}
