package com.bignerdranch.android.safecity.Managers

import com.bignerdranch.android.safecity.ApiInterfaces.TypeApiService
import com.bignerdranch.android.safecity.ApiInterfaces.UserApiService
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GsonApiManager {
    lateinit var baseUrl: String
    private lateinit var retrofit: Retrofit

    fun initialize(baseUrl: String) {
        this.baseUrl = baseUrl

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val userApiService: UserApiService
        get() = retrofit.create(UserApiService::class.java)
    val typeApiService: TypeApiService
        get() = retrofit.create(TypeApiService::class.java)

    private class LocalDateDeserializer : JsonDeserializer<LocalDate> {
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate? {
            val dateString = json?.asString
            return if (dateString != null) {
                LocalDate.parse(dateString, dateFormatter)
            } else {
                null
            }
        }
    }
}
