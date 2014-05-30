package com.susuonline;

import android.util.Log;

public class ClassItem {
	String beginTime;
	String building;
	String endTime;
	String eventType;
	String firstName;
	String id;
	String instructorId;
	String lastName;
	String middleName;
	int pairNumber;
	String room;
	int subGroupNumber;
	String subject;
	int termNumber;
	int termPart;
	int weekDay;
	int weekNumber;
	
	ClassItem(
		String beginTime,
		String building,
		String endTime,
		String eventType,
		String firstName,
		String id,
		String instructorId,
		String lastName,
		String middleName,
		String pairNumber,
		String room,
		String subGroupNumber,
		String subject,
		String termNumber,
		String termPart,
		String weekDay,
		String weekNumber
	) {
		try {
			this.beginTime = beginTime;
			this.building = building;
			this.endTime = endTime;
			this.eventType = eventType;
			this.firstName = firstName;
			this.id = id;
			this.instructorId = instructorId;
			this.lastName = lastName;
			this.middleName = middleName;
			this.pairNumber = Integer.parseInt(pairNumber);
			this.room = room;
			this.subGroupNumber = Integer.parseInt(subGroupNumber);
			this.subject = subject;
			this.termNumber = Integer.parseInt(termNumber);
			this.termPart = Integer.parseInt(termPart);
			this.weekDay = Integer.parseInt(weekDay);
			this.weekNumber = Integer.parseInt(weekNumber);
		} catch (NumberFormatException e) {
			Log.d("exceptions", "cannot parse integer in classItem");
		}
	}
}
