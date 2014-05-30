package com.susuonline;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public abstract class DownloadImageTask extends AsyncTask<String, Void, String> {
	  PictureItem bmImage;
	  String urldisplay;

	  public DownloadImageTask(PictureItem bmImage) {
	      this.bmImage = bmImage;
	  }

	  public abstract void onResponseReceived();
	  
	  protected String doInBackground(String... urls) {
	      urldisplay = urls[0];
	  //Log.d("mytest", urldisplay);
	      try {
	        InputStream in = new java.net.URL(urldisplay).openStream();
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = false;
	        options.inPreferredConfig = Config.RGB_565;
	        options.inDither = true;
	        
	        Bitmap image = BitmapFactory.decodeStream(in, null, options);
	        if (image == null) Log.d("exceptions", "loaded image is null");
	        bmImage.picture = image;
	        return "success";
	      } catch (Exception e) {
	          Log.d("exceptions", "" + e.getMessage());
	          return "failure";
	      }
	  }

	  protected void onPostExecute(String result) {
	      onResponseReceived();
	  }
	}
