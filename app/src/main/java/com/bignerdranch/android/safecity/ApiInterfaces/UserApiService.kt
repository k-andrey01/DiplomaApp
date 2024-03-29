package com.bignerdranch.android.safecity.ApiInterfaces

import com.bignerdranch.android.safecity.DataClasses.User
import retrofit2.http.*
import java.time.LocalDate
import java.util.*

interface UserApiService {
    @GET("/user/selectByLogin")
    suspend fun getUserData(@Query("login") login: String): User

    @POST("/user/login")
    suspend fun login(
        @Query("login") login: String,
        @Query("password") password: String
    ): String

    @PUT("/user/updatePassword/{login}")
    suspend fun updatePassword(
        @Path("login") login: String,
        @Query("password") password: String
    ): String

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