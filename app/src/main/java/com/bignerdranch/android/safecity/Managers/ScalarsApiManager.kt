package com.bignerdranch.android.safecity.Managers

import com.bignerdranch.android.safecity.ApiInterfaces.AddressApiService
import com.bignerdranch.android.safecity.ApiInterfaces.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ScalarsApiManager {
    lateinit var baseUrl: String
    private lateinit var retrofit: Retrofit

    fun initialize(baseUrl: String) {
        ScalarsApiManager.baseUrl = baseUrl
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    val userApiService: UserApiService
        get() = retrofit.create(UserApiService::class.java)
    val addressApiService: AddressApiService
        get() = retrofit.create(AddressApiService::class.java)
}
