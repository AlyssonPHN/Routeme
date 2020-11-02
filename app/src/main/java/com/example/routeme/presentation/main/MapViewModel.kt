package com.example.routeme.presentation.main

import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routeme.data.repositories.MapsResult
import com.example.routeme.data.repositories.RouteDataSource
import com.example.routeme.data.repositories.RoutesRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.GeoApiContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

//class MapViewModel @ViewModelInject constructor(private val routeRepository: RouteRepository): ViewModel() {
class MapViewModel @ViewModelInject constructor(private val routeDataSource: RoutesRepository) :
    ViewModel() {

    val polylinesLiveData = MutableLiveData<PolylineOptions>()
    val markerOptionsLiveData = MutableLiveData<MarkerOptions>()

    // build é necessário para dar zoom nos pontos adicionado
    var builder = LatLngBounds.Builder()

    fun getPolylines(geoApiContext: GeoApiContext, origin: LatLng, destination: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            var paths: ArrayList<LatLng> = arrayListOf()
            // Coroutines para retornar as latitudes da rota
            withContext(Dispatchers.Default) {
                routeDataSource.getPathRoute(
                    geoApiContext,
                    origin,
                    destination
                ) { result: MapsResult ->
                    when (result) {
                        is MapsResult.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            // Rota retornada
                            paths = result.data as ArrayList<LatLng>
                        }
                        // Erro ao retornar rota
                        is MapsResult.ApiError -> result.exception
                    }
                }
            }
            if (paths.size > 0) {
                val opts: PolylineOptions =
                    PolylineOptions().addAll(paths).color(Color.BLUE).width(5f)
                polylinesLiveData.value = opts
            }
        }
    }

    fun createMarkerOption(latLng: LatLng, myPostionBoolean: Boolean) {
//        val markerOptions = routeDataSource.createMarkerOption(latLng, myPostionBoolean)
        var markerOptions: MarkerOptions? = null
        routeDataSource.createMarkerOption(latLng, myPostionBoolean) {result: MapsResult ->
            when(result) {
                is MapsResult.Success<*> -> {
                    // Ao receber o MarkerOption
                    markerOptions = result.data as MarkerOptions
                    // Adiciona no build para o zoom geral
                    builder.include(markerOptions?.position)
                    // Adiciona o ponto no mapa
                    markerOptionsLiveData.value = markerOptions
                }
                is MapsResult.ApiError -> {

                }
            }
        }

    }

}