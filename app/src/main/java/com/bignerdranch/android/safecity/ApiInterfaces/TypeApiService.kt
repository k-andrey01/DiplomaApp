package com.bignerdranch.android.safecity.ApiInterfaces

import retrofit2.http.GET
import retrofit2.http.Query

interface TypeApiService {
    @GET("type/allNames")
    suspend fun getAllTypeNames(): List<String>

    @GET("type/getByType")
    suspend fun getTypeByName(@Query("typeName") typeName: String): Int
}