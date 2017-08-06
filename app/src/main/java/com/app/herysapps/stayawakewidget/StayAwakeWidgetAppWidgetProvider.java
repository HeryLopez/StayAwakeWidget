package com.app.herysapps.stayawakewidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by Hery on 05/08/2017.
 * Created with information recovery from internet and the google guides.
 *
 */
public class StayAwakeWidgetAppWidgetProvider extends AppWidgetProvider {

    private static final String BUTTON_SETTINGS = "ButtonSettings";
    private static final String BUTTON_RESET    = "ButtonReset";

    /*
    * Is called in two ways:
    * - According the app_widget_provider_info.xml config (updatePeriodMillis).
    * - When the user set it in home screen (if there is not config activity, see google guides)
    * */
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent with awake action
            Intent intentSettings = new Intent(context, getClass());
            intentSettings.setAction(BUTTON_SETTINGS);
            PendingIntent pendingIntentSettings = PendingIntent.getBroadcast(context, 0, intentSettings, 0);

            // Create an Intent with reset action
            Intent intentReset = new Intent(context, getClass());
            intentReset.setAction(BUTTON_RESET);
            PendingIntent pendingIntentReset = PendingIntent.getBroadcast(context, 0, intentReset, 0);

            // Get the layout for the App Widget and attach an on-click listener to the buttons
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_stay_awake_widget);
            views.setOnClickPendingIntent(R.id.imageButtonSettings, pendingIntentSettings);
            views.setOnClickPendingIntent(R.id.imageButtonReset, pendingIntentReset);

            // Update interface
            updateInterface(context, views);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /*
    * Is called when user has interface interactions
    * */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line

        if(intent.getAction().equals(BUTTON_SETTINGS)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_stay_awake_widget);
            ComponentName watchWidget = new ComponentName(context, StayAwakeWidgetAppWidgetProvider.class);

            // Open settings
            context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));

            // Update interface
            updateInterface(context, views);

            // Update widget
            appWidgetManager.updateAppWidget(watchWidget, views);
        }


        if(intent.getAction().equals(BUTTON_RESET)){

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_stay_awake_widget);
            ComponentName watchWidget = new ComponentName(context, StayAwakeWidgetAppWidgetProvider.class);

            // Update interface
            updateInterface(context, views);

            // Update widget
            appWidgetManager.updateAppWidget(watchWidget, views);
        }
    }

    // Changes in the interface
    private void updateInterface(Context context, RemoteViews views){

        int value = Settings.Global.getInt(context.getContentResolver(), Settings.Global.STAY_ON_WHILE_PLUGGED_IN, 0);
        boolean checked = (value & (BatteryManager.BATTERY_PLUGGED_AC | BatteryManager.BATTERY_PLUGGED_USB)) != 0;

        if(checked) {
            views.setViewVisibility(R.id.textViewActive, View.VISIBLE);
            views.setViewVisibility(R.id.textViewInactive, View.INVISIBLE);

            views.setViewVisibility(R.id.textViewDescActive, View.VISIBLE);
            views.setViewVisibility(R.id.textViewDescInactive, View.INVISIBLE);

            views.setViewVisibility(R.id.imageButtonWarning, View.VISIBLE);

        } else {
            views.setViewVisibility(R.id.textViewActive, View.INVISIBLE);
            views.setViewVisibility(R.id.textViewInactive, View.VISIBLE);

            views.setViewVisibility(R.id.textViewDescActive, View.INVISIBLE);
            views.setViewVisibility(R.id.textViewDescInactive, View.VISIBLE);

            views.setViewVisibility(R.id.imageButtonWarning, View.GONE);
        }
    }
}