package com.pwnzinc.TrackMe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;




public class TrackMeActivity extends Activity {
	
	private Button TheButton;
	private TextView resultsTextBox;
    private TextView todaysHoursBox;
    private TextView monthsHoursBox;
	
	boolean workStatus = false;
	private Integer default_ringer_volume;
	private AudioManager amanager;  
	private TrackMeDB mDbHelper;
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		Intent customMenu = new Intent(this, customMenu.class);
		this.startActivity(customMenu); 

	    return true;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        mDbHelper = new TrackMeDB(this);
        mDbHelper.open();
        workStatus = mDbHelper.isCurrentlyWorking();

        
        amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        
         renderMainView();  


    }



    
    public void renderMainView(){
    	setContentView(R.layout.main);
        mDbHelper.open();
        workStatus = mDbHelper.isCurrentlyWorking();

        this.TheButton = (Button) this.findViewById(R.id.button);
        this.resultsTextBox = (TextView) this.findViewById(R.id.query_result);
        this.todaysHoursBox = (TextView) this.findViewById(R.id.todayshoursbox);
        this.monthsHoursBox = (TextView) this.findViewById(R.id.monthshoursbox);
        if (!workStatus)
        {
            this.TheButton.setText("Start Working!");
        }
        else
        {
            this.TheButton.setText("Stop Working");
            mDbHelper.open();
            resultsTextBox.setText("Started Working " + mDbHelper.getWorkingSinceTime() );
    
        }
        
        mDbHelper.open();
        todaysHoursBox.setText("Hours Today: " + mDbHelper.getTodaysHours() );
        monthsHoursBox.setText("Hours This Month: " + mDbHelper.getMonthsHours() );

        
        
        this.TheButton.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	      TheButtonClicked();
       	    }		
       	  });

        try {
				default_ringer_volume = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.VOLUME_RING);
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }



    
    
    

	
	
	
	
    private void TheButtonClicked() {
    	 String button_text = (String) TheButton.getText();
         // RemoteViews remoteView = new RemoteViews("com.pwnzinc.TrackmeWidgetProvider",
         //    R.layout.trackme_appwidget_layout);
         mDbHelper.open();
    	if (button_text == "Start Working!"){
    		TheButton.setText("Stop Working!");
    		mDbHelper.createStamp("open");
    		workStatus=true;
    		handleCourtesySettings();
    		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    		Date date = new Date();
    		resultsTextBox.setText("Started Working " + dateFormat.format(date) );
            todaysHoursBox.setText("Today's hours: " + mDbHelper.getTodaysHours() );
            monthsHoursBox.setText("Hours This Month: " + mDbHelper.getMonthsHours() );
    	}
    	else{
    		TheButton.setText("Start Working!");
    		mDbHelper.createStamp("closed");
    		workStatus=false;
    		handleCourtesySettings();
    		resultsTextBox.setText("You are currently not at work");
            todaysHoursBox.setText("Today's hours: " + mDbHelper.getTodaysHours() );
            monthsHoursBox.setText("Hours This Month: " + mDbHelper.getMonthsHours() );
    	}


        // ComponentName trackmeWidget = new ComponentName(context,
        //     TrackmeWidgetProvider.class);
        // appWidgetManager.updateAppWidget(trackmeWidget, remoteView);
    	// Context context = getApplicationContext();
    	// context.startService(new Intent(context, TrackmeWidgetProvider.class));
        // Intent updateWidget = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        Intent updateWidget = new Intent(this, TrackmeWidgetProvider.class);
        updateWidget.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        this.sendBroadcast(updateWidget);
    	
		
	}



    
    private void handleCourtesySettings(){
    	mDbHelper.open();
    	Cursor cursor = mDbHelper.getSetting("courtesyMode");
        boolean courtesybool = false, workVibrateMode = false;
        boolean workRingerMode = false;
        int workRingerVolume =0;
        if (cursor.moveToFirst())
        {
        	courtesybool = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("value")));
        }
        cursor = mDbHelper.getSetting("workRingerMode");
		if (cursor.moveToFirst())
        {
        	workRingerMode = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("value")));
        }
		cursor = mDbHelper.getSetting("workRingerVolume");
		if (cursor.moveToFirst())
        {
        	workRingerVolume = Integer.parseInt(cursor.getString(cursor.getColumnIndex("value")));
        }
		cursor = mDbHelper.getSetting("defaultRingerVolume");
		if (cursor.moveToFirst())
        {
			default_ringer_volume = Integer.parseInt(cursor.getString(cursor.getColumnIndex("value")));
        }
		cursor = mDbHelper.getSetting("workVibrateMode");
		if (cursor.moveToFirst())
        {
			workVibrateMode = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("value")));
        }
    		if (workStatus & courtesybool){
    			mDbHelper.setSetting("defaultRingerVolume",Integer.toString(amanager.getStreamVolume(2)));//Integer.toString(this.getVolumeControlStream())); // //Integer.toString(android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.VOLUME_RING)));
				
    			mDbHelper.setSetting("defaultVibrateMode", Integer.toString(amanager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER)));
    			
    			if (workRingerMode)
    			{	
    				
    				amanager.setStreamVolume(AudioManager.STREAM_RING,workRingerVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
    			}
    			if (workVibrateMode)
    			{
    				amanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);

    			}
    			else
    			{
    				amanager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);

    			}
    			
    			
    		}
    		else if (!workStatus & courtesybool){
    			if (workRingerMode)
    			{
    			amanager.setStreamVolume(AudioManager.STREAM_RING,default_ringer_volume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
    			}
    			
    		}
    			
        		
    			
    		
    }
    @Override
	public void onResume() {
        mDbHelper.open();
        workStatus = mDbHelper.isCurrentlyWorking();

	   if (!workStatus)
        {
            this.TheButton.setText("Start Working!");
            resultsTextBox.setText("You are currently not at work");
        }
        else
        {
            this.TheButton.setText("Stop Working");
            resultsTextBox.setText("Started Working " + mDbHelper.getWorkingSinceTime() );
        }
        
        
        todaysHoursBox.setText("Hours Today: " + mDbHelper.getTodaysHours() );
        monthsHoursBox.setText("Hours This Month: " + mDbHelper.getMonthsHours() );

      super.onResume();
	}
    
    
    

}