package rayan.rayanapp.Receivers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.R;

public class LampWidgetReceiver extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "onReceive", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Toast.makeText(context, "onUpdate", Toast.LENGTH_SHORT).show();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_lamp);
            // Widgets allow click handlers to only launch pending intents
            views.setOnClickPendingIntent(R.id.text, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds[0], views);
//        }
    }
}
