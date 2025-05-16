package com.tutorial.kneecast.data.remote

import com.tutorial.kneecast.data.model.GeocoderResponse
import com.tutorial.kneecast.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeocodeRepository {

    // Yahoo!ジオコーダAPIのベースURL
    private val geocoderBaseUrl = "https://map.yahooapis.jp/"
    private var geocoderApi: GeocoderApi = RetrofitFactory
        .createRetrofitInstance(geocoderBaseUrl)
        .create(GeocoderApi::class.java)

    suspend fun getResponseFromAddress(address: String): GeocoderResponse? {
        return withContext(Dispatchers.IO) {
            try {
                if (address.isEmpty()) return@withContext null
                
                val geoResponse = geocoderApi.getCoordinates(address = address)
                if (!geoResponse.isSuccessful) return@withContext null

                val geoResponseBody = geoResponse.body() ?: return@withContext null
                geoResponseBody
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
} 