

package com.pwnzinc.TrackMe;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class TrackMeDB {

    public static final String KEY_STATUS = "status";
    public static final String KEY_STAMP = "stamp";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "TrackMeDB";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    private static final String DATABASE_CREATE =
        "create table stamps (_id integer primary key autoincrement, "
        + "status string not null, stamp string not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "stamps";
    private static final int DATABASE_VERSION = 6;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL("create table sys_settings (_id integer primary key autoincrement, key string unique not null, value string not null);");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-1 days'), 'open')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-1 days', '+6 hours'), 'closed')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-2 days', '-4 hours'), 'open')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-2 days', '+4 hours'), 'closed')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-3 days', '-2 hours'), 'open')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-3 days', '+2 hours'), 'closed')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-4 days', '-3 hours'), 'open')");
            db.execSQL("insert into stamps('stamp', 'status') values (datetime('2012-12-07 12:34:27', '-4 days', '+3 hours'), 'closed')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS stamps");
            db.execSQL("DROP TABLE IF EXISTS sys_settings");
            onCreate(db);
        }
    }


    public TrackMeDB(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TrackMeDB open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }



    public long createStamp(String status) {


        mDb.execSQL("insert into stamps('stamp', 'status') values (datetime('now'), '"+status+"')");
        return 1;
    }

    public boolean isCurrentlyWorking() {
       Cursor m_cursor = mDb.rawQuery("select status from stamps order by _id desc limit 1", null);
       if (m_cursor.moveToFirst())
        {
            if (m_cursor.getString(m_cursor.getColumnIndex("status")).equals("open"))
            {
                return true;
            }
        }
            return false;

    }


    public Cursor fetchAllStamps() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_STAMP,
                KEY_STATUS}, null, null, null, null, null);
    }
    
    public Cursor getSetting(String key){
    	String args[] = {key};
		return mDb.rawQuery("select value from sys_settings where key =?", args);
    }
    

    
    public long setSetting(String key, String value) {
    		mDb.execSQL("replace into sys_settings('key', 'value') values ('"+key+"', '"+value+"')");
    	return 1;
    }
    
    public String getCourtesyMode() {
		Cursor m_cursor = mDb.rawQuery("select value from sys_settings where key='courtesyMode'", null);
		if (m_cursor.moveToFirst())
		{
			return m_cursor.getString(m_cursor.getColumnIndex("value"));
		}
			return "false";
    		
    }

    public String getWorkingSince() {
        Cursor m_cursor = mDb.rawQuery("select stamp from stamps where status='open' order by _id desc limit 1", null);
        if (m_cursor.moveToFirst())
        {
            return m_cursor.getString(m_cursor.getColumnIndex("stamp"));
        }
            return "false";
    }
    
    public String getWorkingSinceTime() {
        Cursor m_cursor = mDb.rawQuery("select time(stamp) as stamp from stamps where status='open' order by _id desc limit 1", null);
        if (m_cursor.moveToFirst())
        {
            return m_cursor.getString(m_cursor.getColumnIndex("stamp"));
        }
            return "false";
    }

    public String getTodaysHours() {
            Cursor m_cursor = mDb.rawQuery("select begin.stamp start, end.stamp end from stamps begin join stamps end on end._id = begin._id+1 and end.status = 'closed' where begin.status = 'open' and date(begin.stamp) = date('now')", null);
            Date starttime = null;
            Date endtime = null;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long total_seconds = 0;
            while (m_cursor.moveToNext())
    		{
            	try {
    				starttime = (Date)formatter.parse(m_cursor.getString(0));
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                
    			try {
    				endtime = (Date)formatter.parse(m_cursor.getString(1));
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
                total_seconds = total_seconds + ((endtime.getTime() - starttime.getTime()));
            }
                double diff = total_seconds / ((double) 1000*60*60);
                return String.valueOf((int)diff) + ":" + String.valueOf((int)((diff - (int)diff)*60));
            
    }
    
    public String getDaysHours(String date) {
        Cursor m_cursor = mDb.rawQuery("select begin.stamp start, end.stamp end from stamps begin join stamps end on end._id = begin._id+1 and end.status = 'closed' where begin.status = 'open' and date(begin.stamp) = "+date, null);
        Date starttime = null;
        Date endtime = null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long total_seconds = 0;
        while (m_cursor.moveToNext())
		{
        	try {
				starttime = (Date)formatter.parse(m_cursor.getString(0));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			try {
				endtime = (Date)formatter.parse(m_cursor.getString(1));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            total_seconds = total_seconds + ((endtime.getTime() - starttime.getTime()));
        }
            double diff = total_seconds / ((double) 1000*60*60);
            return String.valueOf((int)diff) + ":" + String.valueOf((int)((diff - (int)diff)*60));
        
}

    public String getMonthsHours() {
            Cursor m_cursor = mDb.rawQuery("select begin.stamp start, end.stamp end from stamps begin join stamps end on end._id = begin._id+1 and end.status = 'closed' where begin.status = 'open' and strftime('%m',datetime(begin.stamp)) = strftime('%m',datetime('now'))", null);
            Date starttime = null;
            Date endtime = null;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long total_seconds = 0;
            while (m_cursor.moveToNext())
            {
                try {
                    starttime = (Date)formatter.parse(m_cursor.getString(0));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                try {
                    endtime = (Date)formatter.parse(m_cursor.getString(1));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                total_seconds = total_seconds + ((endtime.getTime() - starttime.getTime()));
            }
                double diff = total_seconds / ((double) 1000*60*60);
                return String.valueOf((int)diff) + ":" + String.valueOf((int)((diff - (int)diff)*60));
            
    }
    
    public Cursor getStamp(String date)
    {
    	return mDb.rawQuery("select * from stamps where strftime('%Y-%m-%d', stamp)='"+date+"'" , null);
    	
    }



}
