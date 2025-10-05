package ru.gishackathon.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/* ----------------------------- ТЕМА (тёмная) -------------------------------- */

private val Bg = Color(0xFF101114)
private val Card = Color(0xFF1B1D22)
private val CardAlt = Color(0xFF22252B)
private val TextPrimary = Color(0xFFECEFF4)
private val TextSecondary = Color(0xFFB3B8C3)
private val Blue = Color(0xFF6BA6FF)
private val Accent = Color(0xFF6BA6FF)

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
    val colors = darkColorScheme(
        primary = Accent,
        background = Bg,
        surface = Bg,
        onPrimary = Color.Black,
        onBackground = TextPrimary,
        onSurface = TextPrimary
    )
    MaterialTheme(colorScheme = colors, typography = Typography(), content = content)
}

/* ----------------------------- ДАННЫЕ --------------------------------------- */

data class Stop(val title: String, val subtitle: String? = null)
data class Action(val title: String, val icon: ImageVector, val tint: Color)

/* ----------- пресеты стартового размера для запроса пина -------------------- */

enum class WidgetSize(val minWdp: Int, val minHdp: Int, val maxWdp: Int, val maxHdp: Int) {
    Small (110, 110, 130, 130),
    Medium(220, 160, 260, 220),
    Large (300, 260, 360, 320)
}

/* ------------------------- ОБЩИЕ КОМПОЗАБЛЫ --------------------------------- */

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun PillCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(CardAlt)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) {}
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun SquareTile(
    title: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) {}
            .padding(vertical = 14.dp)
            .heightIn(min = 84.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            title,
            color = TextPrimary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ------------------------------ SIZE PICKER --------------------------------- */

@Composable
private fun SizePicker(
    selected: WidgetSize,
    onSelect: (WidgetSize) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Card)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetSize.values().forEach { size ->
            FilterChip(
                selected = size == selected,
                onClick = { onSelect(size) },
                label = { Text(if (size == WidgetSize.Small) "Малый" else if (size == WidgetSize.Medium) "Средний" else "Большой") }
            )
        }
    }
}

/* ------------------------------- ЭКРАН -------------------------------------- */

@Composable
fun TransportWidgetScreen(
    stops: List<Stop>,
    actionsTaxi: List<Action>,
    actionsRoute: List<Action>,
    selectedSize: WidgetSize,
    onChangeSize: (WidgetSize) -> Unit,
    onRequestPin: () -> Unit,
) {
    Surface(Modifier.fillMaxSize(), color = Bg) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Bg),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            item { SectionHeader("Ваши остановки") }
            items(stops) { stop ->
                PillCard(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Icon(Icons.Outlined.DirectionsBus, null, tint = Blue, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(stop.title, color = TextPrimary, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!stop.subtitle.isNullOrBlank()) {
                            Text(stop.subtitle!!, color = TextSecondary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            // === БЛОК ВЫБОРА РАЗМЕРА И КНОПКА (ЧУТЬ ВЫШЕ "Заказать такси") ===
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CardAlt)
                        .padding(12.dp)
                ) {
                    Text("Размер виджета", color = TextPrimary, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    SizePicker(selected = selectedSize, onSelect = onChangeSize)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = onRequestPin, modifier = Modifier.fillMaxWidth()) {
                        Text("Добавить виджет на рабочий стол")
                    }
                }
            }

            item { SectionHeader("Заказать такси", modifier = Modifier.padding(top = 6.dp)) }
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    actionsTaxi.take(2).forEach { act ->
                        SquareTile(act.title, act.icon, act.tint, Modifier.weight(1f))
                    }
                }
            }

            item { SectionHeader("Построить маршрут", modifier = Modifier.padding(top = 6.dp)) }
            items(actionsRoute.chunked(2)) { row ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { act ->
                        SquareTile(act.title, act.icon, act.tint, Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/* ------------------------------ АКТИВИТИ ----------------------------------- */

class MainActivity : ComponentActivity() {

    companion object {
        const val ACTION_OPEN_2GIS_SEARCH = "ru.gishackathon.widget.OPEN_2GIS"
        const val EXTRA_QUERY = "query"
        private const val TWO_GIS_PACKAGE = "ru.dublgis.dgismobile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Убираем "белую" системную полоску, отступы — через systemBarsPadding()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WidgetTheme {
                var size by remember { mutableStateOf(WidgetSize.Medium) }

                Box(
                    Modifier.fillMaxSize().background(Bg).systemBarsPadding()
                ) {
                    TransportWidgetScreen(
                        stops = listOf(
                            Stop("Метро Октябрьская"),
                            Stop("пл. Гагарина · 1D")
                        ),
                        actionsTaxi = listOf(
                            Action("Дом", Icons.Outlined.Home, Color(0xFFFFC107)),
                            Action("Работа", Icons.Outlined.WorkOutline, Color(0xFF8BC34A))
                        ),
                        actionsRoute = listOf(
                            Action("Медицина", Icons.Outlined.MedicalServices, Color(0xFFE53935)),
                            Action("Автосервис", Icons.Outlined.Build, Color(0xFF64B5F6)),
                            Action("Продукты", Icons.Outlined.LocalGroceryStore, Color(0xFF66BB6A)),
                            Action("Заправка", Icons.Outlined.LocalGasStation, Color(0xFFFFB74D)),
                            Action("Досуг", Icons.Outlined.LocalActivity, Color(0xFFFFA726)),
                            Action("Аптека", Icons.Outlined.LocalPharmacy, Color(0xFF66BB6A))
                        ),
                        selectedSize = size,
                        onChangeSize = { size = it },
                        onRequestPin = { requestPinWidget(size) }
                    )
                }
            }
        }

        // обработать клик с виджета, если активити запущена из него
        handleWidgetIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {   // <-- НЕ nullable
        super.onNewIntent(intent)
        handleWidgetIntent(intent)               // сюда можно передать ненулевой
    }

    private fun handleWidgetIntent(intent: Intent?) {
        if (intent?.action == ACTION_OPEN_2GIS_SEARCH) {
            val q = intent.getStringExtra(EXTRA_QUERY).orEmpty()
            openIn2GisOrPrompt(q)
        }
    }

    private fun openIn2GisOrPrompt(query: String) {
        val isInstalled = try {
            packageManager.getPackageInfo(TWO_GIS_PACKAGE, 0); true
        } catch (_: Exception) { false }

        if (isInstalled) {
            val uri = android.net.Uri.parse(
                "geo:0,0?q=" + java.net.URLEncoder.encode(query, "UTF-8")
            )
            val i = Intent(Intent.ACTION_VIEW, uri).setPackage(TWO_GIS_PACKAGE)
            startActivity(i)
        } else {
            Toast.makeText(this, "Установите 2ГИС", Toast.LENGTH_SHORT).show()
            // Можно дополнительно открыть маркет:
            // startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$TWO_GIS_PACKAGE")))
        }
    }

    // --- запрос пина с передачей стартового размера ---
    private fun requestPinWidget(size: WidgetSize) {
        val mgr = getSystemService(AppWidgetManager::class.java)
        val provider = ComponentName(this, TransportWidgetProvider::class.java)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(this, "Добавьте виджет вручную (Android < 8.0)", Toast.LENGTH_SHORT).show()
            return
        }
        if (!mgr.isRequestPinAppWidgetSupported) {
            Toast.makeText(this, "Лаунчер не поддерживает пин виджета", Toast.LENGTH_SHORT).show()
            return
        }

        val extras = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,  size.minWdp.dpToPx())
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, size.minHdp.dpToPx())
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,  size.maxWdp.dpToPx())
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, size.maxHdp.dpToPx())
        }
        mgr.requestPinAppWidget(provider, extras, null)
    }

    private fun Int.dpToPx(): Int {
        val d = resources.displayMetrics.density
        return (this * d).toInt()
    }
}

/* -------------------------------- PREVIEW ---------------------------------- */

@Preview(showBackground = true, backgroundColor = 0xFF101114)
@Composable
fun PreviewTransportWidget() {
    WidgetTheme {
        var size by remember { mutableStateOf(WidgetSize.Medium) }
        TransportWidgetScreen(
            stops = listOf(Stop("Метро Октябрьская"), Stop("пл. Гагарина · 1D")),
            actionsTaxi = listOf(
                Action("Дом", Icons.Outlined.Home, Color(0xFFFFC107)),
                Action("Работа", Icons.Outlined.WorkOutline, Color(0xFF8BC34A))
            ),
            actionsRoute = listOf(
                Action("Медицина", Icons.Outlined.MedicalServices, Color(0xFFE53935)),
                Action("Автосервис", Icons.Outlined.Build, Color(0xFF64B5F6)),
                Action("Продукты", Icons.Outlined.LocalGroceryStore, Color(0xFF66BB6A)),
                Action("Заправка", Icons.Outlined.LocalGasStation, Color(0xFFFFB74D)),
                Action("Досуг", Icons.Outlined.LocalActivity, Color(0xFFFFA726)),
                Action("Аптека", Icons.Outlined.LocalPharmacy, Color(0xFF66BB6A))
            ),
            selectedSize = size,
            onChangeSize = { size = it },
            onRequestPin = {}
        )
    }
}
