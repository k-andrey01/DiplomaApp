package com.bignerdranch.android.safecity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.bignerdranch.android.safecity.HelperClass.CodeGenerator
import com.bignerdranch.android.safecity.HelperClass.MailSender
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditingPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mail = ""
        if (AuthManager.isLoggedIn()){
            mail = AuthManager.getUsername()
        }

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
                            resources.getString(R.string.password),
                            resources.getString(R.string.theme_change_pass),
                            mail
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
    password: String,
    theme: String,
    email: String
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val email = remember { mutableStateOf(email) }
        val confirmCode = remember { mutableStateOf("") }
        val code = remember { mutableStateOf("") }
        val newPassword = remember { mutableStateOf("") }
        val isCodeSent = remember { mutableStateOf(false) }

        EmailInput(email = email)
        SendCodeButton(
            email = email,
            onCodeSent = { isCodeSent.value = true },
            mailFrom,
            password,
            theme,
            confirmCode
        )

        if (isCodeSent.value) {
            Spacer(modifier = Modifier.height(10.dp))
            CodeInput(code = code)
            NewPasswordInput(newPassword = newPassword)
            ChangePasswordButton(
                email = email,
                code = code,
                confirmCode = confirmCode,
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
    password: String,
    theme: String,
    confirmCode: MutableState<String>
) {
    val context = LocalContext.current
    val codeGenerator = CodeGenerator()
    val mailSender = MailSender()

    Button(
        onClick = {
            confirmCode.value = codeGenerator.generateRandomNumber()
            mailSender.sendMail(confirmCode.value, mailFrom, email.value, password, theme)
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
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = email.value,
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "emailIcon") },
        onValueChange = { email.value = it },
        label = { Text("Email") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )
}

@Composable
fun CodeInput(code: MutableState<String>) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = code.value,
        onValueChange = { code.value = it },
        label = { Text("Код") },
        placeholder = { Text(text = "Введите код, отправленный на почту") },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(75.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
    )
}

@Composable
fun NewPasswordInput(newPassword: MutableState<String>) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
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
fun ChangePasswordButton(
    email: MutableState<String>,
    code: MutableState<String>,
    confirmCode: MutableState<String>,
    newPassword: MutableState<String>
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (code.value == confirmCode.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    var message = ""
                    try {
                        val response = ScalarsApiManager.userApiService.updatePassword(
                            login = email.value,
                            password = newPassword.value
                        )
                        message = response
                        if (message == "Пароль обновлён") {
                            CoroutineScope(Dispatchers.Main).launch {
                                (context as? Activity)?.finish()
                            }
                        }
                    } catch (e: Exception) {
                        message = "Непредвиденная ошибка"
                        Log.d("D","Error ${e.message}")
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
            } else {
                Toast.makeText(
                    context,
                    "Введен неверный код подтверждения",
                    Toast.LENGTH_SHORT
                ).show()
            }
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