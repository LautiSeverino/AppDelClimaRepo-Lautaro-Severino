package presentation.clima

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.appdelclima.ui.theme.AppDelClimaTheme

@Composable
fun ClimaView(
    modifier: Modifier = Modifier,
    estado: ClimaEstado,
    onAccion: (ClimaIntencion) -> Unit
) {
    // Ejecutar acción al reanudar la pantalla
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAccion(ClimaIntencion.actualizarClima)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar contenido según el estado actual
        when (estado) {
            is ClimaEstado.Error -> ErrorVista(mensaje = estado.mensaje)
            is ClimaEstado.Exitoso -> DetalleClimaVista(
                ciudad = estado.ciudad,
                temperatura = estado.temperatura,
                descripcion = estado.descripcion,
                sensacionTermica = estado.st
            )
            ClimaEstado.Vacio -> MensajeVista("No hay información disponible")
            ClimaEstado.Cargando -> MensajeVista("Cargando...")
            else -> {}
        }
    }
}

@Composable
fun MensajeVista(mensaje: String) {
    Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun ErrorVista(mensaje: String) {
    Text(text = "Error: $mensaje", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun DetalleClimaVista(ciudad: String, temperatura: Double, descripcion: String, sensacionTermica: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = ciudad, style = MaterialTheme.typography.titleMedium)
        Text(text = "${temperatura}°", style = MaterialTheme.typography.displayMedium)
        Text(text = descripcion.capitalize(), style = MaterialTheme.typography.bodyLarge)
        Text(text = "Sensación térmica: ${sensacionTermica}°", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun VistaVacioPreview() {
    AppDelClimaTheme {
        ClimaView(estado = ClimaEstado.Vacio, onAccion = {})
    }
}

@Preview(showBackground = true)
@Composable
fun VistaErrorPreview() {
    AppDelClimaTheme {
        ClimaView(estado = ClimaEstado.Error("No se pudo obtener el clima"), onAccion = {})
    }
}

@Preview(showBackground = true)
@Composable
fun VistaExitosoPreview() {
    AppDelClimaTheme {
        ClimaView(
            estado = ClimaEstado.Exitoso(
                ciudad = "San Miguel de Tucumán",
                temperatura = 34.02,
                descripcion = "Cielo despejado",
                st = 34.97
            ),
            onAccion = {}
        )
    }
}
