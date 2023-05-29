package com.bignerdranch.android.safecity

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.bignerdranch.android.safecity.HelperClass.Gender
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import com.bignerdranch.android.safecity.ui.theme.Grey
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddingCrimeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Добавление опасности", true, onBackPressed = { onBackPressed() })
                        AddingDangerScreen(onBackPressed = { onBackPressed() })
                    }
                }
            }
        }
    }
}

@Composable
fun AddingDangerScreen(onBackPressed: () -> Unit) {
    Column(modifier = Modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val coordX = remember { mutableStateOf("") }
        val coordY = remember { mutableStateOf("") }
        val city = remember { mutableStateOf("") }
        val street = remember { mutableStateOf("") }
        val house = remember { mutableStateOf("") }

        val comment = remember { mutableStateOf("") }
        val type = remember { mutableStateOf("") }
        val dateOfDanger = remember { mutableStateOf(LocalDateTime.now()) }
        val dateOfInfo = remember { mutableStateOf(LocalDateTime.now()) }

        val victims = remember { mutableStateListOf<MutableState<Victim>>() }

        AddressBox(coordX, coordY, city, street, house)
        DangerBox(
            comment = comment,
            type = type,
            dateOfDanger = dateOfDanger,
            dateOfInfo = dateOfInfo
        )
        VictimsBox(victims)
        AddButton(city, street, house, coordX, coordY, type, comment, dateOfDanger, dateOfInfo, victims)
        BackButton(onBackPressed)
    }
}

@Composable
fun AddressBox(
    coordX: MutableState<String>,
    coordY: MutableState<String>,
    city: MutableState<String>,
    street: MutableState<String>,
    house: MutableState<String>
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val hasLocationPermission =
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!city.value.isNotBlank() && !street.value.isNotBlank() && !house.value.isNotBlank()) {
                        coordX.value = location.latitude.toString()
                        coordY.value = location.longitude.toString()

                        GlobalScope.launch(Dispatchers.IO) {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0]
                                withContext(Dispatchers.Main) {
                                    if (!city.value.isNotBlank()) {
                                        city.value = address.locality ?: ""
                                    }
                                    if (!street.value.isNotBlank()) {
                                        street.value = address.thoroughfare ?: ""
                                    }
                                    if (!house.value.isNotBlank()) {
                                        house.value = address.subThoroughfare ?: ""
                                    }
                                }
                            }
                            isLoading.value = false
                        }
                    }
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }

            try {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
            } catch (e: SecurityException) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Не удалось определить местоположение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (isLoading.value) {
        Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))) {
            Column(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)) {
                Text(text = "Определение местоположения", modifier = Modifier.align(Alignment.CenterHorizontally))
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    } else {
        Column(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)) {
            Text(text = "Адрес происшествия")
            Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))) {
                Column() {
                    Row(Modifier.fillMaxWidth()) {
                        CoordInput(
                            label = "Широта",
                            placeholder = "Широта",
                            text = coordX,
                            Modifier.weight(1f)
                        )
                        CoordInput(
                            label = "Долгота",
                            placeholder = "Долгота",
                            text = coordY,
                            Modifier.weight(1f)
                        )
                    }
                    AddressInput(
                        label = "Город",
                        placeholder = "Введите город",
                        text = city,
                        city,
                        street,
                        house,
                        coordX,
                        coordY
                    )
                    AddressInput(
                        label = "Улица",
                        placeholder = "Введите улицу",
                        text = street,
                        city,
                        street,
                        house,
                        coordX,
                        coordY
                    )
                    AddressInput(
                        label = "Дом",
                        placeholder = "Введите дом",
                        text = house,
                        city,
                        street,
                        house,
                        coordX,
                        coordY
                    )
                }
            }
        }
    }
}

@Composable
fun AddressInput(label: String, placeholder: String, text: MutableState<String>,
                 city: MutableState<String>, street: MutableState<String>,
                 house: MutableState<String>, coordX: MutableState<String>,
                 coordY: MutableState<String>) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it; updateCoordinates(context, city.value, street.value, house.value, coordX, coordY) },
        label = { Text(label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )
}

fun updateCoordinates(context: Context, city: String, street: String, house: String, coordX: MutableState<String>, coordY: MutableState<String>) {
    GlobalScope.launch(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = "$house, $street, $city"
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            withContext(Dispatchers.Main) {
                coordX.value = location.latitude.toString()
                coordY.value = location.longitude.toString()
            }
        }
    }
}

@Composable
fun VictimsBox(victims: MutableList<MutableState<Victim>>){
    val victimsState = remember { mutableStateListOf(*victims.toTypedArray()) }

    Column(Modifier.padding(16.dp)) {
        Text(text = "Пострадавшие")
        Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))) {
            VictimsList(victims = victims)
        }
    }
}

@Composable
fun VictimsList(victims: MutableList<MutableState<Victim>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        victims.forEachIndexed { index, victimState ->
            val victim = victimState.value
            VictimRow(
                victim = victim,
                onGenderSelected = { gender -> victimState.value = victim.copy(gender = gender) },
                onAgeChanged = { age -> victimState.value = victim.copy(age = age) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Add new victim button
        Button(
            onClick = { victims.add(mutableStateOf(Victim(Gender.Мужской, ""))) },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(backgroundColor = Grey, contentColor = Color.White)
        ) {
            Text(text = "Добавить пострадавшего")
        }
    }
}

@Composable
fun VictimRow(
    victim: Victim,
    onGenderSelected: (Gender) -> Unit,
    onAgeChanged: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val focusManager = LocalFocusManager.current

        GenderDropdown(selectedGender = victim.gender, onGenderSelected = { gender -> onGenderSelected(gender) }, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = victim.age,
            onValueChange = { newAge -> onAgeChanged(newAge) },
            label = { Text(text = "Возраст") },
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .padding(end = 8.dp)
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}

data class Victim(
    var gender: Gender,
    var age: String
)

@Composable
fun GenderDropdown(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit,
    modifier: Modifier
) {
    val genderValues = Gender.values()

    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .padding(8.dp)
            .height(60.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "DropdownIcon",
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        )
        Column() {
            Text(
                text = "Пол",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
            Text(
                text = selectedGender.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .clickable { expanded = !expanded }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            genderValues.forEach { genderItem ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onGenderSelected(genderItem)
                    }
                ) {
                    Text(genderItem.name)
                }
            }
        }
    }
}

@Composable
fun CoordInput(label: String, placeholder: String, text: MutableState<String>, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(label) },
        placeholder = { Text(text = placeholder) },
        modifier = modifier
            .padding(8.dp)
            .height(60.dp)
            .fillMaxWidth(),
        singleLine = true,
        enabled = false
    )
}

@Composable
fun DangerBox(comment: MutableState<String>, type: MutableState<String>, dateOfDanger: MutableState<LocalDateTime>,
              dateOfInfo: MutableState<LocalDateTime>){
    Column(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)) {
        Text(text = "Происшествие")
        Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))){
            Column() {
                TypeDropdown(
                    selectedType = type,
                    onTypeSelected = { nowType -> type.value = nowType })
                DateTimeInput(selectedDateTime = dateOfDanger, label = "Время происшествия")
                DateTimeInput(selectedDateTime = dateOfInfo, label = "Время записи", isMutable = false)
                InputCommentArea(text = comment)
            }
        }
    }
}

@Composable
fun DateTimeInput(
    selectedDateTime: MutableState<LocalDateTime>,
    label: String,
    isMutable: Boolean = true
) {
    val context = LocalContext.current

    val datePickerDialog = remember { DatePickerDialog(context) }
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val currentDateTime = selectedDateTime.value
                val newDateTime = currentDateTime.withHour(hourOfDay).withMinute(minute)
                selectedDateTime.value = newDateTime
            },
            selectedDateTime.value.hour,
            selectedDateTime.value.minute,
            true
        )
    }

    val dateTimeFormat = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    val dateTimeText = selectedDateTime.value.format(dateTimeFormat)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                if (isMutable) {
                    datePickerDialog.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        val currentDateTime = selectedDateTime.value
                        val newDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                        val newDateTime = currentDateTime
                            .withYear(year)
                            .withMonth(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth)
                        selectedDateTime.value = newDateTime
                        timePickerDialog.updateTime(newDateTime.hour, newDateTime.minute)
                        timePickerDialog.show()
                    }
                    datePickerDialog.show()
                }
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                shape = RoundedCornerShape(4.dp)
            )
            .background(color = MaterialTheme.colors.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "calendarIcon",
            modifier = Modifier.padding(end = 8.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
            Text(
                text = dateTimeText,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun TypeDropdown(
    selectedType: MutableState<String>,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var typeValues by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current

    suspend fun fetchTypeList() {
        try {
            val types = GsonApiManager.typeApiService.getAllTypeNames()
            typeValues = types
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Не удалось получить типы опасностей",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        fetchTypeList()
    }

    Row(
        modifier = Modifier
            .wrapContentHeight()
            .padding(8.dp)
            .height(70.dp)
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "DropdownIcon",
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(end = 8.dp)
        )
        Column {
            Text(
                text = "Тип происшествия",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
            )
            Text(
                text = selectedType.value,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            typeValues.forEach { typeItem ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onTypeSelected(typeItem)
                    }
                ) {
                    Text(typeItem)
                }
            }
        }
    }
}


@Composable
fun InputCommentArea(text: MutableState<String>) {
    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.White
    )
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(text = "Комментарий") },
        placeholder = { Text(text = "Введите комментарий") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 100.dp)
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)),
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        )
    )
}

@Composable
fun AddButton(
    city: MutableState<String>, street: MutableState<String>, house: MutableState<String>,
    coordX: MutableState<String>, coordY: MutableState<String>, type: MutableState<String>,
    comment: MutableState<String>, dateOfDanger: MutableState<LocalDateTime>,
    dateOfInfo: MutableState<LocalDateTime>,
    victims: MutableList<MutableState<Victim>>
) {
    val context = LocalContext.current

    Button(
        onClick = {
            var addressId = -1
            var crimeId: Int
            CoroutineScope(Dispatchers.IO).launch {
                var message = -1
                var crimeMessage = -1;
                var answer = ""
                try {
                    val response = ScalarsApiManager.addressApiService.addAddress(
                        city = city.value,
                        street = street.value,
                        houseNumber = house.value,
                        coordX = coordX.value.toDouble(),
                        coordY = coordY.value.toDouble()
                    )
                    message = response
                    if (message > -1) {
                        addressId = message
                        answer = "Успешно добавлено"
                    }

                    val typeId = ScalarsApiManager.typeApiService.getTypeByName(type.value)
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                    val dateOfDangerFormatted = dateOfDanger.value.format(formatter)
                    val dateOfInfoFormatted = dateOfInfo.value.format(formatter)
                    val crimeResponse = ScalarsApiManager.crimeApiService.addCrime(
                        timeCrime = LocalDateTime.parse(dateOfDangerFormatted, formatter),
                        timeRecord = LocalDateTime.parse(dateOfInfoFormatted, formatter),
                        comment = comment.value,
                        address = response,
                        type = typeId,
                        witness = GsonApiManager.userApiService.getUserData(AuthManager.getUsername()).id
                    )
                    crimeMessage = crimeResponse
                    if (crimeMessage > -1) {
                        crimeId = message
                        answer = "Успешно добавлено"
                        CoroutineScope(Dispatchers.Main).launch {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }

                    var counter = 0
                    victims.forEachIndexed { index, victimState ->
                        val victim = victimState.value
                        if (victim.gender != null && victim.age != null) {
                            val victimResponse = ScalarsApiManager.victimApiService.addVictim(
                                gender = victim.gender.name,
                                age = Integer.parseInt(victim.age),
                                crime = crimeResponse
                            )
                            if (victimResponse.equals("Пострадавший добавлен")){
                                counter++
                            }
                        }
                    }
                    answer += "Успешно добавлено"

                } catch (e: Exception) {
                    answer = "Ошибка добавления"

                    if (addressId > -1) {
                        val deleteResponse = ScalarsApiManager.addressApiService.deleteAddress(addressId)
                    }
                } finally {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            context,
                            answer,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Добавить")
    }
}