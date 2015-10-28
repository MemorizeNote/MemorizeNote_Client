package com.asb.memorizenote.widget.libraryseat;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.asb.memorizenote.R;
import com.asb.memorizenote.utils.MNLog;

/**
 * Created by azureskybox on 15. 10. 27.
 */
public class LibrarySeatsWidgetProvider extends AppWidgetProvider {

    private Context mContext;
    private AppWidgetManager mAppWidgetManager;
    private int[] mWidgetIds;

    @Override
    public void onReceive(Context context, Intent intent) {

        MNLog.d("onReceive, action=" + intent.getAction());

        if(intent.getAction().equals("com.asb.memorizenote.UPDATE_WIDGET")) {
            mContext = context;
            mAppWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            mWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            LibrarySeatParser parser = new LibrarySeatParser();
            parser.startParsing(new LibrarySeatParser.OnLibrarySeatsParsedListener() {
                @Override
                public void onParsed(String total, String current, String remain, String reserved, String next) {
                    RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_library_seats);

                    PendingIntent refreshIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.asb.memorizenote.UPDATE_WIDGET"), PendingIntent.FLAG_ONE_SHOT);
                    remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_refresh, refreshIntent);

                    remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_view, PendingIntent.getActivity(mContext, 0, new Intent(mContext, LibrarySeatsViewActivity.class), 0));

                    remoteViews.setTextViewText(R.id.widget_library_seats_total, total);
                    remoteViews.setTextViewText(R.id.widget_library_seats_remain, remain);
                    remoteViews.setTextViewText(R.id.widget_library_seats_reserved, reserved);
                    remoteViews.setTextViewText(R.id.widget_library_seats_next, next);

                    mAppWidgetManager.updateAppWidget(mWidgetIds[0], remoteViews);
                }
            });
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        int appWidgetId = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()))[0];

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_library_seats);

        PendingIntent refreshIntent = PendingIntent.getBroadcast(context, 0, new Intent("android.appwidget.action.APPWIDGET_UPDATE"), PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_refresh, refreshIntent);

        remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_view, PendingIntent.getActivity(mContext, 0, new Intent(context, LibrarySeatsViewActivity.class), 0));

        remoteViews.setTextViewText(R.id.widget_library_seats_total, "0");
        remoteViews.setTextViewText(R.id.widget_library_seats_next, "0");
        remoteViews.setTextViewText(R.id.widget_library_seats_remain, "0");
        remoteViews.setTextViewText(R.id.widget_library_seats_reserved, "0");

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        mAppWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        mWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

        LibrarySeatParser parser = new LibrarySeatParser();
        parser.startParsing(new LibrarySeatParser.OnLibrarySeatsParsedListener() {
            @Override
            public void onParsed(String total, String current, String remain, String reserved, String next) {
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_library_seats);

                PendingIntent refreshIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("com.asb.memorizenote.UPDATE_WIDGET"), PendingIntent.FLAG_ONE_SHOT);
                remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_refresh, refreshIntent);

                remoteViews.setOnClickPendingIntent(R.id.widget_library_seats_view, PendingIntent.getActivity(mContext, 0, new Intent(context, LibrarySeatsViewActivity.class), 0));

                remoteViews.setTextViewText(R.id.widget_library_seats_total, total);
                remoteViews.setTextViewText(R.id.widget_library_seats_remain, remain);
                remoteViews.setTextViewText(R.id.widget_library_seats_reserved, reserved);
                remoteViews.setTextViewText(R.id.widget_library_seats_next, next);

                mAppWidgetManager.updateAppWidget(mWidgetIds[0], remoteViews);
            }
        });
    }
}
