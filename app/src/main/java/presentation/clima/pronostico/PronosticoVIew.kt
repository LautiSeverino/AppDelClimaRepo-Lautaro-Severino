package presentation.clima.pronostico

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.appdelclima.ui.theme.AppDelClimaTheme
import presentation.clima.ClimaEstado
import presentation.clima.ClimaIntencion
import presentation.clima.ClimaView
import repository.modelos.ListForecast
import repository.modelos.MainForecast
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state: PronosticoEstado,
    onAction: (PronosticoIntencion) -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(PronosticoIntencion.actualizarClima)
    }
    AppDelClimaTheme(darkTheme = true)  {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso -> {

                DetalleDeHoyView(climaHoy = state.climas.firstOrNull())

                Spacer(modifier = Modifier.height(16.dp))


                GraficoDePronosticoView(climas = state.climas)
            }
            PronosticoEstado.Vacio -> LoadingView()
            PronosticoEstado.Cargando -> EmptyView()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción con lógica integrada
        BotonesDeAccion(
            onCambiarCiudad = {
                onAction(PronosticoIntencion.CambiarCiudad)
            },
            onCompartir = {
                val climaHoy = (state as? PronosticoEstado.Exitoso)?.climas?.firstOrNull()
                if (climaHoy != null) {
                    val mensaje = """
                        Clima actual:
                        Temperatura: ${climaHoy.main.temp}°C
                        Sensación térmica: ${climaHoy.main.feels_like}°C
                        Máxima: ${climaHoy.main.temp_max}°C
                        Mínima: ${climaHoy.main.temp_min}°C
                        Humedad: ${climaHoy.main.humidity}%
                    """.trimIndent()
                    onAction(PronosticoIntencion.CompartirClima(mensaje))
                }
            }
        )
    }
    }
}



@Composable
fun EmptyView() {
    AppDelClimaTheme(darkTheme = true) {
        Text(text = "No hay nada que mostrar")
    }
}

@Composable
fun LoadingView() {
    AppDelClimaTheme(darkTheme = true) {
        Text(text = "Cargando")
    }
}

@Composable
fun ErrorView(mensaje: String) {
    AppDelClimaTheme(darkTheme = true)  {
        Text(text = mensaje)
    }
}

@Preview(showBackground = true)
@Composable
fun VistaErrorPreview() {
    AppDelClimaTheme(darkTheme = true) {
        PronosticoView(state = PronosticoEstado.Error("No se pudo obtener el clima"), onAction = {})
    }
}


@Composable
fun DetalleDeHoyView(climaHoy: ListForecast?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Detalle del clima de hoy",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (climaHoy != null) {
                Text(text = "Temperatura: ${climaHoy.main.temp}°C")
                Text(text = "Sensación térmica: ${climaHoy.main.feels_like}°C")
                Text(text = "Máxima: ${climaHoy.main.temp_max}°C")
                Text(text = "Mínima: ${climaHoy.main.temp_min}°C")
                Text(text = "Humedad: ${climaHoy.main.humidity}%")
            } else {
                Text(text = "No hay datos disponibles")
            }
        }
    }
}


@Composable
fun GraficoDePronosticoView(climas: List<ListForecast?>) {
    val datos = climas.filterNotNull().map { it.main }
    val fechas = climas.filterNotNull().map { it.dt_txt.substring(0, 10) }

    if (datos.isEmpty() || fechas.isEmpty()) {
        Text(text = "No hay datos para mostrar el gráfico")
        return
    }

    // Rango de temperaturas (mínimo y máximo global)
    val maxTempGlobal = datos.maxOfOrNull { it.temp_max.toFloat() } ?: 0f
    val minTempGlobal = datos.minOfOrNull { it.temp_min.toFloat() } ?: 0f



    // Diferencia para normalizar los valores
    val rangoTemperatura = maxTempGlobal - minTempGlobal
    AppDelClimaTheme(darkTheme = true)  {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gráfico de temperaturas (máximas y mínimas)",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Canvas para el gráfico
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Dimensiones del gráfico
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barSpacing = canvasWidth / datos.size

                datos.forEachIndexed { index, forecast ->
                    // Normalización de las temperaturas
                    val maxTempNormalized =
                        ((forecast.temp_max - minTempGlobal) / (rangoTemperatura.takeIf { it > 0 } ?: 1f))
                    val minTempNormalized =
                        ((forecast.temp_min - minTempGlobal) / (rangoTemperatura.takeIf { it > 0 } ?: 1f))


                    // Altura de las barras
                    val maxBarHeight = canvasHeight * maxTempNormalized
                    val minBarHeight = canvasHeight * minTempNormalized

                    // Posiciones ajustadas de las barras
                    val barX = index * barSpacing + barSpacing / 4
                    val maxBarX = barX
                    val barWidth = barSpacing / 2
                    val minBarX = barX + barWidth / 3 // Separar la barra mínima


                    // Dibujar barra de temperatura máxima (color amarillo)
                    drawRect(
                        color = Color(0xFFFFA000), // Amarillo
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = maxBarX,
                            y = (canvasHeight - maxBarHeight).toFloat()
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = barWidth,
                            height = maxBarHeight.toFloat()
                        )
                    )

                    // Dibujar barra de temperatura mínima (color azul)
                    drawRect(
                        color = Color(0xFF0288D1), // Azul
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = minBarX,
                            y = (canvasHeight - minBarHeight).toFloat()
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = barWidth,
                            height = minBarHeight.toFloat()
                        )
                    )

                    // Dibujar etiquetas de temperatura
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            "${forecast.temp_max}°C",
                            maxBarX + barWidth / 2,
                            (canvasHeight - maxBarHeight - 10).toFloat(),
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.WHITE
                                textSize = 28f
                            }
                        )
                        drawText(
                            "${forecast.temp_min}°C",
                            minBarX + barWidth / 2,
                            (canvasHeight - minBarHeight - 10).toFloat(),
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.WHITE
                                textSize = 28f
                            }
                        )
                    }
                }
            }

        }
    }
    }
}


@Composable
fun BotonesDeAccion(
    onCambiarCiudad: () -> Unit,
    onCompartir: () -> Unit
) {
    AppDelClimaTheme(darkTheme = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onCambiarCiudad) {
                Text(text = "Cambiar Ciudad")
            }
            Button(onClick = onCompartir) {
                Text(text = "Compartir")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VistaErrorPronosticoPreview() {
    AppDelClimaTheme(darkTheme = true)  {
        PronosticoView(
            state = PronosticoEstado.Error("No se pudo obtener el pronóstico"),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaCargandoPronosticoPreview() {
    AppDelClimaTheme(darkTheme = true)  {
        PronosticoView(
            state = PronosticoEstado.Cargando,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaVacioPronosticoPreview() {
    AppDelClimaTheme(darkTheme = true)  {
        PronosticoView(
            state = PronosticoEstado.Vacio,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VistaExitosoPronosticoPreview() {
    AppDelClimaTheme(darkTheme = true) {
        PronosticoView(
            state = PronosticoEstado.Exitoso(
                climas = listOf(
                    ListForecast(
                        main = MainForecast(
                            temp = 25.0,
                            feels_like = 27.0,
                            temp_max = 30.0,
                            temp_min = 25.0,
                            humidity = 60,
                            grnd_level = 1010,
                            pressure = 1013,
                            sea_level = 1015,
                            temp_kf = 0.0
                        ),
                        dt_txt = "2024-11-17 12:00:00",
                        dt = "2024-11-17 12:00:00".toTimestamp()
                    ),
                    ListForecast(
                        main = MainForecast(
                            temp = 23.0,
                            feels_like = 25.0,
                            temp_max = 28.0,
                            temp_min = 20.0,
                            humidity = 65,
                            grnd_level = 1012,
                            pressure = 1015,
                            sea_level = 1017,
                            temp_kf = 0.0
                        ),
                        dt_txt = "2024-11-17 18:00:00",
                        dt = "2024-11-17 18:00:00".toTimestamp()
                    )
                ),
            ),
            onAction = {}
        )
    }
}


fun String.toTimestamp(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.parse(this)?.time ?: 0L
}