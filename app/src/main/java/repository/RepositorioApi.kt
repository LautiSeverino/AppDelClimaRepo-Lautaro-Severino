package repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import repository.modelos.Ciudad
import repository.modelos.Clima
import repository.modelos.ForecastDTO
import repository.modelos.ListForecast

class RepositorioApi : Repositorio {

    private val apiKey = "1159e10da7a707c3884f2691008a1eb2"

    private val client = HttpClient(){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {

        val response = client.get("https://api.openweathermap.org/geo/1.0/direct"){
            parameter("q", ciudad)
            parameter("limit", 100)
            parameter("appid", apiKey)
        }

        if (response.status == HttpStatusCode.OK){
            val ciudades = response.body<List<Ciudad>>()
            return ciudades
        } else{
            throw Exception()
        }
    }

    override suspend fun obtenerClimaActual(lat: Float, lon: Float): Clima {

        val response = client.get("https://api.openweathermap.org/data/2.5/weather"){
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("appid", apiKey)
        }

        if (response.status == HttpStatusCode.OK){
            val clima = response.body<Clima>()
            return clima
        } else{
            throw Exception()
        }
    }

    override suspend fun obtenerPronostico(nombre: String): List<ListForecast> {

        val response = client.get("https://api.openweathermap.org/data/2.5/forecast"){
            parameter("q", nombre)
            parameter("units", "metric")
            parameter("appid", apiKey)
        }

        if (response.status == HttpStatusCode.OK){
            val forecast = response.body<ForecastDTO>()
            return forecast.list
        } else{
            throw Exception()
        }
    }


}