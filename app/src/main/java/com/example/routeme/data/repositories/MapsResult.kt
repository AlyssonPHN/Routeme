package com.example.routeme.data.repositories

import com.google.android.gms.maps.model.LatLng

sealed class MapsResult {
    //class Succes(val paths: ArrayList<LatLng>): MapsResult()
    class Success<out T>(val data: T): MapsResult()
    class ApiError(val exception: Exception): MapsResult()
}
