package com.tutorial.kneecast.data.model

data class WeatherResponse(
    val Feature: List<WeatherFeature>
)

data class WeatherFeature(
    val Property: WeatherProperty
)

data class WeatherProperty(
    val WeatherList: WeatherList
)

data class WeatherList(
    val Weather: List<Weather>
)

data class Weather(
    val Type: String,
    val Date: String,
    val Rainfall: String,
    val Temperature: Temperature,
    val Image: Image
)

data class Temperature(
    val Celsius: String
)

data class Image(
    val URL: String,
    val Title: String
)