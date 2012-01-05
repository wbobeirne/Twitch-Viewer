/********************
 * 	Custom list view adapter with help from http://www.pocketmagic.net/?p=1343
 */

package com.wbobeirne.twitchviewer;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ChannelAdapter extends BaseAdapter {
	
	private Context context;
	private List<Channel> channelList;
	
	public ChannelAdapter(Context context, List<Channel> channelList){
		
		this.context = context;
		this.channelList = channelList;
		
	}
	
	public int getCount() {
        return channelList.size();
    }
 
    public Object getItem(int position) {
        return channelList.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent){
    	
    	Channel channel = (Channel)getItem(position);
    	View v = new ChannelView(this.context, channel);
    	
    	return v;
    }

}
