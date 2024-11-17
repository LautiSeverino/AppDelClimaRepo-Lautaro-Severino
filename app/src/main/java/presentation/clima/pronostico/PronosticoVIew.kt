package presentation.clima.pronostico

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import repository.modelos.ListForecast



@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state: PronosticoEstado,
    onAction: (PronosticoIntencion) -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(PronosticoIntencion.actualizarClima)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state) {
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso -> {
                // Detalle del clima de hoy
                DetalleDeHoyView(climaHoy = state.climas.firstOrNull())

                Spacer(modifier = Modifier.height(16.dp))

                // Gráfico de pronósticos
                GraficoDePronosticoView(climas = state.climas)
            }
            PronosticoEstado.Vacio -> LoadingView()
            PronosticoEstado.Cargando -> EmptyView()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción con lógica integrada
        BotonesDeAccion(
            onCambiarCiudad = {
                onAction(PronosticoIntencion.CambiarCiudad) // Lógica para cambiar ciudad
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



@Composable
fun EmptyView() {
    Text(text = "No hay nada que mostrar")
}

@Composable
fun LoadingView() {
    Text(text = "Cargando")
}

@Composable
fun ErrorView(mensaje: String) {
    Text(text = mensaje)
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
                    val maxTempNormalized =
                        (forecast.temp_max - minTempGlobal) / rangoTemperatura
                    val minTempNormalized =
                        (forecast.temp_min - minTempGlobal) / rangoTemperatura

                    // Altura de las barras
                    val maxBarHeight = canvasHeight * maxTempNormalized
                    val minBarHeight = canvasHeight * minTempNormalized

                    // Posiciones de las barras
                    val barX = index * barSpacing + barSpacing / 4
                    val barWidth = barSpacing / 2

                    // Dibujar barras de temperatura máxima (color amarillo)
                    drawRect(
                        color = Color(0xFFFFA000), // Amarillo
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = barX,
                            y = canvasHeight.toFloat() - maxBarHeight.toFloat()
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = barWidth,
                            height = maxBarHeight.toFloat()
                        )
                    )

                    // Dibujar barras de temperatura mínima (color azul)
                    drawRect(
                        color = Color(0xFF0288D1), // Azul
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = barX,
                            y = canvasHeight.toFloat() - minBarHeight.toFloat()
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
                            barX + barWidth / 2,
                            canvasHeight.toFloat() - maxBarHeight.toFloat() - 10,
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.BLACK
                                textSize = 28f
                            }
                        )
                        drawText(
                            "${forecast.temp_min}°C",
                            barX + barWidth / 2,
                            canvasHeight.toFloat() - minBarHeight.toFloat() - 10,
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.BLACK
                                textSize = 28f
                            }
                        )
                    }
                }
            }

            // Etiquetas de las fechas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                fechas.forEach { fecha ->
                    Text(
                        text = fecha,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
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


