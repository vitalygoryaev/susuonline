package com.susuonline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.text.Html;
import android.util.Log;

public class VKParser {
	public List<WallPostItem> parseWall(String response) {
		//Log.d("exceptions", "in parse wall");
		List<WallPostItem> wallPostItems = new ArrayList<WallPostItem>();
		
		try {
						
			//get response body
			int itemStart = response.indexOf("\"response\":") + "\"response\":".length();
			int itemEnd = response.length() - 2;
			String responseBody = response.substring(itemStart, itemEnd);
			
			int endOfPost = 0;
			int startOfPost = 0;
			//while there is another post
			while (responseBody.indexOf("\"id\":", endOfPost) != -1) {
			//Log.d("mytest", "in another post");
				List<String> pictures = new ArrayList<String>(20);
					 
				startOfPost = responseBody.indexOf("\"id\":", endOfPost);
				if (responseBody.indexOf("\"id\":", startOfPost + 2) == -1)
					endOfPost = responseBody.length();
				else
					endOfPost = responseBody.indexOf("\"id\":", startOfPost + 2) - 2;
				
				String postBody = responseBody.substring(startOfPost, endOfPost);
			//Log.d("mytest", "post body: " + postBody);
			//Log.d("exceptions", "responseBody: " + responseBody);	
				
				//get post text
				itemStart = postBody.indexOf("\"text\":\"") + "\"text\":\"".length();
				itemEnd = postBody.indexOf("\",", itemStart);
				String postText = postBody.substring(itemStart, itemEnd);
				postText = Html.fromHtml(postText).toString();
				if (postText != null)
	    			postText = postText.replace("\\", "");
			//Log.d("exceptions", "postText: " + postText);
			
				//get timestamp
				itemStart = postBody.indexOf("\"date\":") + "\"date\":".length();
				itemEnd = postBody.indexOf(",", itemStart);
				String timestampString = postBody.substring(itemStart, itemEnd);
				long timestamp = Long.parseLong(timestampString)*1000;
				
				//if there are attachments
				if (postBody.indexOf("\"attachments\":[") != -1) {
				
					//get attachments
					itemStart = postBody.indexOf("\"attachments\":[") + "\"attachments\":[".length();
					itemEnd = postBody.indexOf("],", itemStart);
					String attachments = postBody.substring(itemStart, itemEnd);
				//Log.d("exceptions", "attachments: " + attachments);
					
					int attachmentCursor = 0;
					//while there is another picture
					while (attachments.indexOf("\"photo\":", attachmentCursor) != -1) {
					//Log.d("mytest", "in another picture");
						
						//get photo content
						itemStart = attachments.indexOf("\"photo\":", attachmentCursor) + "\"photo\":".length();
						itemEnd = attachments.indexOf("}", itemStart);
						String photoContent = attachments.substring(itemStart + 1, itemEnd);
						
						attachmentCursor = attachments.indexOf("}", itemEnd + 1);
					//Log.d("exceptions", "photo content: " + photoContent);	
						
						//get photo source
						String photoSource;
						if (photoContent.indexOf("\"src_xxbig\":\"") != -1) {
							itemStart = photoContent.indexOf("\"src_xxbig\":\"") + "\"src_xxbig\":\"".length();
							itemEnd = photoContent.indexOf("\",", itemStart);
							photoSource = photoContent.substring(itemStart, itemEnd);
						} else {
							itemStart = photoContent.indexOf("\"src_big\":\"") + "\"src_big\":\"".length();
							itemEnd = photoContent.indexOf("\",", itemStart);
							photoSource = photoContent.substring(itemStart, itemEnd);
						}	
						photoSource = Html.fromHtml(photoSource).toString();
						if (photoSource != null)
			    			photoSource = photoSource.replace("\\", "");
						
					//Log.d("exceptions", "photo source: " + photoSource);
					
						pictures.add(photoSource);
					}
				}
				
				//creating new wall post item
				if (pictures.size() == 0) 
					pictures = null;
				WallPostItem wallPostItem = new WallPostItem(postText, pictures, timestamp);
				wallPostItems.add(wallPostItem);
				//Log.d("exceptions", "added new item: " + postText + "///" + pictures);
			}
			
		} catch (Exception e) {
			Log.d("exceptions", "problems with wall parsing: " + e.toString());
		}
		
		if (wallPostItems.size() == 0)
			return null;
		else 
			return wallPostItems;
	}
}
