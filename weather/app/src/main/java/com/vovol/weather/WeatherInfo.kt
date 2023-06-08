package com.vovol.weather

import java.io.Serializable

data class WeatherInfo(
    val count: String?,
    val forecasts: List<Forecast>?,
    val info: String?,
    val infocode: String?,
    val status: String?
) : Serializable

data class Forecast(
    val adcode: String,
    val casts: List<Cast>,
    val city: String,
    val province: String,
    val reporttime: String
) : Serializable

data class Cast(
    val date: String,
    val daypower: String,
    val daytemp: String,
    val daytemp_float: String,
    val dayweather: String,
    val daywind: String,
    val nightpower: String,
    val nighttemp: String,
    val nighttemp_float: String,
    val nightweather: String,
    val nightwind: String,
    val week: String
) : Serializable