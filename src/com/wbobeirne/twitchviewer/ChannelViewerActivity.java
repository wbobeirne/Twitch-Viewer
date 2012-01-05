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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ChannelViewerActivity extends Activity {
	
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
        buildGameListView(i.getStringExtra("gameURL"));
	            
        dialog.dismiss();
    }
	
	
	private ListView buildGameListView(String gameURL){
    	ListView lv = new ListView(getApplicationContext());
    	ArrayList<Channel> channelList = new ArrayList<Channel>();
    	//Find and replace spaces with %20
    	gameURL = gameURL.replace(" ", "%20");
    	
    	try{
    		Log.d("Channels", "Grabbing channel page " + gameURL);
    		Document doc = Jsoup.connect(gameURL).get();
    		//String docText = doc.toString();
    		Element content = doc.getElementById("directory_channels");
    		//Since the div's name has 2 spaces in it, jsoup is shitting bricks
    		//Just grab them all, then wrap try/catches around the channel thing
    		Elements channels = content.select("div");
    		
    		for(Element channel : channels){
    			try{
	    			String username = channel.getElementsByClass("channelname").first().text();
	    			username = username.replace("on ", "");
	    			String title = channel.getElementsByClass("title").first().text();
	    			Log.d("Channels", channel.getElementsByClass("title").first().text());
	    			int viewCount = 0;//Integer.parseInt(channel.select("channel_count").toString());
	    			Drawable image = getImgFromUrl(channel.getElementsByClass("cap").first().absUrl("src"));
	    			
	    			Channel newChannel = new Channel(username, title, viewCount, image);
	    			channelList.add(newChannel);
    			}
    			catch(Exception e){
					Log.d("Channels", "Error: " + e.getMessage());
    			}
    		}
    		//The first one gets duplicated due to the way I search for em
    		try{
    			channelList.remove(0);
    		}
    		catch(Exception e){
    			Log.d("Channels", "No channels were found!");
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
			Toast favoriteToast = Toast.makeText(getApplicationContext(), "Could not connect to Twitch.tv", Toast.LENGTH_LONG);
    		favoriteToast.show();
		}
    	
    	return lv;
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
