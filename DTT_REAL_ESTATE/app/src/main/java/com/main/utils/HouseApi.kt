package com.main.utils

import com.main.model.House
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header


interface HouseApi {

    @GET("/api/house")
    suspend fun getHouses(@Header("Access-Key") key: String): Response<List<House>>
}

