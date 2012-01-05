package com.wbobeirne.twitchviewer;

import android.graphics.drawable.Drawable;

public class Channel {
	
	public String username;
	public String title;
	public int viewerCount;
	public Drawable image;
	
	public Channel(String uname, String ttle, int vCount, Drawable img){
		
		username = uname;
		title = ttle;
		viewerCount = vCount;
		image = img;
		
	}

}
