package com.bignerdranch.android.safecity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bignerdranch.android.safecity.DataClasses.Crime
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue
import com.bignerdranch.android.safecity.ui.theme.White
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch


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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                internetPermissionRequestCode
            )
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            internetPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на интернет предоставлено
                } else {
                    // Разрешение на интернет не было предоставлено
                }
            }
            locationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение на местоположение предоставлено
                } else {
                    // Разрешение на местоположение не было предоставлено
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
    var crimesList by remember { mutableStateOf(emptyList<Crime>()) }
    var selectedCrime by remember { mutableStateOf<Crime?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    suspend fun fetchCrimeList() {
        try {
            val crimes = GsonApiManager.crimeApiService.getAllCrimesForMap()
            crimesList = crimes

            mapView.map.mapObjects.clear()

            crimes.forEach { crime ->
                val point = Point(crime.coordX, crime.coordY)
                var img = ImageProvider.fromResource(context, R.drawable.another)
                when (crime.kind) {
                    "Физические" -> img = ImageProvider.fromResource(context, R.drawable.phisical)
                    "Имущественные" -> img = ImageProvider.fromResource(context, R.drawable.rob)
                    "Нарушение общественной безопасности" -> img =
                        ImageProvider.fromResource(context, R.drawable.publics)
                    "Психологические и обман" -> img =
                        ImageProvider.fromResource(context, R.drawable.money)
                    "Природные и техногенные" -> img =
                        ImageProvider.fromResource(context, R.drawable.enviroment)
                }
                val marker = mapView.map.mapObjects.addPlacemark(
                    point,
                    img,
                    IconStyle().apply {
                        anchor = PointF(0.5f, 0.5f)
                        scale = 0.15f / mapView.map.cameraPosition.zoom
                    }
                )

                marker.addTapListener(object : MapObjectTapListener {
                    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
                        coroutineScope.launch {
                            selectedCrime = crime
                            showDialog = true
                        }
                        return true
                    }
                })
            }

            mapView.map.move(
                CameraPosition(
                    Point(crimes.firstOrNull()?.coordX ?: 0.0, crimes.firstOrNull()?.coordY ?: 0.0),
                    10.0f,
                    0.0f,
                    0.0f
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Не удалось получить информацию об опасностях",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchCrimeList()
        }
    }

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
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            FloatingActionButton(
                onClick = {
                    showHelpDialog = true
                },
                backgroundColor = Blue,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Информация",
                    tint = White,
                    modifier = Modifier
                        .size(45.dp)
                )
            }
            // Add a button on top of the map
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, AddingCrimeActivity::class.java)
                    context.startActivity(intent)
                },
                backgroundColor = Blue,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить",
                    tint = White,
                    modifier = Modifier
                        .size(45.dp)
                )
            }
        }

        if (showDialog && selectedCrime != null) {
            CrimeDetailsBalloon(
                selectedCrime = selectedCrime!!,
                onCloseClicked = { showDialog = false })
        }

        if (showHelpDialog) {
            HelpDialog(onCloseClicked = { showHelpDialog = false })
        }
    }
}

@Composable
fun HelpDialog(onCloseClicked: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCloseClicked.invoke() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Помощь", modifier = Modifier.height(30.dp), fontSize = 20.sp)
                IconButton(
                    onClick = { onCloseClicked.invoke() },
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }
        },
        text = {
            Column {
                Column {
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.phisical),
                            contentDescription = "Phisical",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Физические опасности (для жизни и здоровья)")
                    }
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.rob),
                            contentDescription = "rob",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Имущественные опасности (с применением физической силы)")
                    }
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.publics),
                            contentDescription = "publ",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Нарушение общественной безопасности")
                    }
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.money),
                            contentDescription = "money",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Психологические опасности и обман")
                    }
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.enviroment),
                            contentDescription = "env",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Природные и техногенные опасности")
                    }
                    Row() {
                        Image(
                            painter = painterResource(R.drawable.another),
                            contentDescription = "another",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(" - Иные опасности")
                    }
                }
            }
        },
        confirmButton = {}
    )
}


@Composable
fun CrimeDetailsBalloon(selectedCrime: Crime?, onCloseClicked: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCloseClicked.invoke() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Информация", modifier = Modifier.height(30.dp), fontSize = 20.sp)
                IconButton(
                    onClick = { onCloseClicked.invoke() },
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть")
                }
            }
        },
        text = {
            Column {
                Text("Время преступления: ${selectedCrime?.timeCrime?.replace('T', ' ')}")
                Text("Комментарий: ${selectedCrime?.comment}")
                Text("Город: ${selectedCrime?.city}")
                Text("Улица: ${selectedCrime?.street}")
                Text("Дом: ${selectedCrime?.house}")
                Text("Тип: ${selectedCrime?.type}")
                Text("Категория: ${selectedCrime?.kind}")
                if (!selectedCrime?.victims.isNullOrEmpty()) {
                    Text("Пострадавшие:")
                    selectedCrime?.victims?.forEach { victim ->
                        Text("- Пол: ${victim.gender}, Возраст: ${victim.age}")
                    }
                } else {
                    Text("Пострадавших нет")
                }
            }
        },
        confirmButton = {}
    )
}