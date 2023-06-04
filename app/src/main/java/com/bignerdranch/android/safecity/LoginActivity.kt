package com.bignerdranch.android.safecity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {
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
                        TopBar("Вход", false, onBackPressed = { onBackPressed() })
                        LoginScreen()
                    }
                }
            }
        }
        AuthManager.init(this)

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
                } else {
                }
            }
            locationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        EmailInput(email = email)
        PasswordInput(password = password)

        EnterButton(email, password)
        RegistrationButton()
        Text(
            text = buildAnnotatedString {
                append("Забыли пароль? ")
                withStyle(
                    style = SpanStyle(
                        color = Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Нажмите здесь!")
                }
            },
            modifier = Modifier.clickable {
                val intent = Intent(context, EditingPasswordActivity::class.java).apply {
                    putExtra("email", email.value)
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun PasswordInput(password: MutableState<String>) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Пароль") },
        placeholder = { Text(text = "Введите пароль") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
fun EnterButton(email: MutableState<String>, password: MutableState<String>) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                handleLogin(email.value, password.value, context)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Войти")
    }
}
@Composable
fun RegistrationButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Регистрация")
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
fun handleLogin(login: String, password: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        var message: String = ""
        try {
            val response = ScalarsApiManager.userApiService.login(login = login, password = password)
            message = response
            if (message == "Аутентификация успешна") {
                withContext(Dispatchers.Main) {
                    AuthManager.login(login)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            message = "Ошибка"
        } finally {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}