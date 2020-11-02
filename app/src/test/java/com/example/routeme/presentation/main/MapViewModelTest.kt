package com.example.routeme.presentation.main

import android.graphics.Color
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.routeme.data.repositories.MapsResult
import com.example.routeme.data.repositories.RoutesRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.maps.model.Polyline
import com.google.maps.GeoApiContext
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var polylinesLiveDataObserver: Observer<PolylineOptions>

    @Mock
    private lateinit var errorPolilyneLiveData: Observer<Exception>

    @Mock
    private lateinit var markerOptionsLiveDataObserver: Observer<MarkerOptions>

    @Mock
    private lateinit var errorMarkerOptionsLiveDataObserver: Observer<Exception>

    private lateinit var viewModel: MapViewModel


    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp(){
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Mock
    lateinit var geoApicontext: GeoApiContext

    // Ao adicionar MarkerOption
    // Verifica se retornou com sucesso
    @Test
    fun `when get markerOption return sucess`(){
        // Arrange
        val latLng = LatLng(0.0, 0.0)
        val markerOptions = MarkerOptions().position(latLng)
        val resultSucess = MockRouteDataSource(MapsResult.Success(markerOptions))
        viewModel = MapViewModel(resultSucess)
        viewModel.markerOptionsLiveData.observeForever(markerOptionsLiveDataObserver)

        // Act
        viewModel.createMarkerOption(latLng, true)

        // Assert
        verify(markerOptionsLiveDataObserver).onChanged(markerOptions)
    }

    // Ao adicionar MarkerOption
    // Verifica se retornou o erro
    @Test
    fun `when get markerOption return erro`(){
        // Arrange
        val latLng = LatLng(0.0, 0.0)
        val error = Exception()
        val resultSucess = MockRouteDataSource(MapsResult.ApiError(error))
        viewModel = MapViewModel(resultSucess)
        viewModel.errorMarkerOptionsLiveData.observeForever(errorMarkerOptionsLiveDataObserver)

        // Act
        viewModel.createMarkerOption(latLng, true)

        // Assert
        verify(errorMarkerOptionsLiveDataObserver).onChanged(error)
    }

    class MockRouteDataSource(private val result: MapsResult) : RoutesRepository {
        override suspend fun getPathRoute(
            geoApiContext: GeoApiContext,
            origin: LatLng,
            destination: LatLng,
            mapResultCallback: (result: MapsResult) -> Unit
        ) {
            mapResultCallback(result)
        }

        override fun createMarkerOption(
            latLng: LatLng,
            myPostionBoolean: Boolean,
            mapResultCallback: (result: MapsResult) -> Unit
        ) {
            mapResultCallback(result)
        }

    }



}