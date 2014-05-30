package com.susuonline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.susuonline.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	MainActivity thisActivity = this;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavigationNames;
	
	final String ATTRIBUTE_NAME_TEXT = "text";
	final String ATTRIBUTE_NAME_IMAGE = "image";
	final String ATTRIBUTE_NAME_DATE = "date";
	ArrayList<Map<String, Object>> listViewContent;
	ListView lvSimple;
	SimpleAdapter sAdapter;
	int postCount;
	final int postCountStep = 10;
	final int postLimit = 101;
	DBHandler dbHandler;
	int PostNumber = 0;
	boolean isRefresh = true;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.news_layout);
	    
	    dbHandler = new DBHandler(this);
	    //Log.d(getString(R.string.logtag), "MainActivity: i wanna love you");	    
	    
	  //filling the listview
	    listViewContent = new ArrayList<Map<String, Object>>(/*postLimit + 20*/);
/*	    Map<String, Object> m;
	    for (int i = 0; i < texts.length; i++) {
	    	m = new HashMap<String, Object>();
	    	m.put(ATTRIBUTE_NAME_TEXT, texts[i]);
	    	m.put(ATTRIBUTE_NAME_IMAGE, imgurl);
	    	listViewContent.add(m);
	    }
*/	   
	    String[] from = {ATTRIBUTE_NAME_TEXT, ATTRIBUTE_NAME_IMAGE, ATTRIBUTE_NAME_DATE};
	    int[] to = {R.id.tvText, R.id.ivImg, R.id.tvDate};
	    
	    sAdapter = new SimpleAdapter(this, listViewContent, R.layout.news_item, from, to) {
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {  
	    		View view = super.getView(position, convertView, parent);  
		    		if ((position % 2) == 0) {
		    			view.setBackgroundColor(Color.parseColor("#E9E9E9"));
		    		} else {
		    			view.setBackgroundColor(Color.parseColor("#F5F5F5"));
		    		}
		    		return view;  
	    		} 
	    };
	    sAdapter.setViewBinder(new MyViewBinder());
	    
	    lvSimple = (ListView) findViewById(R.id.news_listview);
	    
	    //add footer view
    	LayoutInflater ltInflater = getLayoutInflater();
    	Button loadMoreButton = (Button) ltInflater.inflate(R.layout.load_more_button, null, false);
//    	Button refreshButton = (Button) ltInflater.inflate(R.layout.refresh_news_button, null, false);

    	//set list view parameters
    	lvSimple.addFooterView(loadMoreButton);
//    	lvSimple.addHeaderView(refreshButton);
    	lvSimple.setHeaderDividersEnabled(false);
    	lvSimple.setFooterDividersEnabled(false);
    	lvSimple.setDividerHeight(0);
    	
    	//set on click listener for load more button
    	OnClickListener loadMoreButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (postCount + postCountStep < postLimit) {
					//Log.d("mytest", ""+postCount);
					//Log.d("mytest", ""+(postCount+postCountStep));
					GetWallPosts getWallPostsTask = new GetWallPostsTask(listViewContent, sAdapter);
				   	getWallPostsTask.execute(getResources().getString(R.string.vk_group_id), Integer.toString(postCount), Integer.toString(postCountStep));
				   	postCount += postCountStep;
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.news_limit_reached), Toast.LENGTH_SHORT).show();
				}
			}
    	};
    	loadMoreButton.setOnClickListener(loadMoreButtonListener);
    	
    	//set on click listener for refresh button
//    	OnClickListener refreshButtonListener = new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//refresh behavior
//				isRefresh = true;
//				listViewContent.clear();
//				getVkPosts();
//			}
//    	};
//    	refreshButton.setOnClickListener(refreshButtonListener);
    	
    	//set adapter for list view
	    lvSimple.setAdapter(sAdapter);
	    
	    //run query to vk (id, from, to)
	    
	    getVkPosts();
    	
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
		        		Log.d("mytest", tw2.getText().toString());
		        		
		        		break;
		        		
		        	case 1: 
		        		TextView tw1 = (TextView) view;
		        		Log.d("mytest", tw1.getText().toString());
		        		Drawable draw1 = getResources().getDrawable(R.drawable.ic_action_go_to_today);
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
                getActionBar().setTitle(getResources().getString(R.string.app_name));
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
	
	public void getVkPosts() {
		GetWallPosts getWallPostsTask = new GetWallPostsTask(listViewContent, sAdapter);
	   	getWallPostsTask.execute(getResources().getString(R.string.vk_group_id), Integer.toString(0), Integer.toString(postCountStep));
	    postCount = postCountStep;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.general_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	    }
		
		switch (item.getItemId()) {
		case R.id.refresh_news:
			//Log.d("shit", "menu item selected " + item.getTitle());
			isRefresh = true;
			listViewContent.clear();
			getVkPosts();
		break;
		default:
			//default menu behavior
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class MyViewBinder implements SimpleAdapter.ViewBinder {
	       	    
	    @Override
	    public boolean setViewValue(View view, Object data, String textRepresentation) {
	    //Log.d(getString(R.string.logtag), "in set view " + textRepresentation);
	    	try {
		    	switch (view.getId()) {
		    	case R.id.tvText:
		    		if (data != null) {
			    		TextView tv = (TextView) view;
			    	//Log.d(getString(R.string.logtag), "setting textdata to " + data.toString());
			    		tv.setText(data.toString());
		    		}
		    		
		    		return true;
		    	case R.id.ivImg:
		    		//if (data != null) {
			    		ImageView iv = (ImageView) view;
			    		//new DownloadImageTask(iv).execute(textRepresentation);
			    		Bitmap pic = ((PictureItem) data).picture;
			    		//if (pic != null)
			    		iv.setImageBitmap(pic);
		    		//}
		    		
		    		return true;
		    	case R.id.tvDate:
		    		TextView tv = (TextView) view;
		    		Date date = (Date) data;
		    		String dateString = new SimpleDateFormat("dd MMM yyyy HH:mm").format(date); // 9:00
		    		tv.setText(dateString);
		    		
		    		return true;
		    	}
	    	} catch (Exception e) {
	    		Log.d("exceptions", "error in adapter view binder: " + e.toString());
	    	}
	    	//Log.d(getString(R.string.logtag), "after switch");
	        return false;
	    }
	}
	
	class GetWallPostsTask extends GetWallPosts {
		
		GetWallPostsTask(Object data, SimpleAdapter sAdapter) {
			super(data, sAdapter);
			// TODO Auto-generated constructor stub
		}

		@Override
    	public void onResponseReceived(String result) {
			try {
	    		//parse response
				VKParser vkParser = new VKParser();
				List<WallPostItem> posts = null;
				if ((result != null) && (!result.equals(""))) {
					posts = vkParser.parseWall(result);
					
					
					if (isRefresh) {
						//truncate table
						dbHandler.truncateNews();
					}
					
					//write to database
					dbHandler.addNewsPosts(posts);
				} else {
					//read from db
					posts = dbHandler.getNewsPosts(postCount - postCountStep, postCountStep);
					if (posts == null)
						Toast.makeText(thisActivity, getResources().getString(R.string.no_news), Toast.LENGTH_LONG).show();
					//Log.d("mytest", "GOT NEWS POSTS FROM DB: " + (postCount - postCountStep) + " - " + postCountStep);
				}
					
				if (posts != null) {
					for (WallPostItem post : posts) {
						//create new map for a post
			    		Map<String, Object> map = new HashMap<String, Object>();
				    	map.put(ATTRIBUTE_NAME_TEXT, post.text);
				    	PictureItem postPicture = new PictureItem();
				    	map.put(ATTRIBUTE_NAME_IMAGE, postPicture);
				    	map.put(ATTRIBUTE_NAME_DATE, post.date);
				    	
				    	if ((post.pictures != null) && (post.pictures.size() != 0)) {
				    		//start downloading of pictures
				    		
				    		String pictureLink = post.pictures.get(0);
				    		
				    		if (dbHandler.hasPicture(pictureLink)) {
				    			Bitmap image = dbHandler.getPicture(pictureLink);
				    			postPicture.picture = image;
				    		//Log.d("mytest", " HAS PICTURE " + pictureLink);
				    		} else {
					    		DownloadImageTask downloadImageTask = new DownloadImageTask(postPicture){
					    			@Override
					    			public void onResponseReceived(){
					    			//Log.d("mytest", "adding picture to db: " + urldisplay);
					    				if (bmImage.picture != null)
					    					dbHandler.addPicture(bmImage.picture, urldisplay);
					    				
					    				sAdapter.notifyDataSetChanged();
					    			};
					    		};
					    		downloadImageTask.execute(pictureLink);
				    		}
				    	}
				    	listViewContent.add(map);
					}
			    	sAdapter.notifyDataSetChanged();
				}
				
				if (isRefresh) {
					isRefresh = false;
					lvSimple.setSelectionAfterHeaderView();
				}
				
			} catch (Exception e) {
				Log.d("exceptions", "problem in get wall post task after post execute: " + e.toString());
			}
    	}
	}
	
	//new -----------------------------------------------------------
	/* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	switch (position) {
        	case 1: 
        		//start schedule
        		Intent intent = new Intent(thisActivity, ScheduleActivity.class);
        		startActivity(intent);
        		break;
    		default:
    			//default menu behavior
    			
    			break;
        	}
        	
        	// update selected item and title, then close the drawer
//            mDrawerList.setItemChecked(position, true);
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
}

