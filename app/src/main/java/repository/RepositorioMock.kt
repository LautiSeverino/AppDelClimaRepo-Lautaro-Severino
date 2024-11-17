package repository

import repository.modelos.Ciudad
import repository.modelos.Clima
import repository.modelos.Clouds
import repository.modelos.Coord
import repository.modelos.ListForecast
import repository.modelos.Main
import repository.modelos.MainForecast
import repository.modelos.Weather
import repository.modelos.Wind

class RepositorioMock : Repositorio {

    val cordoba = Ciudad(name = "Cordoba", lat = -23.0f, lon = -24.3f, country = "Argentina")
    val bsAs = Ciudad(name = "Buenos Aires", lat = -23.0f, lon = -24.3f, country = "Argentina")
    val laPlata = Ciudad(name = "La Plata", lat = -23.0f, lon = -24.3f, country = "Argentina")

    val ciudades = listOf(cordoba, bsAs, laPlata)

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        if (ciudad == "error") {
            throw Exception("Error en la búsqueda")
        }
        return ciudades.filter { it.name.contains(ciudad, ignoreCase = true) }
    }

    // Simulación de la obtención del clima actual
    override suspend fun obtenerClimaActual(lat: Float, lon: Float): Clima {
        return Clima(
            base = "stations",
            name = "Cordoba",
            coord = Coord(lon = lon.toDouble(), lat = lat.toDouble()),
            weather = listOf(
                Weather(id = 801, main = "Clouds", description = "Nubes dispersas", icon = "02d")
            ),
            main = Main(
                temp = 25.0,
                feels_like = 23.0,
                temp_min = 22.0,
                temp_max = 28.0,
                pressure = 1015L,
                humidity = 65L
            ),
            wind = Wind(speed = 5.0, deg = 90),
            clouds = Clouds(all = 40L)
        )
    }

    // Simulación del pronóstico de 5 días
    override suspend fun obtenerPronostico(nombre: String): List<ListForecast> {
        return listOf(
            ListForecast(
                dt = 1234567890L,
                main = MainForecast(
                    temp = 25.0,
                    temp_min = 20.0,
                    temp_max = 30.0,
                    pressure = 1013L,
                    humidity = 60L,
                    feels_like = 23.0,
                    grnd_level = 1010L,
                    sea_level = 1013L,
                    temp_kf = 0.0
                ),
                dt_txt = "2024-11-17 12:00:00"
            ),
            ListForecast(
                dt = 1234567891L,
                main = MainForecast(
                    temp = 22.0,
                    temp_min = 18.0,
                    temp_max = 26.0,
                    pressure = 1012L,
                    humidity = 65L,
                    feels_like = 20.0,
                    grnd_level = 1008L,
                    sea_level = 1011L,
                    temp_kf = 0.0
                ),
                dt_txt = "2024-11-18 12:00:00"
            ),
            ListForecast(
                dt = 1234567892L,
                main = MainForecast(
                    temp = 23.0,
                    temp_min = 19.0,
                    temp_max = 27.0,
                    pressure = 1014L,
                    humidity = 62L,
                    feels_like = 21.0,
                    grnd_level = 1011L,
                    sea_level = 1012L,
                    temp_kf = 0.0
                ),
                dt_txt = "2024-11-19 12:00:00"
            )
        )
    }
}



class RepositorioMockError : Repositorio {

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        throw Exception("Error en la búsqueda de la ciudad")
    }

    override suspend fun obtenerClimaActual(lat: Float, lon: Float): Clima {
        throw Exception("Error al obtener el clima actual")
    }

    override suspend fun obtenerPronostico(nombre: String): List<ListForecast> {
        throw Exception("Error al obtener el pronóstico")
    }
}
