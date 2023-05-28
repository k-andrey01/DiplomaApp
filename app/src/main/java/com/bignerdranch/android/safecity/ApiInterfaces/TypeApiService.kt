package com.bignerdranch.android.safecity.ApiInterfaces

import retrofit2.http.GET

interface TypeApiService {
    @GET("type/allNames")
    suspend fun getAllTypeNames(): List<String>
}