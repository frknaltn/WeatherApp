package com.frkn.weatherapp.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.frkn.weatherapp.R
import com.frkn.weatherapp.databinding.ActivityMainBinding
import com.frkn.weatherapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var cName = GET.getString("cityNanme","ankara")

        binding.editCityName.setText(cName)

        viewModel.refreshData(cName!!)

        getLiveData()

        binding.swipeRefresh.setOnRefreshListener {
            binding.dataView.visibility = View.GONE
            binding.errorText.visibility = View.GONE
            binding.pbLoading.visibility = View.GONE

            var cityName = GET.getString("cityName",cName)
            binding.editCityName.setText(cityName)
            viewModel.refreshData(cityName!!)
            binding.swipeRefresh.isRefreshing = false
        }

        binding.imgSearchCityName.setOnClickListener {
            val cityName = binding.editCityName.text.toString()
            SET.putString("cityName",cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()
        }
        binding.editCityName.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val cityName = binding.editCityName.text.toString()
                SET.putString("cityName",cityName)
                SET.apply()
                viewModel.refreshData(cityName)
                getLiveData()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }

    private fun getLiveData() {
        viewModel.weatherData.observe(this, Observer { data ->
            data?.let {
                binding.dataView.visibility = View.VISIBLE
                binding.pbLoading.visibility = View.GONE
                binding.defaultCity.visibility = View.VISIBLE
                binding.defaultCity.text = data.name.toString()
                binding.degree.text = data.main.temp.toString() + "°C"
                binding.countryCode.text = data.sys.country
                binding.cityName.text = data.name.toString()
                binding.humidity.text = data.main.humidity.toString()
                binding.speed.text = data.wind.speed.toString() + "%"
                binding.lat.text = data.coord.lat.toString()
                binding.lon.text = data.coord.lon.toString()
                binding.editCityName.text.clear()

                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather[0].icon + "@2x.png")
                    .into(binding.imgWeatherIcon)
            }

        })
        viewModel.weatherLoad.observe(this, Observer { load ->
            load?.let {
                if (it){
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                    binding.dataView.visibility = View.GONE
                    binding.defaultCity.visibility = View.GONE
                }else{
                    binding.pbLoading.visibility = View.GONE
                }
            }
        })
        viewModel.weatherError.observe(this, Observer {error ->
            error?.let {
                if(it){
                    binding.errorText.visibility = View.VISIBLE
                    binding.dataView.visibility = View.GONE
                    binding.pbLoading.visibility = View.GONE
                    binding.defaultCity.visibility = View.GONE
                }else{
                    binding.errorText.visibility = View.GONE
                }
            }

        })

    }
}