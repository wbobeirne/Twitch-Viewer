package com.wbobeirne.twitchviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class TwitchViewerActivity extends Activity {
	
	public final String TwitchURL = "http://twitch.tv/";
	public final String PatchNotesURL = "http://wbobeirne.github.com/tvpatchnotes.html";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.main);
        
        //LayoutInflater inflater = getLayoutInflater();
    	//LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.main, null);
    	
    	fillInImageButtons();
    	setButtonEventHandlers();
    	//checkForUpdate();
    }
    
    private void setButtonEventHandlers() {
		//Set the event handlers for the 3 static buttons
    	//Search
    	//search?utf8=✓&q="search"
    	ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
    	searchButton.setOnClickListener(searchButtonListener);
    	
    	ImageButton browseTopButton = (ImageButton)findViewById(R.id.browseButton);
    	browseTopButton.setOnClickListener(browseTopButtonListener);
    	
    	ImageButton favoritesButton = (ImageButton)findViewById(R.id.favoritesButton);
    	favoritesButton.setOnClickListener(favoritesButtonListener);
		
	}

	private void fillInImageButtons(){
    	
		Resources res = getResources();
		
		try {
			//Build an array of buttons to attach images to
			ImageButton[] buttonArray = new ImageButton[6];
			buttonArray[0] = (ImageButton)findViewById(R.id.popularGame1);
			buttonArray[1] = (ImageButton)findViewById(R.id.popularGame2);
			buttonArray[2] = (ImageButton)findViewById(R.id.popularGame3);
			buttonArray[3] = (ImageButton)findViewById(R.id.popularGame4);
			buttonArray[4] = (ImageButton)findViewById(R.id.popularGame5);
			buttonArray[5] = (ImageButton)findViewById(R.id.popularGame6);
			//Grab the twitch.tv main page
			Document doc = Jsoup.connect(TwitchURL).get();
			//Grab all the popular game elements
			Element content = doc.getElementById("top_games");
			Elements topGames = content.getElementsByClass("framed_boxart");
			int i = 0;
			for(Element game : topGames){
				Element image = game.getElementsByClass("boxart").first();
				String url = image.absUrl("src");
				Log.d("URL", url);
				buttonArray[i].setImageDrawable(combineDrawables(res.getDrawable(R.drawable.boxart_case), getImgFromUrl(url)));
				buttonArray[i].setOnClickListener(gameButtonListener);
				buttonArray[i].setTag(game.absUrl("href"));
				Log.d("ImageButton tag", game.absUrl("href"));
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
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
    
    private OnClickListener gameButtonListener = new OnClickListener() {
    	public void onClick(View v){
    		Log.d("URL", v.getTag().toString());
    		Intent i = new Intent(TwitchViewerActivity.this, ChannelViewerActivity.class);
    		i.putExtra("gameURL", v.getTag().toString());
    		startActivity(i);
    	}
    };
    
    //Credit to AndroidSnippets for help on the dialog
    //http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
    private OnClickListener searchButtonListener = new OnClickListener() {
    	public void onClick(View v){
    		// Create a text dialog, then search with the input text
			AlertDialog.Builder alert = new AlertDialog.Builder(TwitchViewerActivity.this);
		
			alert.setTitle("Search");
		
			// Set an EditText view to get user input 
			final EditText input = new EditText(TwitchViewerActivity.this);
			alert.setView(input);
		
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					//Create the search URL with the input value, start new channel viewer
				  	Intent i = new Intent(TwitchViewerActivity.this, ChannelSearchViewActivity.class);
		    		i.putExtra("searchURL", TwitchURL + "search?only=live&q=" + value);
		    		startActivity(i);
				}
			});
		
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});
		
			alert.show();
    	}
    };
    
    private OnClickListener browseTopButtonListener = new OnClickListener() {
    	public void onClick(View v){
    		Intent i = new Intent(TwitchViewerActivity.this, ChannelViewerActivity.class);
    		i.putExtra("gameURL", TwitchURL + "directory/all");
    		startActivity(i);
    	}
    };
    
    private OnClickListener favoritesButtonListener = new OnClickListener() {
    	public void onClick(View v){
    		Toast favoriteToast = Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_LONG);
    		favoriteToast.show();
    	}
    };
    
    
    public Drawable combineDrawables(Drawable topLayer, Drawable bottomLayer){
    	Drawable[] layers = new Drawable[2];
    	
    	layers[0] = bottomLayer;
    	layers[1] = topLayer;
    	
    	LayerDrawable layerDrawable = new LayerDrawable(layers);
    	
    	return layerDrawable;
    }
    
    private void checkForUpdate(){
    	//Check for first time run/new version, and if it is, throw open a popupdialog 
        //with patch notes/new version alert
        boolean firstrun = getSharedPreferences("TwitchPrefs", MODE_PRIVATE).getBoolean("firstrun", true);
        String currentVersion = "nadda";
		try {
			currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e1) {
			Toast favoriteToast = Toast.makeText(getApplicationContext(), "Error retrieving version #", Toast.LENGTH_LONG);
    		favoriteToast.show();
		}
        String newestVersion = "nope";
        String patchNotes = "Could not pull patch notes";
        try{
        	Document doc = Jsoup.connect(PatchNotesURL).get();
        	newestVersion = doc.getElementById("version_number").text();
        	patchNotes = doc.getElementById("patch_notes").text();
        }
        catch(Exception e){
        	Toast favoriteToast = Toast.makeText(getApplicationContext(), "Could not pull patch notes", Toast.LENGTH_LONG);
    		favoriteToast.show();
        }
        	
        if(firstrun || !newestVersion.equals(currentVersion)){
        	TextView tv = (TextView)findViewById(R.id.patchNotesText);
        	tv.setText(patchNotes);
        	
        	Display display = getWindowManager().getDefaultDisplay();
        	LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	PopupWindow pw = new PopupWindow(
        			inflater.inflate(R.layout.patch_notes_popup, null, false),
        			display.getWidth() - 50,
        			display.getHeight() - 100,
        			true);
        	pw.showAtLocation(findViewById(R.layout.main), Gravity.CENTER, 0, 0);
        	
        	//Now save that we've run this
        	getSharedPreferences("TwitchPrefs", MODE_PRIVATE)
        		.edit().putBoolean("firstrun", false).commit();
        }
    }
    
    
}