package com.example.weather_forcasting

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_forcasting.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//dff8a2d6228867670b804fe5debbfa5b

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("patna")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "dff8a2d6228867670b804fe5debbfa5b", "matric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.temp.text = "$temperature °C"
                    binding.weatherCondition.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidityMeasure.text = "$humidity %"
                    binding.windMeasure.text = "$windSpeed m/s"
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.sunrisetime.text = "${time(sunrise)}"
                    binding.sunsettime.text = "${time(sunset)}"
                    binding.conditionMeasure.text = "$condition"
                    binding.cityName.text = "$cityName"
                    binding.date.text = date()
                    binding.day.text = dayName(System.currentTimeMillis())
                    backgroundChange(condition)

                    // Log.d("TAG","onResponse : $temperature")
                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun backgroundChange(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "Mist", "Fog", "Overcast", "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.cloudy)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Rain", "Drizzle", "Moderate Rain", "Shower", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rainy)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String? {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String? {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}