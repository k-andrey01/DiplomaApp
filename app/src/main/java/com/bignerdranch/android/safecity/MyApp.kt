package com.bignerdranch.android.safecity

import android.app.Application
import android.content.Intent
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import com.yandex.mapkit.MapKitFactory

class MyApp : Application() {

    companion object {
        lateinit var apiKey: String
    }

    override fun onCreate() {
        super.onCreate()

        ScalarsApiManager.initialize(resources.getString(R.string.api_address))
        GsonApiManager.initialize(resources.getString(R.string.api_address))

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