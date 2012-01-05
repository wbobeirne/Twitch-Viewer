/********************
 * 	Custom list view entries with help from http://www.pocketmagic.net/?p=1343
 */

package com.wbobeirne.twitchviewer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChannelView extends LinearLayout {
	
	public ChannelView(Context context, Channel channel){
		
		super(context);
		
		//Set LinearLayout attributes
		this.setId(channel.username.hashCode());
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setPadding(0, 6, 0, 6);
		
		//Image attributes
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(6, 0, 6, 0);
		
		//set the image
		ImageView iv = new ImageView(context);
		iv.setImageDrawable(channel.image);
		//Add the image to the view
		addView(iv, params);
		
		//Vertical layer for main text and sub text
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout panelV = new LinearLayout(context);
		panelV.setOrientation(LinearLayout.VERTICAL);
		panelV.setGravity(Gravity.BOTTOM);
		
		//Channel title
		TextView textName = new TextView( context );
		textName.setTextSize(12);
		textName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		textName.setText( channel.title);
		panelV.addView(textName);       
		
		//Channel's user name
		TextView textAddress = new TextView( context );
		textAddress.setTextSize(12);
		textAddress.setText( channel.username);
		panelV.addView(textAddress);
		
		this.setTag(channel.username);
 
		addView(panelV, params);
	}

}
