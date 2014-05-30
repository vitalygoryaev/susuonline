package com.susuonline;

import java.text.DecimalFormat;

import com.susuonline.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ClassDialog extends DialogFragment {
	String beginTime;
	String building;
	String endTime;
	String eventType;
	String firstName;
	String instructorId;
	String lastName;
	String middleName;
	int pairNumber;
	String room;
	int weekDay;
	int weekNumber;
	String subject;
	
	Context context;
	
	public ClassDialog newInstance(Context context, ClassItem classItem) {
        ClassDialog f = this;
        this.context = context;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("begin_time", classItem.beginTime);
        args.putString("building", classItem.building);
        args.putString("end_time", classItem.endTime);
        args.putString("event_type", classItem.eventType);
        args.putString("first_name", classItem.firstName);
        args.putString("instructor_id", classItem.instructorId);
        args.putString("last_name", classItem.lastName);
        args.putString("middle_name", classItem.middleName);
        args.putString("room", classItem.room);
        args.putString("subject", classItem.subject);
        args.putInt("pair_number", classItem.pairNumber);
        args.putInt("week_day", classItem.weekDay);
        args.putInt("week_number", classItem.weekNumber);
        f.setArguments(args);

        return f;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	beginTime = getArguments().getString("begin_time");
    	building = getArguments().getString("building");
    	endTime = getArguments().getString("end_time");
    	eventType = getArguments().getString("event_type");
    	firstName = getArguments().getString("first_name");
    	instructorId = getArguments().getString("instructor_id");
    	lastName = getArguments().getString("last_name");
    	middleName = getArguments().getString("middle_name");
    	room = getArguments().getString("room");
    	subject = getArguments().getString("subject");
    	pairNumber = getArguments().getInt("pair_number");
    	weekDay = getArguments().getInt("week_day");
    	weekNumber = getArguments().getInt("week_number");
    	
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        try {
	        //get day string
	        String day = "";
	        switch (weekDay) {
	        case 1: day = context.getResources().getString(R.string.monday); break;
	        case 2: day = context.getResources().getString(R.string.tuesday); break;
	        case 3: day = context.getResources().getString(R.string.wednesday); break;
	        case 4: day = context.getResources().getString(R.string.thursday); break;
	        case 5: day = context.getResources().getString(R.string.friday); break;
	        case 6: day = context.getResources().getString(R.string.saturday); break;
	        }
	        
	        //get time string
	        //make time properly formatted for displaying
			int beginHours = Integer.parseInt(beginTime.substring(beginTime.indexOf("PT") + 2, beginTime.indexOf("H")));
			
			int beginMinutes = 0;
			if (beginTime.indexOf("M") != -1)
				beginMinutes = Integer.parseInt(beginTime.substring(beginTime.indexOf("H") + 1, beginTime.indexOf("M")));
			
			int endHours = Integer.parseInt(endTime.substring(endTime.indexOf("PT") + 2, endTime.indexOf("H")));
			
			int endMinutes = 0;
			if (endTime.indexOf("M") != -1)
				endMinutes = Integer.parseInt(endTime.substring(endTime.indexOf("H") + 1, endTime.indexOf("M")));
			
			DecimalFormat myFormatter = new DecimalFormat("00");
			
			String time = "" + myFormatter.format(beginHours) + ":" 
								+ myFormatter.format(beginMinutes) + " - " 
								+ myFormatter.format(endHours) + ":" 
								+ myFormatter.format(endMinutes);
			
			//compose message string
			String message = day + " " + time + "\n"
					+ subject + " " + building + " / " + room + "\n"
					+ eventType + "\n"
					+ lastName + " " + firstName + " " + middleName;
							
	        
	        builder.setMessage(message)
	               .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                   }
	               });
	               
	        // Create the AlertDialog object and return it
    	} catch (Exception e) {
    		Log.d("exceptions", "error in dialog class: " + e.toString());
    	}
        return builder.create();
    }
}