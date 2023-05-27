package com.bignerdranch.android.safecity

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.yandex.mapkit.MapKitFactory
import java.io.InputStream
import java.util.*

class MyApp : Application() {

    companion object {
        lateinit var apiKey: String
    }

    override fun onCreate() {
        super.onCreate()

        ApiManager.initialize(resources.getString(R.string.api_address))

        apiKey = resources.getString(R.string.api_key)
        MapKitFactory.setApiKey(apiKey)

        AuthManager.init(this)
        val intent = if (AuthManager.isLoggedIn()) {
            Intent(getApplicationContext(), MainActivity::class.java)
        } else {
            Intent(getApplicationContext(), LoginActivity::class.java)
        }
        //startActivity(intent)
        //val intent = Intent(getApplicationContext(), MainActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}