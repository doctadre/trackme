package com.pwnzinc.TrackMe;

import com.pwnzinc.TrackMe.R;
import com.pwnzinc.TrackMe.R.layout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

public class TrackmeWidgetProvider extends AppWidgetProvider {
   public static final String DEBUG_TAG = "TrackmeWidgetProvider";
   @Override
   public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {
      try {
         updateWidgetContent(context, appWidgetManager);
      } catch (Exception e) {
         Log.e(DEBUG_TAG, "Failed", e);
      }
   }
   public static void updateWidgetContent(Context context,
      AppWidgetManager appWidgetManager) {
	   final TrackMeDB db = new TrackMeDB(context);
      db.open();
      Boolean workstatus = db.isCurrentlyWorking();
      
      // String [] projection = {TutListDatabase.COL_TITLE};
      // Uri content = TutListProvider.CONTENT_URI;
      // Cursor cursor = context.getContentResolver().query(content, projection,
      //       null, null, TutListDatabase.COL_DATE + " desc LIMIT 1");
      // if (cursor.moveToFirst()) {
      //       strLatestTitle = cursor.getString(0);
      // }
      // cursor.close();
      RemoteViews remoteView = new RemoteViews(context.getPackageName(),
            R.layout.trackme_appwidget_layout);
      if (workstatus)
      {
         remoteView.setTextViewText(R.id.button1, "Stop Working");
      }
      else
      {
         remoteView.setTextViewText(R.id.button1, "Start Working");
      }


      Intent active = new Intent(context, TrackmeWidgetProvider.class);
      active.setAction("createStamp");
      PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
      remoteView.setOnClickPendingIntent(R.id.button1, actionPendingIntent);


      ComponentName trackmeWidget = new ComponentName(context,
            TrackmeWidgetProvider.class);
      appWidgetManager.updateAppWidget(trackmeWidget, remoteView);
   }

   @Override
   public void onReceive(Context context, Intent intent) {
      RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.trackme_appwidget_layout);
      final TrackMeDB db = new TrackMeDB(context);
         db.open();
      if (intent.getAction().equals("createStamp")) {
         
         if (db.isCurrentlyWorking())
         {
        	 Toast.makeText(context, "Stopped Working", Toast.LENGTH_SHORT).show();
            db.createStamp("closed");
            remoteView.setTextViewText(R.id.button1, "Start Working");
            
         }
         else
         {
        	 Toast.makeText(context, "Started Working", Toast.LENGTH_SHORT).show();
            db.createStamp("open");
            remoteView.setTextViewText(R.id.button1, "Stop Working");
         }
        }
        
      ComponentName TrackMeWidget = new ComponentName(context.getPackageName(), TrackmeWidgetProvider.class.getName());
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
      int[] appWidgetIds = appWidgetManager.getAppWidgetIds(TrackMeWidget);
      onUpdate(context,appWidgetManager, appWidgetIds);
      super.onReceive(context, intent);
   }
}
