package com.bignerdranch.android.safecity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.HelperClass.Gender
import com.bignerdranch.android.safecity.Managers.JsonApiManager
import com.bignerdranch.android.safecity.ui.theme.Grey
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        val victims = remember { mutableStateListOf<Victim>() }

        AddressBox(coordX, coordY, city, street, house)
        DangerBox(
            comment = comment,
            type = type,
            dateOfDanger = dateOfDanger,
            dateOfInfo = dateOfInfo
        )
        VictimsBox(victims)
        AddButton()
        BackButton(onBackPressed)
    }
}

@Composable
fun AddressBox(coordX: MutableState<String>, coordY: MutableState<String>, city: MutableState<String>,
               street: MutableState<String>, house: MutableState<String>) {
    Column(modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)) {
        Text(text = "Адрес происшествия")
        Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))) {
            Column() {
                Row(Modifier.fillMaxWidth()) {
                    CoordInput(label = "Широта", placeholder = "Широта", text = coordX, Modifier.weight(1f))
                    CoordInput(label = "Долгота", placeholder = "Долгота", text = coordY, Modifier.weight(1f))
                }
                AnyInput(label = "Город", placeholder = "Введите город", text = city)
                AnyInput(label = "Улица", placeholder = "Введите улицу", text = street)
                AnyInput(label = "Дом", placeholder = "Введите дом", text = house)
            }
        }
    }
}

@Composable
fun VictimsBox(victims: SnapshotStateList<Victim>){
    Column(Modifier.padding(16.dp)) {
        Text(text = "Пострадавшие")
        Box(modifier = Modifier.border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))) {
            VictimsList(victims = victims)
        }
    }
}

@Composable
fun VictimsList(victims: SnapshotStateList<Victim>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        victims.forEachIndexed { index, victim ->
            VictimRow(
                gender = victim.gender,
                age = victim.age,
                onGenderSelected = { gender -> victims[index].gender = gender },
                onAgeChanged = { age -> victims[index].age = age }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Add new victim button
        Button(
            onClick = { victims.add(Victim(Gender.Мужской, "")) },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(backgroundColor = Grey, contentColor = Color.White)
        ) {
            Text(text = "Добавить пострадавшего")
        }
    }
}

@Composable
fun VictimRow(
    gender: Gender,
    age: String,
    onGenderSelected: (Gender) -> Unit,
    onAgeChanged: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GenderDropdown(selectedGender = gender, onGenderSelected = onGenderSelected, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChanged,
            label = { Text(text = "Возраст") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
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
            .wrapContentHeight()
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
                .padding(end = 8.dp)
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
        singleLine = true
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
                DateTimeInput(selectedDateTime = dateOfInfo, label = "Время записи")
                InputCommentArea(text = comment)
            }
        }
    }
}

@Composable
fun DateTimeInput(selectedDateTime: MutableState<LocalDateTime>, label: String) {
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

    val dateTimeFormat = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }
    val dateTimeText = selectedDateTime.value.format(dateTimeFormat)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
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
            val types = JsonApiManager.typeApiService.getAllTypeNames()
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
            .padding(16.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)),
        colors = textFieldColors
    )
}

@Composable
fun AddButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Добавить")
    }
}