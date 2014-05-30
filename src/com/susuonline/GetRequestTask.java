package com.susuonline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public abstract class GetRequestTask extends AsyncTask<String, Void, String> {
	Activity activity;
	
	GetRequestTask(Activity activity) {
		this.activity = activity;
	}

	public abstract void onResponseReceived(String result);
	
	protected String doInBackground(String... params) {
		//Log.d("susu_query", "inside background task");
		String url = params[0];
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response;
		String responseString = null;
		  
	    try {
	    	//Log.d("susu_query", "before connect");
	    	HttpGet httpGet = new HttpGet(url);
	    	httpGet.setHeader("Accept", "application/json");
	    	response = httpClient.execute(httpGet);
	    	StatusLine statusLine = response.getStatusLine();
	    	  
	    	if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	    		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    		response.getEntity().writeTo(out);
	    		out.close();
	          	responseString = out.toString();
	        } else {
	            //Closes the connection.
	        	response.getEntity().getContent().close();
	        	throw new IOException(statusLine.getReasonPhrase());
	        }	  
	    } catch (ClientProtocolException e) {
	    	Log.d("exceptions", "client protocol exception in get request: " + e.toString());
	          //TODO Handle problems..
	    } catch (Exception e) {
	    	Log.d("exceptions", "error in get requets: " + e.toString());
	          //TODO Handle problems..
	    }
	    
	    return responseString;
	}

	protected void onPostExecute(String result) {
		//Log.d("susu_query", "in post execute");
		if (result != null) 
			Log.d("susu_query", result);
		else 
			Log.d("susu_query", "result is null");
		onResponseReceived(result);
	}
}

