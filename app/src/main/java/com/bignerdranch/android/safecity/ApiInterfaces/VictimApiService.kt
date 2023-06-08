package com.bignerdranch.android.safecity.ApiInterfaces

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface VictimApiService {
    @POST("/victim/add")
    suspend fun addVictim(
        @Query("age") age: Int,
        @Query("gender") gender: String,
        @Query("crime") crime: Int
    ): String

    @GET("victim/countByGender")
    suspend fun getCountByGender(): Map<String, Integer>

    @GET("victim/countByAgeGroup")
    suspend fun getCountByAgeGroup(): Map<String, Integer>
}