//package com.pwnzinc.TrackMe;
//
//import android.app.ListActivity;
//import android.content.Context;
//import android.database.Cursor;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.provider.Settings.SettingNotFoundException;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.SimpleCursorAdapter;
//
//public class TrackMeActivity extends ListActivity {
//	
//	private Button TheButton, courtesySettingsButton;
//	private CheckBox courtesyCheckBox;
//	private boolean workStatus = false, courtesybool=false;
//	private Integer default_ringer_volume ;
//	private AudioManager amanager;  
//	private TrackMeDB mDbHelper;
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        
//        mDbHelper = new TrackMeDB(this);
//        mDbHelper.open();
//        
//        amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        
//         this.TheButton = (Button) this.findViewById(R.id.button);
//         this.TheButton.setText("Start Working!");
//         
//         
//         this.TheButton.setOnClickListener(new OnClickListener() {
//        	    public void onClick(View v) {
//        	      TheButtonClicked();
//        	    }		
//        	  });
//
//         try {
//				default_ringer_volume = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.VOLUME_RING);
//			} catch (SettingNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//         
//         
//      // Get all of the notes from the database and create the item list
//         Cursor c = mDbHelper.fetchAllStamps();
//         startManagingCursor(c);
//
//         String[] from = new String[] { TrackMeDB.KEY_STAMP };
//         int[] to = new int[] { R.id.text1 };
//         
//         // Now create an array adapter and set it to display using our row
//         SimpleCursorAdapter notes =
//             new SimpleCursorAdapter(this, R.layout.stamps_row, c, from, to);
//         setListAdapter(notes);
//         
//
//    }
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    	
//    	setContentView(R.layout.menu);
//        this.courtesySettingsButton = (Button) this.findViewById(R.id.coutesySettingsMenuButton);
//        this.courtesySettingsButton.setOnClickListener(new OnClickListener() {
//       	    public void onClick(View v) {
//       	      courtesyButtonClicked();
//       	    }
//       	    
//       	  });
//        
//        Button okButton = (Button) this.findViewById(R.id.mainMenuOkButton);
//        okButton.setOnClickListener(new OnClickListener() {
//       	    public void onClick(View v) {
//       	    	setContentView(R.layout.main);
//       	    	TheButton = (Button) findViewById(R.id.button);
//                TheButton.setText("Start Working!");
//                TheButton.setOnClickListener(new OnClickListener() {
//               	    public void onClick(View v) {
//               	      TheButtonClicked();
//               	    }		
//               	  });
//       	    }
//       	    
//       	  });
//        
//        this.courtesyCheckBox = (CheckBox) this.findViewById(R.id.coutesyModeToggle);
//        this.courtesyCheckBox.setOnClickListener(new OnClickListener() {
//       	    public void onClick(View v) {
//       	      courtesyModeToggled();
//       	    }
//       	  });
//        return true;
//    }
//    
//    
//    
//	private void courtesyModeToggled() {
//		
//		courtesySettingsButton.setEnabled(courtesyCheckBox.isChecked());
//		courtesybool=courtesyCheckBox.isChecked();
//
//	}
//	
//	private void courtesyButtonClicked(){
//		
//		setContentView(R.layout.courtesymenu);
//	}
//	
//	
//    private void TheButtonClicked() {
//    	 String button_text = (String) TheButton.getText();
//    	if (button_text == "Start Working!"){
//    		TheButton.setText("Stop Working!");
//    		mDbHelper.createStamp("open");
//    		Cursor allStamps =  mDbHelper.fetchAllStamps();
//    		startManagingCursor(allStamps);
//    		allStamps.moveToLast();
//    		workStatus=true;
//    		handleCourtesySettings();
//    	}
//    	else{
//    		TheButton.setText("Start Working!");
//    		workStatus=false;
//    		handleCourtesySettings();
//    	}
//    	
//    	 Cursor c = mDbHelper.fetchAllStamps();
//         startManagingCursor(c);
//
//         String[] from = new String[] { TrackMeDB.KEY_STAMP };
//         int[] to = new int[] { R.id.text1 };
//         
//         // Now create an array adapter and set it to display using our row
//         SimpleCursorAdapter notes =
//             new SimpleCursorAdapter(this, R.layout.stamps_row, c, from, to);
//         setListAdapter(notes);
//		
//	}
//    
//    private void handleCourtesySettings(){
//    		if (workStatus & courtesybool){
//    			try {
//					default_ringer_volume = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.VOLUME_RING);
//				} catch (SettingNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    			
//    			amanager.setStreamVolume(AudioManager.STREAM_RING,amanager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
//    			
//    			
//    		}
//    		else if (!workStatus & courtesybool){
//    			
//    			amanager.setStreamVolume(AudioManager.STREAM_RING,default_ringer_volume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND); 
//    			
//    		}
//    					
//    			
//    		
//    }
//    
//    
//    
//
//}