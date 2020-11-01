package com.example.routeme.presentation.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.routeme.R
import com.example.routeme.presentation.main.MapActivity
import com.example.routeme.presentation.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    lateinit var viewModel: SplashViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        viewModel.getStart()


        // Ir para a tela de boas vindas ou Maps
        viewModel.startLiveData.observe(this, {
            // Se for o primeiro acesso, ir para tela de boas vindas
            val intent: Intent = if (sharedPref.getBoolean(FIRST_ACESS, true)) {
                sharedPref.edit().putBoolean(FIRST_ACESS, false).apply()
                Intent(this, WelcomeActivity::class.java)
            } else {
                // Se não for o primeiro acesso, ir para tela do Mapa
                Intent(this, MapActivity::class.java)
            }
            startActivity(intent)
            finish()
        })
    }

    companion object {
        const val FIRST_ACESS = "firstAcess"
    }
}