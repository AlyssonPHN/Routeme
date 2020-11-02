package com.example.routeme.data.repositories

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RouteDataSource @Inject constructor() : RoutesRepository {
    override suspend fun getPathRoute(
        geoApiContext: GeoApiContext,
        origin: LatLng,
        destination: LatLng,
        mapResultCallback: (result: MapsResult) -> Unit
    ) {
        return withContext(Dispatchers.Default) {
            val paths: ArrayList<LatLng> = arrayListOf()
            val apiRequest2: DirectionsApiRequest = DirectionsApi.newRequest(geoApiContext)
                .origin("${origin.latitude},${origin.longitude}")
                .destination("${destination.latitude},${destination.longitude}")
                .mode(TravelMode.DRIVING)


            try {
                val routes: Array<DirectionsRoute> = apiRequest2.await()

                // Decode polyline das coordenadas da rota
                for (route in routes) {
                    if (route.legs != null) {
                        for (i in route.legs.indices) {
                            val leg = route.legs[i]
                            if (leg.steps != null) {
                                for (j in leg.steps.indices) {
                                    val step = leg.steps[j]

                                    val points = step.polyline
                                    if (points != null) {
                                        // Decode polyline das coordenadas da rota
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            paths.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //mapResultCallback(MapsResult.Success(paths))
                if (paths.size > 0) {
                    val opts: PolylineOptions =
                        PolylineOptions().addAll(paths).color(Color.BLUE).width(5f)
                    mapResultCallback(MapsResult.Success(opts))
                } else {
                    mapResultCallback(MapsResult.ApiError(Exception()))
                }
            } catch (e: Exception) {
                mapResultCallback(MapsResult.ApiError(e))
            }

        }
    }

    override fun createMarkerOption(latLng: LatLng, myPostionBoolean: Boolean, mapResultCallback: (result: MapsResult) -> Unit) {
        // Cria o marcador no mapa
        try {
            val markerOptions: MarkerOptions = if (myPostionBoolean)
                MarkerOptions().position(latLng).visible(false)
            else
                MarkerOptions().position(latLng)

            mapResultCallback(MapsResult.Success(markerOptions))
        } catch (e: Exception) {
            mapResultCallback(MapsResult.ApiError(e))
        }
    }

}