package com.bignerdranch.android.safecity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue
import com.bignerdranch.android.safecity.ui.theme.White
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView

class MainActivity : ComponentActivity() {
    private val internetPermissionRequestCode = 1
    private val locationPermissionRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Карта", false, onBackPressed = { onBackPressed() })
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            MyMapView(LocalContext.current)
                        }
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                    NavigationBar(1, context = LocalContext.current)
                }
            }
        }

        checkInternetPermission()
        checkLocationPermission()
    }

    private fun checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), internetPermissionRequestCode)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            internetPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на интернет предоставлено
                    // Продолжайте выполнение необходимой логики
                } else {
                    // Разрешение на интернет не было предоставлено
                    // Обработайте это соответствующим образом (например, показав диалоговое окно с просьбой предоставить разрешение)
                }
            }
            locationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на местоположение предоставлено
                    // Продолжайте выполнение необходимой логики
                } else {
                    // Разрешение на местоположение не было предоставлено
                    // Обработайте это соответствующим образом (например, показав диалоговое окно с просьбой предоставить разрешение)
                }
            }
        }
    }
}

@Composable
fun TopBar(name: String, hasBackButton: Boolean, onBackPressed: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                name,
                modifier = Modifier.fillMaxWidth(),
                color = White,
                textAlign = TextAlign.Center
            )
        },
        backgroundColor = Blue,
        navigationIcon = if (hasBackButton) {
            {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = White
                    )
                }
            }
        } else {
            null
        }
    )
}


@Composable
fun NavigationBar(selected: Int, context: Context) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxHeight()) {
        BottomAppBar {
            IconButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Карта",
                    tint = if (selected == 1) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = {
                val intent = Intent(context, AnalysisActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Анализ",
                    tint = if (selected == 2) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = {
                val intent = Intent(context, MyListActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Filled.List,
                    contentDescription = "Мои отметки",
                    tint = if (selected == 3) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = {
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Filled.AccountBox,
                    contentDescription = "Личный кабинет",
                    tint = if (selected == 4) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}

@Composable
fun MyMapView(context: Context) {
    val mapView = remember { MapView(context) }

    AndroidView(
        factory = { mapView },
        update = { view ->
            MapKitFactory.getInstance().onStart()
            if (!MapKitFactory.getInstance().isValid) {
                MapKitFactory.getInstance().setApiKey(MyApp.apiKey)
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Добавляем кнопку поверх карты
        FloatingActionButton(
            onClick = {
                val intent = Intent(context, AddingCrimeActivity()::class.java)
                context.startActivity(intent)
            },
            backgroundColor = Blue,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = White,
                modifier = Modifier
                    .size(45.dp)
            )
        }
    }
}