package presentation.clima.pronostico

import repository.modelos.ListForecast

sealed class PronosticoEstado {
    data class Exitoso(
        val climas: List<ListForecast?>,
    ) : PronosticoEstado()
    data class Error(
        val mensaje :String = "",
    ) : PronosticoEstado()
    data object Vacio: PronosticoEstado()
    data object Cargando: PronosticoEstado()

}