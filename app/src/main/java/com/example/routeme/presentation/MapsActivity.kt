package com.example.routeme.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.routeme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsRoute
import com.google.maps.model.TravelMode
import kotlinx.coroutines.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //pedirPermissionLocal()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val zaragoza = LatLng(41.648823, -0.889085)

        val barcelona = LatLng(41.385064, 2.173403)
        mMap.addMarker(MarkerOptions().position(barcelona).title("Marker in Barcelona"))

        val madrid = LatLng(40.416775, -3.70379)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marker in Madrid"))

        val context = GeoApiContext().setApiKey(getString(R.string.google_maps_key))
        val paths: ArrayList<LatLng> = arrayListOf()
        CoroutineScope(Dispatchers.Main).launch {
            //withContext(Dispatchers.Default) {


            suspend fun rodar() {
                return withContext(Dispatchers.Default) {
                    val apiRequest2: DirectionsApiRequest = DirectionsApi.newRequest(context)
                        .origin("41.385064,2.173403")
                        .destination("40.416775,-3.70379")
                        .mode(TravelMode.DRIVING)

                    //apiRequest2.await()

                    var latlongList: List<LatLng>? = null


                    try {
                        val routes: Array<DirectionsRoute> = apiRequest2.await()

                        for (route in routes) {
                            if (route.legs != null) {
                                for (i in route.legs.indices) {
                                    val leg = route.legs[i]
                                    if (leg.steps != null) {
                                        for (j in leg.steps.indices) {
                                            val step = leg.steps[j]

                                            val points = step.polyline
                                            if (points != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                val coords = points.decodePath()
                                                for (coord in coords) {
                                                    //path.add(LatLng(coord.lat, coord.lng))
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


                    //return points
                }
                //    }

                //rodar()
            }
            async {
                rodar()
            }.await()

            if (paths.size > 0) {

                val opts: PolylineOptions =
                    PolylineOptions().addAll(paths).color(Color.BLUE).width(5f)
                mMap.addPolyline(opts)
            }

            mMap.uiSettings.isZoomControlsEnabled = true

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6f))
        }
        //mMap.uiSettings.isZoomControlsEnabled = true

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6f))
    }
}