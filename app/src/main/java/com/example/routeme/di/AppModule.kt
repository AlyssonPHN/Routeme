package com.example.routeme.di

import com.example.routeme.presentation.main.RouteRepository
import com.example.routeme.presentation.splash.SplashRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSplashRepository() = SplashRepository()

    @Singleton
    @Provides
    fun provideRouteRepository() = RouteRepository()

}