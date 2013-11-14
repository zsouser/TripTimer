package com.zsouser.triptracker;

public class Route {
	public final long time, time_of_day;
	public final int id;
	
	public Route(int id, long t, long tod) {
		time = t; 
		this.id = id;
		time_of_day = tod;
	}
	
	public String toString() {
		java.util.Date date = new java.util.Date(time_of_day);
		String t = makeTime(time);
		return date.toString() + " \n " + t;
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
