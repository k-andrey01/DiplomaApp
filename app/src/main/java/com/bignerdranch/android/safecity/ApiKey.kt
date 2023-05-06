package com.bignerdranch.android.safecity

class ApiKey {
    fun getApiKey(): String{
        val apiKey = ConfProperties.getProperty("api_key")
        return apiKey
    }
}