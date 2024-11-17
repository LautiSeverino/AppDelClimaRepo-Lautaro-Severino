package presentation.clima.pronostico

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.Repositorio
import router.Router
import router.Ruta

class PronosticoViewModel(
    private val repositorio: Repositorio,
    private val router: Router,
    private var nombre: String // Ciudad actual
) : ViewModel() {

    var uiState by mutableStateOf<PronosticoEstado>(PronosticoEstado.Vacio)

    fun ejecutar(intencion: PronosticoIntencion) {
        when (intencion) {
            PronosticoIntencion.actualizarClima -> traerPronostico()
            PronosticoIntencion.CambiarCiudad -> cambiarCiudad()
            is PronosticoIntencion.CompartirClima -> compartirClima(intencion.mensaje)
        }
    }

    private fun traerPronostico() {
        uiState = PronosticoEstado.Cargando
        viewModelScope.launch {
            try {
                // Obtener los datos del repositorio
                val forecast = repositorio.obtenerPronostico(nombre)
                    .groupBy { it.dt_txt.substring(0, 10) } // Agrupa por día usando la fecha
                    .map { (_, pronosticosPorDia) ->
                        pronosticosPorDia.first()
                    }

                uiState = PronosticoEstado.Exitoso(forecast)
            } catch (exception: Exception) {
                uiState = PronosticoEstado.Error(exception.localizedMessage ?: "Error desconocido")
            }
        }
    }

    private fun cambiarCiudad() {
        // Navegar a la ruta de selección de ciudades
        router.navegar(Ruta.Ciudades)
    }

    private fun compartirClima(mensaje: String) {
        // Por ahora, este router no tiene lógica para compartir.
        println("Compartir clima: $mensaje")
    }
}

class PronosticoViewModelFactory(
    private val repositorio: Repositorio,
    private val router: Router,
    private val nombre: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PronosticoViewModel::class.java)) {
            return PronosticoViewModel(repositorio, router, nombre) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
