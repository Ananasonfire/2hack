package ru.gishackathon.widget

import android.R
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

/**
 * Утилита для обновления всех экземпляров виджета снаружи (например из MainActivity).
 */
object WidgetUpdater {

    fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, TransportWidgetProvider
        ::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        if (appWidgetIds.isNotEmpty()) {
            // Уведомляем AppWidgetManager, что нужно обновить UI виджета
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)

            // А также вызываем прямое обновление
            TransportWidgetProvider().onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}