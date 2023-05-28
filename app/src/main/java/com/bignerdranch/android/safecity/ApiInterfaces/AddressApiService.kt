package com.bignerdranch.android.safecity.ApiInterfaces

import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate

interface AddressApiService {
    @POST("/address/add")
    suspend fun addAddress(
        @Query("city") city: String,
        @Query("street") street: String,
        @Query("houseNumber") houseNumber: String,
        @Query("coordX") coordX: Double,
        @Query("coordY") coordY: Double
    ): Int
}