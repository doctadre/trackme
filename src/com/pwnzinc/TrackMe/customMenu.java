package com.pwnzinc.TrackMe;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import java.io.File; 
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date; 
import java.util.Locale;

import jxl.*; 
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

    public class customMenu extends Activity {
    	
    	Button courtesySettingsButton, weekViewButton, timesheetButton;
    	CheckBox courtesyCheckBox;
    	private TrackMeDB mDbHelper;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            mDbHelper = new TrackMeDB(this);
                        
            
    	  setContentView(R.layout.menu);
        this.courtesySettingsButton = (Button) this.findViewById(R.id.courtesySettingsMenuButton);
        mDbHelper.open();
        Cursor cursor = mDbHelper.getSetting("courtesyMode");
        

        boolean courtesyMode = false;
        if (cursor.moveToFirst())
        {
        	courtesyMode = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("value")));
        }
        this.courtesySettingsButton.setEnabled(courtesyMode);
        this.courtesySettingsButton.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	      courtesyButtonClicked();
       	    }
       	    
       	  });
        
        
        this.courtesyCheckBox = (CheckBox) this.findViewById(R.id.courtesyModeToggle);
        this.courtesyCheckBox.setChecked(courtesyMode);
        this.courtesyCheckBox.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	      courtesyModeToggled();
       	    }
       	  });
        
        this.weekViewButton = (Button) this.findViewById(R.id.weekViewButton);
        this.weekViewButton.setOnClickListener(new OnClickListener() {
       	    public void onClick(View v) {
       	    	weekViewButtonClicked();
       	    }
       	    
       	  });

        this.timesheetButton = (Button) this.findViewById(R.id.timesheetButton);
        this.timesheetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
        				timesheetButtonClicked();

                    }
                    
                  });
    }
    	
    	private void courtesyModeToggled() {
    		
    		courtesySettingsButton.setEnabled(courtesyCheckBox.isChecked());
        mDbHelper.open();
    		mDbHelper.setSetting("courtesyMode", new Boolean(courtesyCheckBox.isChecked()).toString());
        

    	}
    	
    	private void courtesyButtonClicked(){
    		Intent courtesyMenu = new Intent(this, courtesyMenu.class);
    		this.startActivity(courtesyMenu); 
    		
    		
    	}
    	
    	private void weekViewButtonClicked() {
    		Intent weekView = new Intent(this, weekView.class);
    		this.startActivity(weekView); 
    	}

      private void timesheetButtonClicked() {


        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/TrackMe");
        directory.mkdirs();
        File file = new File(directory, "TrackMeTimesheet.xls");
        WorkbookSettings wbSettings = new WorkbookSettings();

                wbSettings.setLocale(new Locale("en", "EN"));

      	WritableWorkbook workbook = null;
  		try {
  			workbook = Workbook.createWorkbook(file, wbSettings);
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
      	WritableSheet sheet = workbook.createSheet("First Sheet", 0);
          Calendar calendar = Calendar.getInstance();
          int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
          calendar.set(Calendar.DAY_OF_MONTH, 1);
          int x = 0;
          int y = 0;
          String[] weekdays = new DateFormatSymbols().getWeekdays();
          for (int i = 1; i  <   days + 1; i+= 1 )
          {
            Label label = new Label(x, y, weekdays[calendar.get(Calendar.DAY_OF_WEEK) ]+ " - " + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH)); 
            Label hoursValue = new  Label(x,y+1,mDbHelper.getDaysHours( calendar.get(Calendar.YEAR) + "/" +  calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH)));
            try {
        			sheet.addCell(label);
        		} catch (RowsExceededException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (WriteException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} 
            try {
				sheet.addCell(hoursValue);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            x++;
            if (x==7)
            {
          	  y += 2;
          	  x = 0;
            }
            calendar.add(Calendar.DATE, 1);

          }

          try {
			workbook.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
          try {
			workbook.close();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        }
    
}