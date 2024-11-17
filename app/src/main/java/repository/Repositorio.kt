package repository

import repository.modelos.Ciudad
import repository.modelos.Clima
import repository.modelos.ListForecast


interface Repositorio {
    suspend fun buscarCiudad(ciudad: String): List<Ciudad>
    suspend fun obtenerClimaActual(lat: Float, lon: Float): Clima
    suspend fun obtenerPronostico(nombre: String) :List<ListForecast>

}