package presentation.clima

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import presentation.clima.pronostico.PronosticoView
import presentation.clima.pronostico.PronosticoViewModel
import presentation.clima.pronostico.PronosticoViewModelFactory
import repository.RepositorioApi
import router.Enrutador

@Composable
fun ClimaPage(
    navHostController: NavHostController,
    lat : Float,
    lon : Float,
    nombre: String
){
    val viewModel : ClimaViewModel = viewModel(
        factory = ClimaViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            lat = lat,
            lon = lon,
            nombre = nombre
        )
    )
    val pronosticoViewModel : PronosticoViewModel = viewModel(
        factory = PronosticoViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            nombre = nombre
        )
    )

    Column {
        ClimaView(
            estado = viewModel.uiState,
             onAccion = { intencion ->
                viewModel.ejecutar(intencion)
            }
        )
        PronosticoView(
            state = pronosticoViewModel.uiState,
            onAction = { intencion ->
                pronosticoViewModel.ejecutar(intencion)
            }
        )
    }

}