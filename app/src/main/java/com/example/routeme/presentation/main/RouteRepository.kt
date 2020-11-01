package com.example.routeme.presentation.main

import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RouteRepository @Inject constructor() {


    // Pegar os caminhos da rota
    suspend fun getPathRoute(
        geoApiContext: GeoApiContext,
        origin: LatLng,
        destination: LatLng
    ): ArrayList<LatLng> {
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
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
            paths
        }
    }
}