package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Igor on 02/04/2017.
 *
 * Part of the widget code was inspired from the hackathon
 */

public class StockWidgetProvider extends AppWidgetProvider{

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int appWidgetId : appWidgetIds){
      Intent intent = new Intent(context, MainActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

      RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_initial_layout);
      remoteViews.setOnClickPendingIntent(R.id.container,pendingIntent);

      Intent widgetIntent = new Intent(context, StockWidgetService.class);
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
      intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); //TODO
      remoteViews.setRemoteAdapter(R.id.listview_widget, widgetIntent);

      appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }
}
