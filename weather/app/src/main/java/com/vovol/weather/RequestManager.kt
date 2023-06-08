package com.vovol.weather


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface RequestManager {

    @GET("/v3/weather/weatherInfo")
    fun getWeather(@Query("city") city:String,@Query("key") key:String,@Query("extensions") extensions:String
    ): Call<WeatherInfo>
}