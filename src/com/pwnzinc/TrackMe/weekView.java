package com.pwnzinc.TrackMe;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

    public class weekView extends Activity {
    	
    	
    	
    	
    	private ListView lv1;
    	//DatePicker selectDate;
    	Button dateButton;
    	private int mYear;
        private int mMonth;
        private int mDay;
        static final int DATE_DIALOG_ID = 0;
    	

    	private TrackMeDB mDbHelper;
    	
    	@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mDbHelper = new TrackMeDB(this);
            mDbHelper.open();
            setContentView(R.layout.weekview);
            
            lv1 = (ListView) findViewById (R.id.listView1);
            dateButton= (Button) findViewById (R.id.dateButton);
            dateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDialog(DATE_DIALOG_ID);
                }
            });

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            updateDisplay();


          
          
            

        
    }
    	
    	private void updateDisplay(){
    		DateFormat formatter,timeformatter,fullformatter ; 
    		String date = mYear+"-"+(mMonth+1)+"-"+mDay;
    		formatter = new SimpleDateFormat("yyyy-MM-dd");
    		fullformatter = new SimpleDateFormat("yyyy-MM-dd h:mm:ss");
    		timeformatter = new SimpleDateFormat("h:mm:ss a");
    		try {
				date = formatter.format(formatter.parse(date));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		Cursor cursor;
		
			cursor = mDbHelper.getStamp(date);
			
    		
			dateButton.setText(date);
			
            String[] s1 = null;
            if (cursor != null)
            {
            	s1 = new String[cursor.getCount()];  
            	cursor.moveToFirst();
            }
            for (int i = 0; i < cursor.getCount(); i ++)
            {
            	String tmp;
            	if (cursor.getString(1).equals("open"))
            	{
            		tmp = "Started Working";
            	}
            	else
            	{
            		tmp = "Stopped Working";
            	}
            	String time = "0";
				try {
					time = timeformatter.format(fullformatter.parse(cursor.getString(2)));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	s1[i] = tmp+" " + time;
            	cursor.moveToNext();
            }
    
            lv1.setAdapter(new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, s1));
    	}
    	
    	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, 
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        updateDisplay();
                    }
                };
    	

    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    }