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

//        val properties = Properties()
//        val inputStream: InputStream = this.resources.assets.open("usersValues/conf.properties")
//        properties.load(inputStream)
//        apiKey = properties.getProperty("api_key")
        //val apiKeyGetter = ApiKey()

        //apiKey = apiKeyGetter.getApiKey()
        MapKitFactory.setApiKey(apiKey)

        val intent = Intent(getApplicationContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}