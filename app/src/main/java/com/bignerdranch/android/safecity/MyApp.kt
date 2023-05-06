package com.bignerdranch.android.safecity

import android.app.Application
import android.content.Intent
import com.yandex.mapkit.MapKitFactory
import java.io.InputStream
import java.util.*

class MyApp : Application() {

    companion object {
        lateinit var apiKey: String
    }

    override fun onCreate() {
        super.onCreate()

        apiKey = resources.getString(R.string.api_key)
        MapKitFactory.setApiKey(apiKey)

        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}