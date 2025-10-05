package ru.gishackathon.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews

class WidgetConfigActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        // Собираем вьюшки (можно настраивать тексты, цвета и т.п.)
        val views = buildViews()

        // Отдаём системе
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Возвращаем успешный результат
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, result)
        finish()
    }

    private fun buildViews(): RemoteViews {
        val v = RemoteViews(packageName, R.layout.widget_transport)
        // Пример: можно подменить тексты остановок из настроек/интента
        v.setTextViewText(R.id.widget_stop1, "🚌 Метро Октябрьская")
        v.setTextViewText(R.id.widget_stop2, "🚌 пл. Гагарина · 1D")
        return v
    }
}
