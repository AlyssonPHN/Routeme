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
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
import kotlin.collections.ArrayList
import kotlin.coroutines.suspendCoroutine

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
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Mock
    lateinit var geoApicontext: GeoApiContext

    // Ao adicionar MarkerOption
    // Verifica se retornou com sucesso
    @Test
    fun `when get markerOption return sucess`() {
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
    fun `when get markerOption return erro`() {
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

    //Coroutines
    @Test
    fun `when get markerOption return sucesss`() {
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

    @Test
    fun `when get polylines return sucess`() = runBlockingTest {
        // Arrange
        val orgin = LatLng(-7.2080833, -39.3141)
        val destination = LatLng(-7.208203699999999, -39.3139455)

        val out1 = LatLng(-7.2081100000000005, -39.3141)
        val out2 = LatLng(-7.20809, -39.31396)
        val paths = ArrayList<LatLng>()
        paths.add(out1)
        paths.add(out2)

        val polylines: PolylineOptions =
            PolylineOptions().addAll(paths).color(Color.BLUE).width(5f)

        val resultSucess = MockRouteDataSource(MapsResult.Success(paths))
        viewModel = MapViewModel(resultSucess)
        viewModel.polylinesLiveData.observeForever(polylinesLiveDataObserver)


        // Act
        viewModel.getPolylines(geoApicontext, orgin, destination)

        // Assert
        verify(polylinesLiveDataObserver).onChanged(polylines)
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