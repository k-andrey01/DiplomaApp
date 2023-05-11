package com.bignerdranch.android.safecity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme

class LoginActivity : ComponentActivity() {
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

        EnterButton()
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

    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Пароль") },
        placeholder = { Text(text = "Введите пароль") },
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
fun EnterButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
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
    Button(
        onClick = {/*TODO*/ },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Регистрация")
    }
}