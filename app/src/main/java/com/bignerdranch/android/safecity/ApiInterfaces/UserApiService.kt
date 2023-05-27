package com.bignerdranch.android.safecity.ApiInterfaces

import com.bignerdranch.android.safecity.DataClasses.User
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.LocalDate

interface UserApiService {
    @GET("/user/all")
    suspend fun getUsers(): List<User>

    @POST("/user/add")
    suspend fun addUser(
        @Query("login") login: String,
        @Query("password") password: String,
        @Query("name") name: String,
        @Query("surname") surname: String,
        @Query("birthdate") birthdate: LocalDate,
        @Query("gender") gender: String
    ): String
}