package presentation.ciudades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appdelclima.ui.theme.AppDelClimaTheme
import repository.modelos.Ciudad


@Composable
fun CiudadesView(
    modifier: Modifier = Modifier,
    state: CiudadesEstado,
    onAction: (CiudadesIntencion) -> Unit
) {
    var value by remember { mutableStateOf("") }

    val ciudadesPorDefecto = obtenerCiudadesPorDefecto()

    AppDelClimaTheme(darkTheme = true) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Buscador
        TextField(
            value = value,
            onValueChange = {
                value = it
                onAction(CiudadesIntencion.Buscar(value))
            },
            label = { Text(text = "Buscar por nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Estado del buscador
        when (state) {
            CiudadesEstado.Cargando -> Text(
                text = "Cargando...",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            is CiudadesEstado.Error -> Text(
                text = state.mensaje,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            is CiudadesEstado.Resultado -> ListaDeCiudades(state.ciudades) {
                onAction(CiudadesIntencion.Seleccionar(it))
            }
            CiudadesEstado.Vacio -> Text(
                text = "No hay resultados",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Título de las ciudades sugeridas
        Text(
            text = "Ciudades sugeridas",
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp) // Espacio entre el buscador y la lista
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium
        )

        // Lista de ciudades por defecto
        ListaDeCiudades(ciudades = ciudadesPorDefecto) { ciudadSeleccionada ->
            onAction(CiudadesIntencion.Seleccionar(ciudadSeleccionada))
        }
    }
    }
}

@Composable
fun ListaDeCiudades(ciudades: List<Ciudad>, onSelect: (Ciudad) -> Unit) {
    AppDelClimaTheme(darkTheme = true) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = ciudades) { ciudad ->
            Card(
                onClick = { onSelect(ciudad) },
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = ciudad.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${ciudad.country}${if (ciudad.state.isNotEmpty()) ", ${ciudad.state}" else ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }}
}



// Lista de ciudades por defecto
fun obtenerCiudadesPorDefecto(): List<Ciudad> {
    return listOf(
        Ciudad(name = "Buenos Aires", lat = -34.6037f, lon = -58.3816f, country = "Argentina", state = "Buenos Aires"),
        Ciudad(name = "Madrid", lat = 40.4168f, lon = -3.7038f, country = "España", state = "Comunidad de Madrid"),
        Ciudad(name = "New York", lat = 40.7128f, lon = -74.0060f, country = "Estados Unidos", state = "New York"),
        Ciudad(name = "Londres", lat = 51.5074f, lon = -0.1278f, country = "Reino Unido"),
        Ciudad(name = "Tokio", lat = 35.6895f, lon = 139.6917f, country = "Japón")
    )
}



@Preview(showBackground = true)
@Composable
fun CiudadesViewPreview_Cargando() {
    AppDelClimaTheme(darkTheme = true) {
        CiudadesView(
            state = CiudadesEstado.Cargando,
            onAction = {}
        )
    }

}

@Preview(showBackground = true)
@Composable
fun CiudadesViewPreview_Error() {
    AppDelClimaTheme(darkTheme = true) {
    CiudadesView(
        state = CiudadesEstado.Error("Error al cargar las ciudades"),
        onAction = {}
    )
    }
}

@Preview(showBackground = true)
@Composable
fun CiudadesViewPreview_Resultado() {
    AppDelClimaTheme(darkTheme = true) {
    CiudadesView(
        state = CiudadesEstado.Resultado(
            ciudades = listOf(
                Ciudad(name = "Buenos Aires", lat = -34.6037f, lon = -58.3816f, country = "Argentina", state = "Buenos Aires"),
                Ciudad(name = "Madrid", lat = 40.4168f, lon = -3.7038f, country = "España", state = "Comunidad de Madrid")
            )
        ),
        onAction = {}
    )
    }
}

@Preview(showBackground = true)
@Composable
fun CiudadesViewPreview_Vacio() {
    AppDelClimaTheme(darkTheme = true) {
    CiudadesView(
        state = CiudadesEstado.Vacio,
        onAction = {}
    )
    }
}

@Preview(showBackground = true)
@Composable
fun ListaDeCiudadesPreview() {
    AppDelClimaTheme(darkTheme = true) {
        ListaDeCiudades(
            ciudades = obtenerCiudadesPorDefecto(),
            onSelect = {}
        )
    }
}
