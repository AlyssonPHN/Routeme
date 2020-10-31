package com.example.routeme.presentation.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SplashRepository @Inject constructor() {

    suspend fun getStart(): Boolean {
        return withContext(Dispatchers.Default) {
            delay(2000)
            true
        }
    }
}