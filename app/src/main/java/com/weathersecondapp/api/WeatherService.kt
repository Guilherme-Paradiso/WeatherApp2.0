package com.weathersecondapp.api

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.Coil.imageLoader
import coil.request.ImageRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService (private val context: Context) {
    private var weatherAPI: WeatherServiceAPI
    init {
        val retrofitAPI = Retrofit.Builder()
            .baseUrl(WeatherServiceAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        weatherAPI = retrofitAPI.create(WeatherServiceAPI::class.java)
    }
    suspend fun getName(lat: Double, lng: Double):String? = withContext(Dispatchers.IO){
        search("$lat,$lng")?.name // retorno
    }
    suspend fun getLocation(name: String): LatLng? = withContext(Dispatchers.IO){
        search(name)?.let { LatLng(it.lat ?: 0.0, it.lon ?: 0.0) }
    }
    private fun search(query: String) : APILocation? {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        val apiLoc = call.execute().body()
        return if (!apiLoc.isNullOrEmpty()) apiLoc[0] else null
    }
    suspend fun getWeather(name: String): APICurrentWeather? =
        withContext(Dispatchers.IO) {
            val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
            call.execute().body() // retorno
        }
    suspend fun getForecast(name: String) : APIWeatherForecast? =
        withContext(Dispatchers.IO) {
            val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
            call.execute().body()
        }
    suspend fun getBitmap(imgUrl: String) : Bitmap? = withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context).data(imgUrl)
            .allowHardware(false).build()
        val loader = Coil.imageLoader(context)
        val response = loader.execute(request)
        response.drawable?.toBitmap()
    }
}