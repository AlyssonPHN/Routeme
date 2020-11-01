package com.example.routeme.presentation.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.routeme.presentation.MainActivityMap2
import com.example.routeme.R
import com.example.routeme.presentation.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewModel.getStart()

        viewModel.startLiveData.observe(this, {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}