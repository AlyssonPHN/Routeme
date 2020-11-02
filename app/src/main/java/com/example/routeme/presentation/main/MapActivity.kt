package com.example.routeme.presentation.main

import PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import PermissionUtils.isPermissionGranted
import PermissionUtils.requestPermission
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.routeme.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.GeoApiContext
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, OnRequestPermissionsResultCallback {

    lateinit var viewModel: MapViewModel
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private var myPostion: LatLng? = null
    private var geoApicontext: GeoApiContext? = null
    // Responsável por pegar a localização atual
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    // Para pegar a atualização atual
    private val locationRequest = LocationRequest.create().apply {
        interval = 10_000
        fastestInterval = 5_000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map2)
        initializePlace()
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        //Inicializa o contexto do GeoApi
        geoApicontext = GeoApiContext().setApiKey(getString(R.string.google_maps_key))


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getMyLocationCallback()
        startLocationUpdates()
        showPoint()
        showRoute()
        showErrors()
    }

    // Inicializa suporte Place
    private fun initializePlace() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        initializeAutoComplete()
    }

    // Inicializado AutoComplete
    private fun initializeAutoComplete() {
        // Inicializa o AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Filtrar apenas endereço
        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS)
        // Filtro para trazer apenas endereços do Brasil
        autocompleteFragment.setCountries("BR")

        // Especifica o tipo de retorno
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        // Quando o endereço é escolhido isso é chamado
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.createMarkerOption(place.latLng!!, false)
                viewModel.getPolylines(geoApicontext!!, myPostion!!, place.latLng!!)
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext, getString(R.string.erro_autocomplete), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        googleMap.setOnMyLocationButtonClickListener(this)
        enableMyLocation()
    }

    // Mostrar rota
    private fun showRoute() {
        viewModel.polylinesLiveData.observe(this, { polylines ->
            // Adiciona a polylines que são responsáveis por traçar a rota
            map.addPolyline(polylines)
            map.uiSettings.isZoomControlsEnabled = true
            // bound é necessário para dar zoom nos pontos adicionado
            val bounds = viewModel.builder.build()
            // Zoom em todos os pontos
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        })
    }

    // Mostrar erros
    private fun showErrors(){
        // Erro ao buscar rotas
        viewModel.errorPolilyneLiveData.observe(this, {
            Toast.makeText(this, getString(R.string.erro_get_rota), Toast.LENGTH_SHORT).show()
        })
        // Erro ao adicionar ponto no mapa.
        viewModel.errorMarkerOptionsLiveData.observe(this, {
            Toast.makeText(this, getString(R.string.erro_point_map), Toast.LENGTH_SHORT).show()
        })
    }

    // Para de atualizar posição atual
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Escuta a mudança da minha localização
    private fun getMyLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Seta minha posição
                    myPostion = LatLng(location.latitude, location.longitude)
                    // Cria o Marcador no mapa
                    viewModel.createMarkerOption(
                        LatLng(location.latitude, location.longitude),
                        myPostionBoolean = true
                    )

                }
            }
        }
    }

    // Iniciar captura da minha localização
    private fun startLocationUpdates() {
        // Pedi as permissões necessárias
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Iniciar a captura da minha localização
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // ir para o ponto selecionado
    private fun showPoint() {
        viewModel.markerOptionsLiveData.observe(this, { markerOption ->
            map.addMarker(markerOption)
            // Direcionado para o ponto no mapa
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        markerOption.position.latitude,
                        markerOption.position.longitude
                    ), 17f
                )
            )
        })
    }

    // Vai para minha localização
    private fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Vai para minha localização
            map.isMyLocationEnabled = true
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            } else {
                // Permissão para acessar a localização
                requestPermission(
                    this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true
                )
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        enableMyLocation()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Estou aqui.", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    // Mostrar dialog pedindo a permissão novamente
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onLocationChanged(p0: Location?) {
        Toast.makeText(this, getString(R.string.my_position), Toast.LENGTH_SHORT).show()
    }

}