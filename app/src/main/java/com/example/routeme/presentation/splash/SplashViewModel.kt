package com.example.routeme.presentation.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel @ViewModelInject constructor(private val repository: SplashRepository): ViewModel() {

    val startLiveData = MutableLiveData<Boolean>()

    fun getStart() {
        CoroutineScope(Dispatchers.Main).launch {
            val start = withContext(Dispatchers.Default) {
                repository.getStart()
            }
            startLiveData.value = start
        }
    }
}