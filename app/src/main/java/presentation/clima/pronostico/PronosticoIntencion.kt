package presentation.clima.pronostico

sealed class PronosticoIntencion {
    object actualizarClima: PronosticoIntencion()
    object CambiarCiudad : PronosticoIntencion()
    data class CompartirClima(val mensaje: String) : PronosticoIntencion()
}