package com.example.routeme.data.repositories

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.GeoApiContext

interface RoutesRepository {

    suspend fun getPathRoute(
        geoApiContext: GeoApiContext,
        origin: LatLng,
        destination: LatLng,
        mapResultCallback: (result: MapsResult) -> Unit
    )

    fun createMarkerOption(
        latLng: LatLng,
        myPostionBoolean: Boolean,
        mapResultCallback: (result: MapsResult) -> Unit
    )

}