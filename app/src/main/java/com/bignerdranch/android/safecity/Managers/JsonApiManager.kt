package com.bignerdranch.android.safecity.Managers

import com.bignerdranch.android.safecity.ApiInterfaces.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object JsonApiManager {
    lateinit var baseUrl: String
    private lateinit var retrofit: Retrofit

    fun initialize(baseUrl: String) {
        this.baseUrl = baseUrl
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApiService: UserApiService
        get() = retrofit.create(UserApiService::class.java)
}