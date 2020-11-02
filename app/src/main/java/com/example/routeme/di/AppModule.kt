package com.example.routeme.di

import com.example.routeme.data.repositories.RouteDataSource
import com.example.routeme.data.repositories.RoutesRepository
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
    fun provideRouteRepository() = RouteDataSource() as RoutesRepository

}