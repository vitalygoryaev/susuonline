package com.susuonline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

public abstract class GetWallPosts extends AsyncTask<String, Void, String> {
	//Object data;
	SimpleAdapter sAdapter;
	
	GetWallPosts(Object data, SimpleAdapter sAdapter) {
		//this.data = data;
		this.sAdapter = sAdapter;
	}
	
	public abstract void onResponseReceived(String result);
	
	private final String url = "http://api.vk.com/method/wall.get";  	  
	
	  protected String doInBackground(String... params) {
		  //Log.d("vkquery", "inside background task");
		  String id = params[0];
		  String from = params[1];
		  String count = params[2];
		  
		  HttpClient httpClient = new DefaultHttpClient();
		  HttpResponse response;
		  String responseString = null;
		  
		  try {
	    	  String urlWithParams = url + "?owner_id=" + id + "&offset=" + from + "&count=" + count;
	    	  response = httpClient.execute(new HttpGet(urlWithParams));
	    	  StatusLine statusLine = response.getStatusLine();
	    	  
	    	  if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
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
	    	  Log.d("exceptions", "protocol exception: " + e.toString());
	            //TODO Handle problems..
	      } catch (Exception e) {
	    	  Log.d("exceptions", "problem in get wall posts: " + e.toString());
	            //TODO Handle problems..
	      }
	      return responseString;
	  }

	  protected void onPostExecute(String result) {
		  //Log.d("vkquery", "in post execute");
		  //Log.d("vkquery", "" + result);
		  onResponseReceived(result);
	  }
	}
