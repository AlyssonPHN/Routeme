package com.example.routeme.presentation.main

import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel @ViewModelInject constructor(private val routeRepository: RouteRepository): ViewModel() {

    val polylinesLiveData = MutableLiveData<PolylineOptions>()
    val markerOptionsLiveData = MutableLiveData<MarkerOptions>()
    // build é necessário para dar zoom nos pontos adicionado
    var builder = LatLngBounds.Builder()

    fun getPolylines(geoApiContext: GeoApiContext, origin: LatLng, destination: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            // Pega as rotas no repositório
            val paths = withContext(Dispatchers.Default) {
                routeRepository.getPathRoute(geoApiContext, origin, destination)
            }

            // Adiciona as polylines que é usada para traçar rota
            if (paths.size > 0) {
                val opts: PolylineOptions =
                    PolylineOptions().addAll(paths).color(Color.BLUE).width(5f)
                polylinesLiveData.value = opts
            }

        }
    }

    // Para criar o ponto no mapa
    fun createMarkerOption(latLng: LatLng, myPostionBoolean: Boolean){
        // Se for a primeira posição, não adiciona o icone
        val markerOptions: MarkerOptions = if (myPostionBoolean)
            MarkerOptions().position(latLng).visible(false)
        else
            MarkerOptions().position(latLng)
        // Adicionado no build para zoom entre pontos
        builder.include(markerOptions.position)

        markerOptionsLiveData.value = markerOptions
    }

}