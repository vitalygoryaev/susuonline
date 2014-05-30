package com.susuonline;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.susuonline.R;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends Activity {
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationNames;
	
	int termNumber;
	int termPart;
	int weekNumber;
	Activity thisActivity = this;
	Spinner facultySpinner;
	Spinner groupSpinner;
	Switch weekSwitch;
	List<FacultyItem> facultyList;
	List<GroupItem> groupList;
	List<ClassItem> schedule;
	List<String> groupNumbers;
	List<String> facultyNames;
	List<CharSequence> weekDays;
	ArrayAdapter<String> facultyAdapter;
	ArrayAdapter<String> groupAdapter;
	Button getButton;
	List<CharSequence> allowedFaculties;
	GroupItem selectedGroup;
	FacultyItem selectedFaculty;
	DBHandler dbHandler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_layout);
		Intent intent = getIntent();
		
		//create database handler
		dbHandler = new DBHandler(this);
		
		//find spinners and buttons
		facultyNames = new ArrayList<String>();
		groupNumbers = new ArrayList<String>();
		weekDays = Arrays.asList(getResources().getTextArray(R.array.weekdays));
		facultyNames.add(getResources().getString(R.string.select_faculty));
		allowedFaculties = Arrays.asList(getResources().getTextArray(R.array.allowed_faculties));
		facultySpinner = (Spinner) findViewById(R.id.faculty_spinner);
		groupSpinner = (Spinner) findViewById(R.id.group_spinner);
		getButton = (Button) findViewById(R.id.schedule_button);
		weekSwitch = (Switch) findViewById(R.id.week_switch);
		mTitle = getResources().getString(R.string.title_activity_schedule);
		
		//basic offline movements
		Map<String, String> currentParameters = dbHandler.getCurrentParameters();
		if (currentParameters == null)
			Toast.makeText(this, getResources().getString(R.string.need_internet), Toast.LENGTH_LONG);
		else {
			selectedGroup = dbHandler.getGroup(currentParameters.get("group_id"));
			selectedFaculty = dbHandler.getFacultyOfGroup(selectedGroup);
		
			Calendar calendar = Calendar.getInstance(); 
			weekNumber = (calendar.get(Calendar.WEEK_OF_YEAR) % 2) + 1;
			
			try {
				termNumber = Integer.parseInt(currentParameters.get("term_number"));
				termPart = Integer.parseInt(currentParameters.get("term_part"));
			} catch (Exception e) {
				Log.d("exceptions", "error parsing term number and part from current parameters from database");
			}
			Log.d("mytest", "getting classes for group: " + selectedGroup.groupNumber + " term part: " + termPart + "term number: " + termNumber);
			schedule = dbHandler.getClasses(selectedGroup, termPart, termNumber);
			
			//call method that fills the table (schedule, termNumber, termPart, weekNumber)
			try {
				this.fillScheduleTable(schedule, termNumber, termPart, weekNumber);
			} catch (Exception e) {
				Log.d("exceptions", "error filling schedule from offline database");
			}
			
			if (weekNumber == 1) 
				weekSwitch.setChecked(true);
			else 
				weekSwitch.setChecked(false);
		
		}
		
		//create and set on click listener for button get/refresh
		OnClickListener scheduleButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//get selected faculty and group
//				Toast.makeText(getBaseContext(), "" + facultySpinner.getSelectedItemPosition()
//						+ " " + groupSpinner.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();
				
				try {
					selectedGroup = groupList.get(groupSpinner.getSelectedItemPosition());
					selectedFaculty = facultyList.get(facultySpinner.getSelectedItemPosition() - 1);
					dbHandler.setCurrentScheduleParameters(selectedGroup);
					//get termNumber, termPart and weekNumber
					//assign defaults
	        		termNumber = 1;
	        		termPart = 1;
	        		
	        		//set week number
	        		Calendar calendar = Calendar.getInstance(); 
	        		weekNumber = (calendar.get(Calendar.WEEK_OF_YEAR) % 2) + 1;
	        		
	        		//get current term
					GetRequestTask getTermPart = new GetRequestTask(thisActivity) {
	                	@Override
	                	public void onResponseReceived(String result) {
	                		//parsing input string
	                		try {
	                			//check for null input
//	                		Log.d("mytest", "term part response:" + result + ":");
	                			if ((result != null) && (!result.equals(""))) {
	
		                			//get term number
	                				int itemStart = result.indexOf("\"TermNumber\":") + "\"TermNumber\":".length();
	                				int itemEnd = result.indexOf(",", itemStart);
	                				String tn = result.substring(itemStart, itemEnd);
	                			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nid string: " + id);
	                			
	                				//get term part
	                				itemStart = result.indexOf("\"TermPart\":") + "\"TermPart\":".length();
	                				itemEnd = result.indexOf("}", itemStart);
	                				String tp = result.substring(itemStart, itemEnd);
	                			//Log.d("parsing", "start: " + itemStart + "\nend: " + itemEnd + "\nname string: " + name);
	                				
	                				termNumber = Integer.parseInt(tn);
	                				termPart = Integer.parseInt(tp);
	                			//Log.d("mytest", "set termNumber=" + termNumber + " termPart=" + termPart);
	                				
	                				//write term info to db
	                				dbHandler.addTermInfo(selectedGroup, termNumber, termPart);
	                			} else {
		                				
	                				//read data from db
	                				Map<String, Integer> termInfo = dbHandler.getTermInfo(selectedGroup);
	                				termNumber = termInfo.get("term_number");
	                				termPart = termInfo.get("term_part");
	                			}
                			//Log.d("mytest", "set termNumber=" + termNumber + " termPart=" + termPart);
                				
                				if (weekNumber == 1) 
                					weekSwitch.setChecked(true);
                				else 
                					weekSwitch.setChecked(false);
	                				
	                		//Log.d("exceptions", "number + part: " + termNumber + " / " + termPart);		
	                		
	                		} catch (Exception e) {
	                			Log.d("exceptions", "error parsing term number " + e.toString());
	                		}
	                	}
	                };
//	            Log.d("mytest", getResources().getString(R.string.univeris_term_part_link) + selectedGroup.id);
	                getTermPart.execute(getResources().getString(R.string.univeris_term_part_link) + selectedGroup.id);
					
					//get schedule
					GetRequestTask getSchedule = new GetRequestTask(thisActivity) {
	                	@Override
	                	public void onResponseReceived(String result) {
	                		UniverisParser up = new UniverisParser();
	                		
	                		try {
	                			if (result != null) {
		                		//Log.d("exceptions", "result for parsing" + result);
		        	        		schedule = up.parseSchedule(result);
		        	        		
		        	        		//write classes to db
		        	        		dbHandler.addClasses(schedule, selectedGroup);
	                			} else {
		        	        		//read data from db
		        	        		schedule = dbHandler.getClasses(selectedGroup, termPart, termNumber);
	                			}
	        	        		//call method that fills the table (schedule, termNumber, termPart, weekNumber)
	                			if (schedule == null)
	                				Toast.makeText(thisActivity, getResources().getString(R.string.no_classes), Toast.LENGTH_LONG).show();
	        	        		((ScheduleActivity) activity).fillScheduleTable(schedule, termNumber, termPart, weekNumber);
	        	        		
	                		} catch (Exception e) {
	                			Log.d("exceptions", "error with schedule:\n" + e.toString());
	                		}
	                	}
	                };
//	            Log.d("mytest", getResources().getString(R.string.univeris_schedule_link) + selectedGroup.id);
	                getSchedule.execute(getResources().getString(R.string.univeris_schedule_link) + selectedGroup.id);
				} catch (Exception e) {
					Log.d("exceptions", "problem in refresh button handler" + e.toString());
				}
			}
		};
		getButton.setOnClickListener(scheduleButtonListener);
		
		//setting an adapter for group spinner
		groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupNumbers);
		groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(groupAdapter);
        groupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        		//Toast.makeText(getBaseContext(), "selected: " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
		
		//setting an adapter for faculty spinner
		facultyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, facultyNames);
		facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyAdapter);
        facultySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            	//if something selected
            	if (position != 0) {
            		selectedFaculty = facultyList.get(position - 1);
            		//Toast.makeText(getBaseContext(), selectedItem.name + position, Toast.LENGTH_SHORT).show();
            		
            		//depending on selected faculty get a list of groups
                	GetRequestTask getGroupList = new GetRequestTask(thisActivity) {
                    	@Override
                    	public void onResponseReceived(String result) {
                    		UniverisParser up = new UniverisParser();
                    		
                    		try {
                    			if (result != null) {
	            	        		groupList = up.parseGroups(result);
	            	        		
	            	        		//add groups to db
	            	        		dbHandler.addGroups(groupList, selectedFaculty);
                    			} else {
	            	        		//read groups from db
	            	        		groupList = dbHandler.getGroups(selectedFaculty);
                    			}
            	        		
            	        		//fill the new data
            	        		groupNumbers.clear();
            	        		for (GroupItem group : groupList) {
            	        			groupNumbers.add("" + group.groupNumber);
            	        		}
            	        		
            	        		//notify adapter that it should refresh
            	        		groupAdapter.notifyDataSetChanged();
            	        		
            	        		//make group spinner visible
            	        		groupSpinner.setVisibility(View.VISIBLE);
                    		} catch (Exception e) {
                    			Log.d("exceptions", "in schedule activity after data set changed:\n" + e.toString());
                    		}
                    	}
                    };
//                Log.d("mytest", getResources().getString(R.string.univeris_groups_link) + selectedFaculty.id);
                    getGroupList.execute(getResources().getString(R.string.univeris_groups_link) + selectedFaculty.id);
            	}
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        
        
        //get faculty list
        GetRequestTask getFacultyList = new GetRequestTask(thisActivity) {
        	@Override
        	public void onResponseReceived(String result) {
        		UniverisParser up = new UniverisParser();
        		
        		try {
        			if (result != null) {
//        			Log.d("mytest", "result is null " + (result == null));
        			
        				facultyList = up.parseFaculties(result);
		        	
		        		//fill the new data
		        		List<FacultyItem> filteredFacultyList = new ArrayList<FacultyItem>();
		        		for (FacultyItem faculty : facultyList) {
		        			//filter faculties
		        			if (allowedFaculties.contains(faculty.name)) {
		        				filteredFacultyList.add(faculty);
		        				facultyNames.add(faculty.name);
		        			}
		        		}
		        		facultyList = filteredFacultyList;
		        		
		        		//insert faculties into db
		        		dbHandler.addFaculties(filteredFacultyList);
        			} else {
		        		//read faculties from db
		        		facultyList = dbHandler.getFaculties();
		        		
		        		for (FacultyItem faculty : facultyList) {
	        				facultyNames.add(faculty.name);
		        		}
        			}
	        		
	        		//notify adapter that it should refresh
	        		facultyAdapter.notifyDataSetChanged();
        		} catch (Exception e) {
        			Log.d("exceptions", "in schedule activity after data set changed:\n" + e.toString());
        		}
        	}
        };
//    Log.d("mytest", getResources().getString(R.string.univeris_faculties_link));
        getFacultyList.execute(getResources().getString(R.string.univeris_faculties_link));
        
        //set week switch
		weekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            weekNumber = 1; 
		        } else {
		        	weekNumber = 2;
		        }
		        try {
		        	if (schedule != null) {
//		        	Log.d("mytest", "filling schedule table. week=" + weekNumber + " termpart=" + termPart + " termNumber=" + termNumber);
		        		fillScheduleTable(schedule, termNumber, termPart, weekNumber);
		        	} else {
		        		Toast.makeText(thisActivity, getResources().getString(R.string.no_classes), Toast.LENGTH_LONG).show();
		        	}
		        } catch (Exception e) {
		        	Log.d("exceptions", "after week change: " + e.toString());
		        }
		    }
		});
		
		//-------------------------------------------------------------------------------
	    mDrawerTitle = getResources().getString(R.string.navigation_title);
        mNavigationNames = getResources().getStringArray(R.array.navigation_names);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        // set up the drawer's list view with items and click listener
        
	    ArrayAdapter<String> navAdapter = new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationNames) {
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {  
	    		View view = super.getView(position, convertView, parent);  
	    		
		    		switch (position) {
		    		case 0:
		        		Drawable draw0 = getResources().getDrawable(R.drawable.ic_action_about);
		        		DisplayMetrics metrics = getResources().getDisplayMetrics();
		        		int mX = Math.round(50*metrics.density);
		        		draw0.setBounds(0, 0, mX, mX);
		        		TextView tw2 = (TextView) view;
		        		tw2.setCompoundDrawables(draw0, null, null, null);
//		        		Log.d("mytest", tw2.getText().toString());
		        		
		        		break;
		        		
		        	case 1: 
		        		TextView tw1 = (TextView) view;
//		        		Log.d("mytest", tw1.getText().toString());
		        		Drawable draw1 = getResources().getDrawable(R.drawable.ic_action_go_to_today);
		        		draw1.setBounds(0, 0, 50, 50);
		        		metrics = getResources().getDisplayMetrics();
		        		mX = Math.round(50*metrics.density);
		        		draw1.setBounds(0, 0, mX, mX);
		        		tw1.setCompoundDrawables(draw1, null, null, null);
		        		
		        		break;
		    		default:
		    			break;
		        	}
		    		
		    		return view;  
	    		} 
	    };
        
        mDrawerList.setAdapter(navAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
        //-------------------------------------------------------------------------------------------------
	}

	//MENU
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.general_menu, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.news_menu_item:
//			//Log.d("shit", "menu item selected " + item.getTitle());
//			
//			Intent intent = new Intent(this, MainActivity.class);
//			startActivity(intent);
//			break;
//		default:
//			//Log.d("shit", "default");
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	public void fillScheduleTable(List<ClassItem> schedule, int termNumber, int termPart, int weekNumber) throws Exception {
		//get layout inflater
		LayoutInflater ltInflater = getLayoutInflater();
		
		//clean the table
		for (CharSequence day : weekDays) {
    		for (int i = 1; i < 7; i++) {
    			int rowId = getResources().getIdentifier(day.toString() + i, "id", getPackageName());
    			if (rowId == 0) 
    				throw new Exception("table row was not found for timestring :" + day + i);
    			
    			int headerId = getResources().getIdentifier(day + "_header", "id", getPackageName());
    			if (headerId == 0) 
    				throw new Exception("table row header was not found for " + day);
    			
    			TableRow tableRow = (TableRow) findViewById(rowId);
    			tableRow.removeAllViews();
    			
    			TableRow tableHeaderRow = (TableRow) findViewById(headerId);
    			tableHeaderRow.setVisibility(View.GONE);
    		}
		}
		
		//fill the table
		//show group name
		TextView scheduleHeader = (TextView) findViewById(R.id.schedule_header);
		scheduleHeader.setText(getResources().getString(R.string.schedule_header_text) + selectedFaculty.shortName + "-" + selectedGroup.groupNumber);
		
//		Log.d("mytest", "weekNumber: " + weekNumber + " termPart: " + termPart + " termNumber: " + termNumber);
		
		//walk through whole list
		for (ClassItem classItem : schedule) {
			String timeString = "";
			
			if (((classItem.weekNumber == weekNumber) | (classItem.weekNumber == 0)) && 
					(classItem.termPart == termPart) && (classItem.termNumber == termNumber)) {
				timeString = weekDays.get(classItem.weekDay - 1).toString() + classItem.pairNumber;
//				Log.d("mytest", "in if");
//				
//				Log.d("mytest", "==========start of class item");
//				Log.d("mytest", classItem.beginTime);
//				Log.d("mytest", classItem.endTime);
//				Log.d("mytest", classItem.subject);
//				Log.d("mytest", classItem.id);
//				Log.d("mytest", classItem.room);
//				
//				Log.d("mytest", "pairNumber: " + classItem.pairNumber);
//				Log.d("mytest", "weekDay: " + classItem.weekDay);
//				Log.d("mytest", "weekNumber: " + classItem.weekNumber);
//				Log.d("mytest", "termNumber: " + classItem.termNumber);
//				Log.d("mytest", "termPart: " + classItem.termPart);
//				Log.d("mytest", "==========end of class item");
				
				//make day header visible
				String day = weekDays.get(classItem.weekDay - 1).toString();
				int headerId = getResources().getIdentifier(day + "_header", "id", getPackageName());
				if (headerId == 0) 
					throw new Exception("table row header was not found for " + day);
				
				TableRow tableHeaderRow = (TableRow) findViewById(headerId);
				tableHeaderRow.setVisibility(View.VISIBLE);
				
				//get table row for this class
				int rowId = getResources().getIdentifier(timeString, "id", getPackageName());
				if (rowId == 0) 
					throw new Exception("table row was not found for timestring :" + timeString);
				
				TableRow tableRow = (TableRow) findViewById(rowId);
				//set row on click listener
				tableRow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View view) {
						TextView classId = (TextView) view.findViewById(R.id.class_id);
						
						//find class from schedule by id
						ClassItem cl = findClassById(classId.getText().toString());
						if (cl != null) {
							//show dialog with class info
							ClassDialog dialog = (new ClassDialog()).newInstance(thisActivity, cl);
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							dialog.show(ft, "class_info_dialog");
						}
					}
				});
				
				TextView classTime = (TextView) ltInflater.inflate(R.layout.class_time, null, false);
				TextView classSubject = (TextView) ltInflater.inflate(R.layout.class_subject, null, false);
				TextView classId = (TextView) ltInflater.inflate(R.layout.class_id, null, false);
				
				//make time properly formatted for displaying
				int beginHours = Integer.parseInt(classItem.beginTime.substring(classItem.beginTime.indexOf("PT") + 2, classItem.beginTime.indexOf("H")));
				
				int beginMinutes = 0;
				if (classItem.beginTime.indexOf("M") != -1)
					beginMinutes = Integer.parseInt(classItem.beginTime.substring(classItem.beginTime.indexOf("H") + 1, classItem.beginTime.indexOf("M")));
				
//				int endHours = Integer.parseInt(classItem.endTime.substring(classItem.endTime.indexOf("PT") + 2, classItem.endTime.indexOf("H")));
//				
//				int endMinutes = 0;
//				if (classItem.endTime.indexOf("M") != -1)
//					endMinutes = Integer.parseInt(classItem.endTime.substring(classItem.endTime.indexOf("H") + 1, classItem.endTime.indexOf("M")));
				
				DecimalFormat myFormatter = new DecimalFormat("00");
				classTime.setText("" + myFormatter.format(beginHours) + ":" 
									+ myFormatter.format(beginMinutes) //+ " - " 
//									+ myFormatter.format(endHours) + ":" 
//									+ myFormatter.format(endMinutes)
								);
				classSubject.setText(classItem.subject + " " + classItem.room + " / " + classItem.building);
				classId.setText(classItem.id);
				
				tableRow.addView(classTime);
				tableRow.addView(classSubject);
				tableRow.addView(classId);
				classId.setVisibility(View.GONE);
			}
		}
	}
	
	//new -----------------------------------------------------------
		/* The click listner for ListView in the navigation drawer */
	    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	switch (position) {
	        	case 0: 
	        		//start schedule
	        		Intent intent = new Intent(thisActivity, MainActivity.class);
	    			startActivity(intent);
	        		break;
	    		default:
	    			//default menu behavior
	    			
	    			break;
	        	}
	        	
	        	// update selected item and title, then close the drawer
//	            mDrawerList.setItemChecked(position, true);
	            setTitle(R.string.app_name);
	            mDrawerLayout.closeDrawer(mDrawerList);
	        }
	    }

	    @Override
	    public void setTitle(CharSequence title) {
	        mTitle = title;
	        getActionBar().setTitle(mTitle);
	    }
		
		@Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        // Pass any configuration change to the drawer toggls
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Pass the event to ActionBarDrawerToggle, if it returns
	        // true, then it has handled the app icon touch event
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	        }
	        // Handle your other action bar items...

	        return super.onOptionsItemSelected(item);
	    }
	    
	    //find class from schedule by class id
	    public ClassItem findClassById(String id) {
	    	for (ClassItem classItem : schedule) {
	    		if (classItem.id.equals(id))
	    			return classItem;
	    	}
	    	
	    	return null;
	    }
}
