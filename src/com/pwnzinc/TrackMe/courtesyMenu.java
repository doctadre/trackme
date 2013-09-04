package com.pwnzinc.TrackMe;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

    public class courtesyMenu extends Activity {
    	
    	
    	
    	
    	CheckBox ringerCheckBox, vibrateCheckBox;
    	SeekBar ringerSlider;
    	private TrackMeDB mDbHelper;
    	private SeekBar mSeekBar;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setContentView(R.layout.courtesymenu);
            
            mDbHelper = new TrackMeDB(this);
            mDbHelper.open();
            

        Cursor ringer_cursor = mDbHelper.getSetting("workRingerMode");
        boolean workRingerMode = false;
        if (ringer_cursor.moveToFirst())
        {
        	workRingerMode = Boolean.parseBoolean(ringer_cursor.getString(ringer_cursor.getColumnIndex("value")));
        }
        
        this.ringerCheckBox = (CheckBox) this.findViewById(R.id.workRingerCheckbox);
        this.ringerCheckBox.setChecked(workRingerMode);
        this.ringerCheckBox.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	    	workRingerToggled();
       	    }
        });
        
        Cursor cursor = mDbHelper.getSetting("workVibrateMode");
        boolean workVibrateMode = false;
        if (cursor.moveToFirst())
        {
        	workVibrateMode = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("value")));
        }
        
        this.vibrateCheckBox = (CheckBox) this.findViewById(R.id.workVibrateCheckbox);
        this.vibrateCheckBox.setChecked(workVibrateMode);
        this.vibrateCheckBox.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	    	workVibrateToggled();
       	    }
       	  });
        
        mSeekBar = (SeekBar) findViewById(R.id.ringerSlider);
        int workRingerVolume =0;
		cursor = mDbHelper.getSetting("workRingerVolume");
		if (cursor.moveToFirst())
        {
        	workRingerVolume = Integer.parseInt(cursor.getString(cursor.getColumnIndex("value")));
        }
		mSeekBar.setProgress(workRingerVolume);
        mSeekBar.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
        {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
        	mDbHelper.setSetting("workRingerVolume", Integer.toString(progress));                                                      
           
        }

              public void onStartTrackingTouch(SeekBar seekBar){}
              public void onStopTrackingTouch(SeekBar seekBar){}
        });
    	
//        Cursor slider = mDbHelper.getSetting("workVolumeSetting");
//        int workVolumeSetting;
//        if (ringer_cursor.moveToFirst())
//        {
//        	workRingerMode = Boolean.parseBoolean(ringer_cursor.getString(ringer_cursor.getColumnIndex("value")));
//        }
    }
    	
    	public void workRingerToggled(){
    		mDbHelper.setSetting("workRingerMode", new Boolean(ringerCheckBox.isChecked()).toString());
    	}
    	
    	public void workVibrateToggled(){
    		mDbHelper.setSetting("workVibrateMode", new Boolean(vibrateCheckBox.isChecked()).toString());
    	}
    	
    }