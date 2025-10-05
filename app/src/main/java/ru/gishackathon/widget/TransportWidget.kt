package ru.gishackathon.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class TransportWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            val views = buildViews(context)
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    private fun buildViews(context: Context): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_transport)

        // 1) Открыть приложение
        rv.setOnClickPendingIntent(
            R.id.widget_open_app,
            PendingIntent.getActivity(
                context, 1000,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        // 2) Хэлпер на поиск в 2ГИС
        fun searchPI(query: String, req: Int): PendingIntent {
            val i = Intent(context, MainActivity::class.java).apply {
                action = MainActivity.ACTION_OPEN_2GIS_SEARCH
                putExtra(MainActivity.EXTRA_QUERY, query)
            }
            return PendingIntent.getActivity(
                context, req, i,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        // 3) Вешаем клики на плитки (ID совпадают с layout!)
        rv.setOnClickPendingIntent(R.id.tile_home,     searchPI("дом",         1101))
        rv.setOnClickPendingIntent(R.id.tile_work,     searchPI("работа",      1102))
        rv.setOnClickPendingIntent(R.id.tile_medical,  searchPI("медицина",    1201))
        rv.setOnClickPendingIntent(R.id.tile_service,  searchPI("автосервис",  1202))
        rv.setOnClickPendingIntent(R.id.tile_grocery,  searchPI("продукты",    1203))
        rv.setOnClickPendingIntent(R.id.tile_gas,      searchPI("заправка",    1204))
        rv.setOnClickPendingIntent(R.id.tile_leisure,  searchPI("досуг",       1205))
        rv.setOnClickPendingIntent(R.id.tile_pharmacy, searchPI("аптека",      1206))

        return rv
    }
}
