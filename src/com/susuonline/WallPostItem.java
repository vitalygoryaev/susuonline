package com.susuonline;

import java.util.Date;
import java.util.List;

import android.util.Log;

public class WallPostItem {
	String text;
	Date date;
	List<String> pictures;
	
	WallPostItem(String text, List<String> pictures, long timestamp) {
		try {
			this.text = text;
			this.pictures = pictures;
			this.date = new Date(timestamp);
		} catch (Exception e) {
			Log.d("exceptions", "exception in wall post item constructor: " + e.toString());
		}
	}
}
