package com.bignerdranch.android.safecity.ApiInterfaces

import android.telecom.Call
import com.bignerdranch.android.safecity.DataClasses.Crime
import com.fasterxml.jackson.annotation.JsonFormat
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDateTime

interface CrimeApiService {
    @POST("crime/add")
    suspend fun addCrime(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Query("timeCrime") timeCrime: LocalDateTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @Query("timeRecord") timeRecord: LocalDateTime,
        @Query("comment") comment: String,
        @Query("address") address: Int,
        @Query("type") type: Int,
        @Query("witness") witness: Int
    ): Int

    @GET("crime/allForMap")
    suspend fun getAllCrimesForMap(): List<Crime>

    @GET("crime/countByKind")
    suspend fun getCountByKind(): Map<String, Integer>
}