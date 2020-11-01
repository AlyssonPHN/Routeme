package com.example.routeme.presentation.welcome

import PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import PermissionUtils.isPermissionGranted
import PermissionUtils.requestPermission
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.routeme.R
import kotlinx.android.synthetic.main.activity_welcome.*
import com.example.routeme.presentation.MainActivityMap2
import com.example.routeme.presentation.MapsActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        enableMyLocation()

        btnMap.setOnClickListener {
            // Ir para o Map
            //val intent = Intent(this, MainActivityMap2::class.java)
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Quando a permissão foi concedida
        if (requestCode != MainActivityMap2.LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        // Quando a permissão foi negada
        if (!isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showMissingPermissionError()
        }
    }

    // Pedir Permissão de localização
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        // Permission to access the location is missing. Show rationale and request permission
        requestPermission(
            this, MainActivityMap2.LOCATION_PERMISSION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION, true
        )
        requestPermission(
            this, MainActivityMap2.LOCATION_PERMISSION_REQUEST_CODE,
            Manifest.permission.ACCESS_COARSE_LOCATION, true
        )
    }

    // Mostrar Permissão negada
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }
}