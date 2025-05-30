package org.legtux.m_316k.fortune;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

public class FortuneWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) { updateAppWidget(context, appWidgetManager, appWidgetIds[i]); }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) { Toast.makeText(context, R.string.del_widget, Toast.LENGTH_LONG).show(); }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Fortune.setContext(context);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fortune_widget);
        views.setTextViewText(R.id.fortune_widget_text, Fortune.instance().getCurrent()); //changed by ChatGPT:true).current());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

