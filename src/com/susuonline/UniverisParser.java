package com.susuonline;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class UniverisParser {
	public List<FacultyItem> parseFaculties(String input) {
		List<FacultyItem> facultyList = new ArrayList<FacultyItem>();
		
		//parsing input string
		try {
			//check for null input
			if (input == null)
				throw new Exception("input string for parsing is null");
			
			//actual parsing by finding { and }
			int start = 0;
			int end = 0;
			
			//while there is another faculty
			while (input.indexOf("}", end + 1) != -1) {
				start = input.indexOf("{", end + 1) + 1;
				end = input.indexOf("}", start + 1);
				String facultyString = input.substring(start, end);
			//Log.d("parsing", "start: " + start + "\nend: " + end + "\nfaculty string: " + facultyString);
				
				//get id
				int itemStart = facultyString.indexOf("\"Id\":\"") + "\"Id\":\"".length();
				int itemEnd = facultyString.indexOf("\"", itemStart);
				String id = facultyString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nid string: " + id);
			
				//get name
				itemStart = facultyString.indexOf("\"Name\":\"") + "\"Name\":\"".length();
				itemEnd = facultyString.indexOf("\"", itemStart);
				String name = facultyString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nname string: " + name);
				
				//get short name
				itemStart = facultyString.indexOf("\"ShortName\":\"") + "\"ShortName\":\"".length();
				itemEnd = facultyString.indexOf("\"", itemStart);
				String shortName = facultyString.substring(itemStart, itemEnd);
				
			//Log.d("parsing", "id: " + id + "\nname: " + name + "\nshortName: " + shortName);
				
				//creating FacultyItem
				FacultyItem faculty = new FacultyItem(id, name, shortName);
				facultyList.add(faculty);
			}
			
			
				
		} catch (Exception e) {
			Log.d("exceptions", "error parsing faculties" + e.toString());
		}
		
		return facultyList;
	}
	
	public List<GroupItem> parseGroups(String input) {
		List<GroupItem> groupList = new ArrayList<GroupItem>();
		
		//parsing input string
		try {
			//check for null input
			if (input == null)
				throw new Exception("input string for parsing is null");
			
			//actual parsing by finding { and }
			int start = 0;
			int end = 0;
			
			//while there is another group
			while (input.indexOf("}", end + 1) != -1) {
				start = input.indexOf("{", end + 1) + 1;
				end = input.indexOf("}", start + 1);
				String groupString = input.substring(start, end);
			//Log.d("parsing", "start: " + start + "\nend: " + end + "\nfaculty string: " + facultyString);
				
				//get course number
				int itemStart = groupString.indexOf("\"CourseNumber\":") + "\"CourseNumber\":".length();
				int itemEnd = groupString.indexOf(",", itemStart);
				String courseNumber = groupString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nid string: " + id);
			
				//get group number
				itemStart = groupString.indexOf("\"GroupNumber\":") + "\"GroupNumber\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String groupNumber = groupString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nname string: " + name);
				
				//get id
				itemStart = groupString.indexOf("\"Id\":\"") + "\"Id\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String id = groupString.substring(itemStart, itemEnd);
				
				//get study form
				itemStart = groupString.indexOf("\"StudyForm\":\"") + "\"StudyForm\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String studyForm = groupString.substring(itemStart, itemEnd);
				
			//Log.d("parsing", "id: " + id + "\nname: " + name + "\nshortName: " + shortName);
				
				//creating FacultyItem
				GroupItem group = new GroupItem(courseNumber, groupNumber, id, studyForm);
				groupList.add(group);
			}
			
			
				
		} catch (Exception e) {
			Log.d("exceptions", "error parsing groups" + e.toString());
		}
		
		return groupList;
	}
	
	public List<ClassItem> parseSchedule(String input) {
		List<ClassItem> schedule = new ArrayList<ClassItem>();
		
		//parsing input string
		try {
			//check for null input
			if (input == null)
				throw new Exception("input string for parsing is null");
			
			//actual parsing by finding { and }
			int start = 0;
			int end = 0;
			
			//while there is another class
			while (input.indexOf("}", end + 1) != -1) {
				start = input.indexOf("{", end + 1) + 1;
				end = input.indexOf("}", start + 1);
				String groupString = input.substring(start, end);
			//Log.d("parsing", "start: " + start + "\nend: " + end + "\nfaculty string: " + groupString);
				
				//get begin time
				int itemStart = groupString.indexOf("\"BeginTime\":\"") + "\"BeginTime\":\"".length();
				int itemEnd = groupString.indexOf("\"", itemStart);
				String beginTime = groupString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nid string: " + id);
			
				//get building
				itemStart = groupString.indexOf("\"Building\":\"") + "\"Building\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String building = groupString.substring(itemStart, itemEnd);
			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nname string: " + name);
				
				//get end time
				itemStart = groupString.indexOf("\"EndTime\":\"") + "\"EndTime\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String endTime = groupString.substring(itemStart, itemEnd);
				
				//get event type
				itemStart = groupString.indexOf("\"EventType\":\"") + "\"EventType\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String eventType = groupString.substring(itemStart, itemEnd);
				
				//get first name
				itemStart = groupString.indexOf("\"FirstName\":\"") + "\"FirstName\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String firstName = groupString.substring(itemStart, itemEnd);
				
				//get id
				itemStart = groupString.indexOf("\"Id\":\"") + "\"Id\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String id = groupString.substring(itemStart, itemEnd);
				
				//get instructor id
				itemStart = groupString.indexOf("\"InstructorId\":\"") + "\"InstructorId\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String instructorId = groupString.substring(itemStart, itemEnd);
				
				//get last name
				itemStart = groupString.indexOf("\"LastName\":\"") + "\"LastName\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String lastName = groupString.substring(itemStart, itemEnd);
				
				//get middle name
				itemStart = groupString.indexOf("\"MiddleName\":\"") + "\"MiddleName\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String middleName = groupString.substring(itemStart, itemEnd);
				
				//get pair number
				itemStart = groupString.indexOf("\"PairNumber\":") + "\"PairNumber\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String pairNumber = groupString.substring(itemStart, itemEnd);
				
				//get room
				itemStart = groupString.indexOf("\"Room\":\"") + "\"Room\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String room = groupString.substring(itemStart, itemEnd);
				
				//get sub group number
				itemStart = groupString.indexOf("\"SubGroupNumber\":") + "\"SubGroupNumber\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String subGroupNumber = groupString.substring(itemStart, itemEnd);
				
				//get subject
				itemStart = groupString.indexOf("\"Subject\":\"") + "\"Subject\":\"".length();
				itemEnd = groupString.indexOf("\"", itemStart);
				String subject = groupString.substring(itemStart, itemEnd);
				
				//get term number
				itemStart = groupString.indexOf("\"TermNumber\":") + "\"TermNumber\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String termNumber = groupString.substring(itemStart, itemEnd);
				
				//get term part
				itemStart = groupString.indexOf("\"TermPart\":") + "\"TermPart\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String termPart = groupString.substring(itemStart, itemEnd);
				
				//get week day
				itemStart = groupString.indexOf("\"WeekDay\":") + "\"WeekDay\":".length();
				itemEnd = groupString.indexOf(",", itemStart);
				String weekDay = groupString.substring(itemStart, itemEnd);
				
				//get week number
				itemStart = groupString.indexOf("\"WeekNumber\":") + "\"WeekNumber\":".length();
				itemEnd = itemStart + 1;
				String weekNumber = groupString.substring(itemStart, itemEnd);
			//Log.d("parsing", "id: " + id + "\nname: " + name + "\nshortName: " + shortName);
				
				//creating classItem
				ClassItem classItem = new ClassItem(
					beginTime,
					building,
					endTime,
					eventType,
					firstName,
					id,
					instructorId,
					lastName,
					middleName,
					pairNumber,
					room,
					subGroupNumber,
					subject,
					termNumber,
					termPart,
					weekDay,
					weekNumber);
				schedule.add(classItem);
			}
		} catch (Exception e) {
			Log.d("exceptions", "error parsing classes" + e.toString());
		}
		
		if (schedule.size() == 0)
			 return null;
		else
			return schedule;
	}
}
