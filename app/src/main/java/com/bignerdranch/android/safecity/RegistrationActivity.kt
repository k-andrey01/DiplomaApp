package com.bignerdranch.android.safecity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Регистрация", true, onBackPressed = { onBackPressed() })
                        RegistrationScreen(onBackPressed = { onBackPressed() })
                    }
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val passwordConfirm = remember { mutableStateOf("") }
        val name = remember { mutableStateOf("") }
        val surname = remember { mutableStateOf("") }
        val birthday = remember { mutableStateOf(LocalDate.now()) }
        val selectedGender = remember { mutableStateOf(Gender.Мужской) }

        EmailInput(email = email)
        PasswordInput(password = password)
        PasswordConfirm(password = passwordConfirm)

        AnyInput(label = "Фамилия", placeholder = "Введите фамилию", surname)
        AnyInput(label = "Имя", placeholder = "Введите имя", name)
        DateInput(selectedDate = birthday, "Дата рождения")
        GenderDropdown(
            selectedGender = selectedGender.value,
            onGenderSelected = { gender ->
                selectedGender.value = gender
            }
        )

        RegisterButton()
        BackButton(onBackPressed)
    }
}

@Composable
fun PasswordConfirm(password: MutableState<String>) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Подтверждение пароля") },
        placeholder = { Text(text = "Повторите пароль") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Icon(
                    imageVector = getVisibilityIcon(passwordVisible),
                    contentDescription = "Password Visibility Icon"
                )
            }
        }
    )
}

private fun getVisibilityIcon(passwordVisible: Boolean): ImageVector {
    return if (passwordVisible) {
        Icons.Filled.Visibility
    } else {
        Icons.Filled.VisibilityOff
    }
}

@Composable
fun AnyInput(label: String, placeholder: String, text: MutableState<String>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true
    )
}

@Composable
fun DateInput(selectedDate: MutableState<LocalDate>, label: String) {
    val context = LocalContext.current

    val datePickerDialog = remember { DatePickerDialog(context) }
    val dateFormat = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    val dateText = selectedDate.value.format(dateFormat)

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                datePickerDialog.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val newDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                    selectedDate.value = newDate
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
                text = dateText,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun GenderDropdown(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    val genderValues = Gender.values()

    var expanded by remember { mutableStateOf(false) }

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
fun RegisterButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Зарегистрироваться")
    }
}