package rayan.rayanapp.Services.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import rayan.rayanapp.Util.HomeScreenWidgetFactory;

public class HomeScreenWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return(new HomeScreenWidgetFactory(this.getApplicationContext(),
                                 intent));
  }
}