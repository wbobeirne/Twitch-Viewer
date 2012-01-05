package com.wbobeirne.twitchviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChannelSearchViewActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading, please wait...");
        
        new Thread(new Runnable(){
        	public void run(){
        		//dialog.show();
        	}
        }).start();
        
    	Intent i = getIntent();
        buildSearchListView(i.getStringExtra("searchURL"));
	            
        dialog.dismiss();
    }
	
	private void buildSearchListView(String searchURL){
		ListView lv = new ListView(getApplicationContext());
    	ArrayList<Channel> channelList = new ArrayList<Channel>();
    	
    	try{
    		
    		Document doc = Jsoup.connect(searchURL).get();
    		Element content = doc.getElementsByClass("grid").first();
    		//Since the div's name has 2 spaces in it, jsoup is shitting bricks
    		//Just grab them all, then wrap try/catches around the channel thing
    		Elements channels = content.getElementsByClass("live");
    		Log.d("Search", "Found " + channels.size() + " channels in search");
    		
    		for(Element channel : channels){
    			try{
    				String username = channel.getElementsByClass("channelname").first().text();
    				username = username.replace("on ", "");
    				Log.d("Search", username);
    				String title = channel.getElementsByClass("title").first().text();
    				Log.d("Search", title);
    				int viewCount = 0;
    				//As it stands, returns a blank image because Twitch lazy
    				//loads search thumbnails. Must look up a way to handle that.
    				Drawable image = getImgFromUrl(channel.getElementsByClass("cap").first().absUrl("src"));
    				
    				Channel newChannel = new Channel(username, title, viewCount, image);
	    			channelList.add(newChannel);
    			}
    			catch(Exception e){
    				Log.d("Search", "Error in searching: " + e.getMessage());
    			}
    		}
    		
    		ChannelAdapter lvAdapter = new ChannelAdapter(getApplicationContext(), channelList);
    		lv.setAdapter(lvAdapter);
    		//The on click event for channels, open the web view and imbed the player
    		lv.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View v,
						int arg2, long arg3) {
					
					Intent i = new Intent(getApplicationContext(), ChannelWebViewActivity.class);
		    		i.putExtra("channelName", v.getTag().toString());
		    		startActivity(i);
				}
    			
    		});
    		
    		setContentView(lv);
    		
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("Search", "Could not connect to Twitch.tv");
			Toast favoriteToast = Toast.makeText(getApplicationContext(), "Could not connect to Twitch.tv", Toast.LENGTH_LONG);
    		favoriteToast.show();
		}
		
	}
	
	private Drawable getImgFromUrl(String url){
    	
    	try{
    		InputStream is = (InputStream) new URL(url).getContent();
    		Drawable d = Drawable.createFromStream(is, "src");
	    	return d;
	    	
	    } catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
    }

}
