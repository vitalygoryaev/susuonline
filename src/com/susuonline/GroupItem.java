package com.susuonline;

import android.util.Log;

public class GroupItem {
	int courseNumber;
	int groupNumber;
	String id;
	String studyForm;
	
	GroupItem(String courseNumber, String groupNumber, String id, String studyForm) {
		try {
			this.courseNumber = Integer.parseInt(courseNumber);
			this.groupNumber = Integer.parseInt(groupNumber);
			this.id = id;
			this.studyForm = studyForm;
		} catch (NumberFormatException e) {
			Log.d("exceptions", "cannot parse courseNumber or groupNumber from: " + courseNumber + " OR " + groupNumber);
		}
	}
}
