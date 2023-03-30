package com.frkn.weatherapp.service

import com.frkn.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    // https://api.openweathermap.org/data/2.5/weather?q=antalya&APPID=03060da3cecc818e129e2230bcac08ce

    @GET("data/2.5/weather?&units=metric&APPID=03060da3cecc818e129e2230bcac08ce")
    fun getData(
        @Query("q") cityName:String
    ): Single<WeatherModel>
}