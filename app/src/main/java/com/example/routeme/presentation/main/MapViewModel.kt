package com.example.routeme.presentation.main

import android.graphics.Color
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.routeme.data.repositories.MapsResult
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
import java.util.*

class MapViewModel @ViewModelInject constructor(private val routeDataSource: RoutesRepository) :
    ViewModel() {

    val polylinesLiveData = MutableLiveData<PolylineOptions>()
    val markerOptionsLiveData = MutableLiveData<MarkerOptions>()

    val errorPolilyneLiveData = MutableLiveData<Exception>()
    val errorMarkerOptionsLiveData = MutableLiveData<Exception>()

    // build é necessário para dar zoom nos pontos adicionado
    var builder = LatLngBounds.Builder()

    fun getPolylines(geoApiContext: GeoApiContext, origin: LatLng, destination: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
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
                            polylinesLiveData.postValue(result.data as PolylineOptions)
                        }
                        // Erro ao retornar rota
                        is MapsResult.ApiError -> {
                            errorPolilyneLiveData.postValue(result.exception)
                        }
                    }
                }
            }
        }
    }

    fun createMarkerOption(latLng: LatLng, myPostionBoolean: Boolean) {
        var markerOptions: MarkerOptions?
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
                    errorMarkerOptionsLiveData.value = result.exception
                }
            }
        }

    }

}