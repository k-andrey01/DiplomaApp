package com.bignerdranch.android.safecity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

class EditingPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var email = ""
        if (!intent.getStringExtra("email").isNullOrEmpty()) {
            email = intent.getStringExtra("email").toString()
        }
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Смена пароля", true, onBackPressed = { onBackPressed() })
                        PasswordResetScreen(
                            onBackPressed = { onBackPressed() },
                            resources.getString(R.string.mail_from),
                            resources.getString(R.string.mail_to),
                            resources.getString(R.string.password),
                            resources.getString(R.string.theme_change_pass),
                            email
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordResetScreen(
    onBackPressed: () -> Unit,
    mailFrom: String,
    mailTo: String,
    password: String,
    theme: String,
    email: String
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf(email) }
        val code = remember { mutableStateOf("") }
        val newPassword = remember { mutableStateOf("") }
        val isCodeSent = remember { mutableStateOf(false) }

        EmailInput(email = email)
        SendCodeButton(
            email = email,
            onCodeSent = { isCodeSent.value = true },
            mailFrom,
            mailTo,
            password,
            theme
        )

        if (isCodeSent.value) {
            Spacer(modifier = Modifier.height(10.dp))
            CodeInput(code = code)
            NewPasswordInput(newPassword = newPassword)
            ChangePasswordButton(
                code = code,
                newPassword = newPassword
            )
        }

        BackButton(onBackPressed)
    }
}

@Composable
fun SendCodeButton(
    email: MutableState<String>,
    onCodeSent: () -> Unit,
    mailFrom: String,
    mailTo: String,
    password: String,
    theme: String
) {
    val context = LocalContext.current
    Button(
        onClick = {
            // Отправить код на указанный email
            // Здесь можно добавить логику для отправки кода на email
            val mailSender = MailSender()
            mailSender.sendMail("тут код", mailFrom, mailTo, password, theme)
            Toast.makeText(context, "Код отправлен на указанный email", Toast.LENGTH_SHORT).show()
            onCodeSent()
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Отправить код")
    }
}


@Composable
fun EmailInput(email: MutableState<String>) {
    OutlinedTextField(
        value = email.value,
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "emailIcon") },
        onValueChange = { email.value = it },
        label = { Text("Email") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true
    )
}

@Composable
fun CodeInput(code: MutableState<String>) {
    OutlinedTextField(
        value = code.value,
        onValueChange = { code.value = it },
        label = { Text("Код") },
        placeholder = { Text(text = "Введите код, отправленный на почту") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true
    )
}

@Composable
fun NewPasswordInput(newPassword: MutableState<String>) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = newPassword.value,
        onValueChange = { newPassword.value = it },
        label = { Text("Новый пароль") },
        placeholder = { Text(text = "Введите новый пароль") },
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
fun ChangePasswordButton(code: MutableState<String>, newPassword: MutableState<String>) {
    val context = LocalContext.current
    Button(
        onClick = {
            // Сменить пароль с использованием полученного кода и нового пароля
            // Здесь можно добавить логику для смены пароля
            Toast.makeText(context, "Пароль успешно изменён", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Сменить пароль")
    }
}

@Composable
fun BackButton(onBackPressed: () -> Unit) {
    Button(
        onClick = onBackPressed,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text("Назад")
    }
}