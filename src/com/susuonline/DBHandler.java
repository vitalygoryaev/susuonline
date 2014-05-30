package com.susuonline;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {
	private static final int dbVersion = 23;
	
	public DBHandler(Context context) {
	    super(context, "susu_online", null, dbVersion);
	}
	
	@Override
    public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(
					"create table faculties (" +
						"id char(40) primary key on conflict ignore," +
						"name varchar(255) not null unique on conflict ignore," +
						"short_name varchar(10) not null unique on conflict ignore" +
					");"
			);
	
			db.execSQL(
	    		  	"create table groups (" +
						"id char(40) primary key on conflict ignore," +
						"faculty_id char(40) not null, " +
						"course_number INT(2) not null check (course_number > 0)," +
						"group_number INT(2) not null check (group_number > 0)," +
						"study_form varchar(20)," +
						"foreign key(faculty_id) references faculties(id)" +
	    			");"
			);
			
			db.execSQL(
					"create table classes (" +
						"group_id char(40) not null," +
						"id char(40) primary key on conflict ignore," +
						"begin_time varchar(15)," +
						"building varchar(50)," +
						"end_time varchar(15)," +
						"event_type varchar(100)," +
						"first_name varchar(100)," +
						"instructor_id char(40)," +
						"last_name varchar(100)," +
						"middle_name varchar(100)," +
						"pair_number int(2)," +
						"room varchar(20)," +
						"subgroup_number int(2)," +
						"subject varchar(255)," +
						"term_number int(2)," +
						"term_part int(2)," +
						"week_day int(2)," +
						"week_number int(2)," +
						"foreign key(group_id) references groups(id)," +
						"unique (term_part, term_number, week_day, pair_number, week_number) on conflict ignore" +
					");"
			);
	
			db.execSQL(
					"create table current (" +
						"group_id char(40) primary key on conflict replace," +
						"foreign key(group_id) references groups(id)" +
					");"
			);
			
			db.execSQL(
					"create table term_info (" +
						"group_id char(40) primary key on conflict replace," +
						"term_number INT(2) not null," +
						"term_part INT(2) not null," +
						"foreign key(group_id) references groups(id)" +
					");"
			);
			
			db.execSQL(
					"create table news (" +
						"timestamp INT(20) not null," +
						"text TEXT," +
						"picture VARCHAR(400)," +
						"unique (text, timestamp) on conflict ignore" +
					");"
			);
			
			db.execSQL(
					"create table pictures (" +
							"link VARCHAR(400) PRIMARY KEY ON CONFLICT IGNORE," +
							"picture BLOB not null" +
					");"
			);
	    	
		} catch (Exception e) {
			Log.d("exceptions", "db exception in on create: " + e.toString());
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try{
	    	//what to do if version is old
    		db.execSQL("DROP TABLE IF EXISTS pictures");
    		db.execSQL("DROP TABLE IF EXISTS news");
	    	db.execSQL("DROP TABLE IF EXISTS term_info");
	    	db.execSQL("DROP TABLE IF EXISTS classes");
	    	db.execSQL("DROP TABLE IF EXISTS current");
	    	db.execSQL("DROP TABLE IF EXISTS groups");
	    	db.execSQL("DROP TABLE IF EXISTS faculties");
	    	
	    	onCreate(db);
	    	
	    } catch (Exception e) {
			Log.d("exceptions", "db exception in on create: " + e.toString());
		}
    }
    
    public void addFaculties(List<FacultyItem> facultyList) {
    	try {
    		//delete old term info
    		SQLiteDatabase db = this.getWritableDatabase();
    		String query = "DELETE FROM faculties";
	        db.rawQuery(query, null);
    		db.close();
    		
	    	db = this.getWritableDatabase();
			
			for (FacultyItem fi : facultyList) {
				ContentValues values = new ContentValues();
				values.put("id", fi.id);
				values.put("name", fi.name);
				values.put("short_name", fi.shortName);
				db.insert("faculties", null, values);
			}
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add faculties: " + e.toString());
		}
    }
    
    public void addGroups(List<GroupItem> groupList, FacultyItem faculty) {
    	try {
    		//delete old term info
    		SQLiteDatabase db = this.getWritableDatabase();
    		String query = "DELETE FROM groups WHERE faculty_id = ?";
	        db.rawQuery(query, new String[] { faculty.id });
    		db.close();
    		
	    	db = this.getWritableDatabase();
			
			for (GroupItem gi : groupList) {
				ContentValues values = new ContentValues();
				values.put("id", gi.id);
				values.put("faculty_id", faculty.id);
				values.put("course_number", gi.courseNumber);
				values.put("group_number", gi.groupNumber);
				values.put("study_form", gi.studyForm);
				
				db.insert("groups", null, values);
			}
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add groups: " + e.toString());
		}
    }
    
    public void addClasses(List<ClassItem> classList, GroupItem group) {
    	try {
    		//delete old term info
    		SQLiteDatabase db = this.getWritableDatabase();
    		String query = "DELETE FROM classes WHERE group_id = ?";
	        db.rawQuery(query, new String[] { group.id });
    		db.close();
    		
	    	db = this.getWritableDatabase();
			for (ClassItem ci : classList) {
				ContentValues values = new ContentValues();
				values.put("group_id", group.id);
				values.put("id", ci.id);
				values.put("begin_time", ci.beginTime);
				values.put("building", ci.building);
				values.put("end_time", ci.endTime);
				values.put("event_type", ci.eventType);
				values.put("first_name", ci.firstName);
				values.put("instructor_id", ci.instructorId);
				values.put("last_name", ci.lastName);
				values.put("middle_name", ci.middleName);
				values.put("pair_number", ci.pairNumber);
				values.put("room", ci.room);
				values.put("subgroup_number", ci.subGroupNumber);
				values.put("subject", ci.subject);
				values.put("term_number", ci.termNumber);
				values.put("term_part", ci.termPart);
				values.put("week_day", ci.weekDay);
				values.put("week_number", ci.weekNumber);
				
				db.insert("classes", null, values);
			}
			
			
			db.close();
			
			setCurrentScheduleParameters(group);
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add classes: " + e.toString());
		}
    }
    
    public void setCurrentScheduleParameters(GroupItem group) {
    	try {
    		//clean old parameters
    		SQLiteDatabase db = this.getWritableDatabase();
    		db.execSQL("DELETE FROM current");
    		db.close();
    		
	    	db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("group_id", group.id);
			
			db.insert("current", null, values);
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in set parameters: " + e.toString());
		}
    }
    
    public void addTermInfo(GroupItem group, int termNumber, int termPart) {
    	try {
    		//delete old term info
    		SQLiteDatabase db = this.getWritableDatabase();
    		String query = "DELETE FROM term_info WHERE group_id = ?";
	        db.rawQuery(query, new String[] { group.id });
    		db.close();
    		
	    	db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("group_id", group.id);
			values.put("term_number", termNumber);
			values.put("term_part", termPart);
			
			db.insert("term_info", null, values);
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add term info: " + e.toString());
		}
    }
    
    public List<FacultyItem> getFaculties() {
    	List<FacultyItem> facultyList = new ArrayList<FacultyItem>();
    	try {
	        // Select All Query
	        String selectQuery = "SELECT * FROM " + "faculties";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                FacultyItem faculty = new FacultyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
	                facultyList.add(faculty);
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get faculties: " + e.toString());
		}
        
    	if (facultyList.size() == 0)
    		return null;
    	else
    		return facultyList;
        
    }
    
    public List<GroupItem> getGroups(FacultyItem faculty) {
    	List<GroupItem> groupList = new ArrayList<GroupItem>();
    	try {
	        String selectQuery = "SELECT * FROM groups WHERE faculty_id = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { faculty.id });
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                GroupItem group = new GroupItem(cursor.getString(2), cursor.getString(3), cursor.getString(0), cursor.getString(4));
	                groupList.add(group);
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get groups: " + e.toString());
		}
     
    	if (groupList.size() == 0)
    		return null;
    	else
    		return groupList;
    }
    
    public GroupItem getGroup(String id) {
    	GroupItem group = null;
    	try {
	        String selectQuery = "SELECT * FROM groups WHERE id = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { id });
	        
	        if (cursor.moveToFirst()) {
	            do {
	                group = new GroupItem(cursor.getString(2), cursor.getString(3), cursor.getString(0), cursor.getString(4));
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get group: " + e.toString());
		}
     
        return group;
    }
    
    public FacultyItem getFacultyOfGroup(GroupItem group) {
    	FacultyItem faculty = null;
    	try {
	        String selectQuery = "SELECT * FROM groups WHERE id = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { group.id });
	        String faculty_id = "";
	        if (cursor.moveToFirst()) {
	            do {
	                faculty_id = cursor.getString(1);
	            } while (cursor.moveToNext());
	        }
	        db.close();
	        
	        selectQuery = "SELECT * FROM faculties WHERE id = ?";
		     
	        db = this.getWritableDatabase();
	        cursor = db.rawQuery(selectQuery, new String[] { faculty_id });
	        
	        if (cursor.moveToFirst()) {
	            do {
	            	faculty = new FacultyItem(cursor.getString(0), cursor.getString(1), cursor.getString(2));
	            } while (cursor.moveToNext());
	        }
	        db.close();
	        
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get faculty of group: " + e.toString());
		}
     
        return faculty;
    }
    
    public List<ClassItem> getClasses(GroupItem group, int termPart, int termNumber) {
    	List<ClassItem> classList = new ArrayList<ClassItem>();
    	try {
	        String selectQuery = "SELECT * FROM classes WHERE group_id = ? AND term_part = ? AND term_number = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { group.id, ""+termPart, ""+termNumber });
	     
	        // looping through all rows and adding to list
	        int i = 0;
	        if (cursor.moveToFirst()) {
	            do {
	            	i++;
	                ClassItem oneClass = new ClassItem(
	                		cursor.getString(2)
	                		, cursor.getString(3)
	                		, cursor.getString(4)
	                		, cursor.getString(5)
	                		, cursor.getString(6)
	                		, cursor.getString(1)
	                		, cursor.getString(7)
	                		, cursor.getString(8)
	                		, cursor.getString(9)
	                		, cursor.getString(10)
	                		, cursor.getString(11)
	                		, cursor.getString(12)
	                		, cursor.getString(13)
	                		, cursor.getString(14)
	                		, cursor.getString(15)
	                		, cursor.getString(16)
	                		, cursor.getString(17)
	                );
	                classList.add(oneClass);
	            } while (cursor.moveToNext());
	        }
	        
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get classes: " + e.toString());
		}
    	
    	if (classList.size() == 0)
    		return null;
    	else
    		return classList;
    }
    
    public Map<String, String> getCurrentParameters() {
    	Map<String, String> parameters = new HashMap<String, String>();
    	try {
	    	//select current group and week number
	    	String selectQuery = "SELECT group_id FROM current";
	        
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        String groupId = "";
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	            	groupId = cursor.getString(0);
	                parameters.put("group_id", groupId);
	            } while (cursor.moveToNext());
	        }
	        db.close();
	        
	        //select term number and term part from group
	        selectQuery = "SELECT term_number, term_part FROM term_info WHERE group_id = ?";
	        
	        db = this.getWritableDatabase();
	        cursor = db.rawQuery(selectQuery, new String[] { groupId });
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	            	parameters.put("term_number", cursor.getString(0));
	            	parameters.put("term_part", cursor.getString(1));
	            } while (cursor.moveToNext());
	        }
	        
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get current parameters: " + e.toString());
		}
    	if (parameters.size() == 0)
    		return null;
    	else
    		return parameters;
    }
    
    public Map<String, Integer> getTermInfo(GroupItem group) {
    	Map<String, Integer> termInfo = new HashMap<String, Integer>();
    	try {
	    	//select current group and week number
	    	String selectQuery = "SELECT * FROM term_info WHERE group_id = ?";
	        
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { group.id });
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                termInfo.put("term_number", cursor.getInt(1));
	                termInfo.put("term_part", cursor.getInt(2));
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get term info: " + e.toString());
		}

        return termInfo;
    }
    
    public void addNewsPosts(List<WallPostItem> newsList) {
    	try {
	    	SQLiteDatabase db = this.getWritableDatabase();
			
			for (WallPostItem post : newsList) {
				ContentValues values = new ContentValues();
				values.put("timestamp", post.date.getTime());
				values.put("text", post.text);
				if (post.pictures != null)
					values.put("picture", post.pictures.get(0));
				db.insert("news", null, values);
			}
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add news posts: " + e.toString());
		}
    }
    
    public List<WallPostItem> getNewsPosts(int from, int count) {
    	List<WallPostItem> newsList = new ArrayList<WallPostItem>();
    	try {
	        String selectQuery = "SELECT * FROM news ORDER BY timestamp desc LIMIT ?, ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { ""+from, ""+count });
	     
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	            	String pictureUrl = cursor.getString(2);
	            	List<String> pictures = null;
	            	if (pictureUrl != null) {
		            	pictures = new ArrayList<String>();
		            	pictures.add(cursor.getString(2));
	            	}
	                WallPostItem post = new WallPostItem(cursor.getString(1), pictures, Long.parseLong(cursor.getString(0)));
	                newsList.add(post);
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get news posts: " + e.toString());
		}
    	
    	if (newsList.size() == 0)
    		return null;
    	else
    		return newsList;
    }
    
    public void addPicture(Bitmap picture, String link) {
    	try {
	    	SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put("link", link);
			//compress bitmap and convert in to byte array
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			picture.compress(Bitmap.CompressFormat.JPEG, 95, stream);
			byte[] pictureByteArray = stream.toByteArray();
			values.put("picture", pictureByteArray);
			db.insert("pictures", null, values);
			
			db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in add picture: " + e.toString());
		}
    }
    
    public Bitmap getPicture(String link) {
    	Bitmap picture = null;
    	try {
	        String selectQuery = "SELECT picture FROM pictures WHERE link = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { link });
	     
	        //decode byte array to bitmap
	        if (cursor.moveToFirst()) {
	            do {
	            	byte[] pictureByteArray = cursor.getBlob(0);
	            	picture = BitmapFactory.decodeByteArray(pictureByteArray, 0, pictureByteArray.length);
	            } while (cursor.moveToNext());
	        }
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in get picture: " + e.toString());
		}
     
        return picture;
    }
    
    public boolean hasPicture(String link) {
    	//Log.d("mytest", "in HAS PICTURE procedure with link: " + link);
    	try {
	        String selectQuery = "SELECT count(*) FROM pictures WHERE link = ?";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, new String[] { link });
	     
	        //decode byte array to bitmap
	        if (cursor.moveToFirst()) {
	            do {
	            	int count = cursor.getInt(0);
	            	if (count == 1) {
	            		db.close();
	            		return true;
	            	} else {
	            		db.close();
	            		return false;
	            	}
	            } while (cursor.moveToNext());
	        } else {
	        	db.close();
	        	return false;
	        }
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in has picture: " + e.toString());
		}
     
        return false;
    }
    
    public void truncateNews() {
    	//Log.d("mytest", "in HAS PICTURE procedure with link: " + link);
    	try {
	        String selectQuery = "DELETE FROM news";
	     
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        
	        db.close();
    	} catch (Exception e) {
			Log.d("exceptions", "db exception in truncate news: " + e.toString());
		}
    }
}
