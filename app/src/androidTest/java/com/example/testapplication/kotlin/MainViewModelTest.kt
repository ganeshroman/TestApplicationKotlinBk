package com.example.testapplication.kotlin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.testapplication.models.Movie
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {
//xxxxxxxxnotTestedxxxxxxxxxxxxxxxxx
    private val testDispatcher = StandardTestDispatcher()
    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: MainRepository
    lateinit var mockWebServer: MockWebServer
    @Mock
    lateinit var gson: Gson

    @Mock
    lateinit var apiService: RetrofitService

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)//initMocks(this) // openMocks(this)
        Dispatchers.setMain(testDispatcher)//setMain(testDispatcher)


        gson = Gson()
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(RetrofitService::class.java)



        mainRepository = MainRepository(apiService)
        mainViewModel = MainViewModel(mainRepository)
    }


    @Test
    fun getAllMoviesTest() {
        runBlocking {
            Mockito.`when`(mainRepository.getAllMovies())
                .thenReturn(Response.success(listOf<Movie>(Movie("movie", "", "new"))))
            mainViewModel.getAllMovies()
            val result = mainViewModel.movieList.getOrAwaitValue()
            Assert.assertEquals(listOf<Movie>(Movie("movie", "", "new")), result)
        }
    }


    @Test
    fun `emptyMovieListTest`() {
        runBlocking {
            Mockito.`when`(mainRepository.getAllMovies())
                .thenReturn(Response.success(listOf<Movie>()))
            mainViewModel.getAllMovies()
            val result = mainViewModel.movieList.getOrAwaitValue()
            Assert.assertEquals(listOf<Movie>(), result)
        }
    }

    /*@Test
    fun `get all movie test`() {
        runBlocking {
            Mockito.`when`(apiService.getAllMovies()).thenReturn(Response.success(listOf<Movie>()))
            val response = mainRepository.getAllMovies()
            Assert.assertEquals(listOf<Movie>(), response.body())
        }

    }*/




}