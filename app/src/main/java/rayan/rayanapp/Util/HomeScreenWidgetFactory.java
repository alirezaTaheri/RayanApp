package rayan.rayanapp.Util;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Persistance.database.DeviceDatabase;
import rayan.rayanapp.R;


public class HomeScreenWidgetFactory implements RemoteViewsService.RemoteViewsFactory {


  private List<Device> devices = new ArrayList<>();
  private Context ctxt;
 private DeviceDatabase deviceDatabase;
  private int appWidgetId;

  public HomeScreenWidgetFactory(Context ctxt, Intent intent) {
      this.ctxt=ctxt;
      appWidgetId=intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                      AppWidgetManager.INVALID_APPWIDGET_ID);
  }
  
  @Override
  public void onCreate() {
    deviceDatabase=new DeviceDatabase(ctxt);
    devices=getFavorateDevices();
  }
  
  @Override
  public void onDestroy() {
    // no-op
  }

  @Override
  public int getCount() {
    return(devices.size());
  }



  @Override
  public RemoteViews getLoadingView() {
    return(null);
  }
  
  @Override
  public int getViewTypeCount() {
    return(1);
  }

  @Override
  public long getItemId(int position) {
    return(position);
  }

  @Override
  public boolean hasStableIds() {
    return(true);
  }

  @Override
  public void onDataSetChanged() {
    // no-op
  }


  @Override
  public RemoteViews getViewAt(int position) {
    RemoteViews row=new RemoteViews(ctxt.getPackageName(),
            R.layout.home_screen_widget_item);

    row.setTextViewText(R.id.name, devices.get(position).getName1());

    Intent i=new Intent();
    Bundle extras=new Bundle();

    extras.putString(HomeScreenWidgetProvider.EXTRA_WORD, devices.get(position).getUsername());
    i.putExtras(extras);
    row.setOnClickFillInIntent(R.id.name, i);

    return(row);
  }

  public List<Device> getFavorateDevices() {
    return deviceDatabase.getFavorates();
  }

}