package rayan.rayanapp.Util;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.R;
import rayan.rayanapp.Services.widget.HomeScreenWidgetService;


public class HomeScreenWidgetProvider extends AppWidgetProvider {
  public static String EXTRA_WORD= "rayan.rayanapp.WORD";

  @Override
  public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
                        int[] appWidgetIds) {
    for (int i=0; i<appWidgetIds.length; i++) {
      Intent svcIntent=new Intent(ctxt, HomeScreenWidgetService.class);
      
      svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
      svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
      
      RemoteViews widget=new RemoteViews(ctxt.getPackageName(),
                                          R.layout.home_screen_widget_gridview);
      
      widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_gridview,
                              svcIntent);

      Intent clickIntent=new Intent(ctxt, MainActivity.class);
      PendingIntent clickPI=PendingIntent
                              .getActivity(ctxt, 0,
                                            clickIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
      
      widget.setPendingIntentTemplate(R.id.widget_gridview, clickPI);
     /// Toast.makeText(ctxt, "Widget updated", Toast.LENGTH_SHORT).show();

      appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
    }
    
    super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
  }
}