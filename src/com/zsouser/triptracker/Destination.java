package com.zsouser.triptracker;
import android.database.sqlite.*;
import android.database.*;
public class Destination {
	public final String name;
	public final int id;
	public final double avg;
	public Destination(String name, int id, double avg) {
		this.name = name;
		this.id = id;
		this.avg = avg;
		
	}
	
	public String toString() {
		return this.name + " - " + makeTime((long)this.avg);
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
