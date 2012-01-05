package com.wbobeirne.twitchviewer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ChannelWebViewActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Make activity fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        Intent i = getIntent();
        
        launchWebView(i.getStringExtra("channelName"));
        
        //scrapePlayer(i.getStringExtra("channelName"));
	}
	
	private void launchWebView(String channelName){
		Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();
        int height = display.getHeight();
        
		String html = "<object type=\"application/x-shockwave-flash\" height=\"" + height + "\" width=\"" + width + "\" id=\"live_embed_player_flash\" data=\"http://www.twitch.tv/widgets/live_embed_player.swf?channel=" + channelName + " bgcolor=\"#000000\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowScriptAccess\" value=\"always\" /><param name=\"allowNetworking\" value=\"all\" /><param name=\"movie\" value=\"http://www.twitch.tv/widgets/live_embed_player.swf\" /><param name=\"flashvars\" value=\"hostname=www.twitch.tv&channel=ezsickmotion&auto_play=false&start_volume=25\" /></object><a href=\"http://www.twitch.tv/ezsickmotion#r=-rid-&amp;s=em\" class=\"trk\" style=\"padding:2px 0px 4px; display:block; width:345px; font-weight:normal; font-size:10px; text-decoration:underline; text-align:center;\">Watch live video from " + channelName + " on www.twitch.tv</a>";
		String html2 = "<div class='live_site_player_container swf_container' id='standard_holder' style='width: " + width + "px; height:" + height + "px'><div class='swf_container' id='live_site_player_flash'>You need Adobe Flash Player to watch this video.<br><a href=\"http://get.adobe.com/flashplayer/\">Download it from Adobe.</a><script>  //<![CDATA[    swfobject.embedSWF(\"http://www-cdn.justin.tv/widgets/live_site_player.r716b559fc249273917f31bebe19bdadb79e8c0a7.swf\", \"live_site_player_flash\", \"640px\", \"387px\", \"9\", \"/widgets/expressinstall.swf\", {\"publisherGuard\":null,\"hide_chat\":\"true\",\"searchquery\":null,\"backgroundImageUrl\":\"http://static-cdn.jtvnw.net/jtv_user_pictures/anonymous_monkey-320x240.jpg\",\"channel\":\"angrytestie\",\"auto_play\":\"true\",\"hostname\":\"www.twitch.tv\",\"publisherTimezoneOffset\":300}, {\"allowNetworking\":\"all\",\"allowScriptAccess\":\"always\",\"allowFullScreen\":\"true\",\"wmode\":\"opaque\"}, \"\");  //]]></script></div></div>";
		
		setContentView(R.layout.channel_web_view);
		WebView webView;
		webView = (WebView)findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		//webView.loadData(html, "text/html", "UTF-8");
		
		//CURRENTLY WORKING, JUST NOT WELL
		webView.loadUrl("http://www.twitch.tv/widgets/live_embed_player.swf?channel=" + channelName + "&auto_play=true");
		
		//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitch.tv/widgets/live_embed_player.swf?channel=" + v.getTag().toString()));
		//startActivity(browserIntent);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	
	private void scrapePlayer(String channelName){
		try{
			Document doc = Jsoup.connect("www.twitch.tv/" + channelName).get();
			Element player = doc.getElementById("standard_holder");
			
			WebView webView = (WebView)findViewById(R.id.webview);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setPluginsEnabled(true);
			webView.setWebViewClient(new WebViewClient());
			
			webView.loadData(player.html(), "text/html", "UTF-8");
		}
		catch(Exception e){
			Toast favoriteToast = Toast.makeText(getApplicationContext(), "Could not connect to Twitch.tv", Toast.LENGTH_LONG);
    		favoriteToast.show();
		}
	}
}
