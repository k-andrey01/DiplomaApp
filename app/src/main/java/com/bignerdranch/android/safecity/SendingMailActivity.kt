package com.bignerdranch.android.safecity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.HelperClass.MailSender
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme

class SendingMailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Вопросы и предложения", true, onBackPressed = { onBackPressed() })
                        InputWithButtons(LocalContext.current, onBackPressed = { onBackPressed() }, resources.getString(R.string.mail_from),
                            resources.getString(R.string.mail_to), resources.getString(R.string.password), resources.getString(R.string.theme_ask))
                    }
                }
            }
        }
    }
}

@Composable
fun InputWithButtons(context: Context, onBackPressed: () -> Unit, mailFrom: String, mailTo: String, password: String, theme: String) {
    val text = rememberSaveable { mutableStateOf("") }
    InputArea(text)
    ButtonOnMailSending(context, onBackPressed = { onBackPressed() }, mailFrom, mailTo, password, theme, text.value)
}

@Composable
fun InputArea(text: MutableState<String>) {
    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.White
    )
    OutlinedTextField(
        value = text.value,
        onValueChange = { text.value = it },
        label = { Text(text = "Ваше обращение") },
        placeholder = { Text(text = "Введите текст") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 265.dp)
            .padding(10.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)),
        colors = textFieldColors
    )
}


@Composable
fun ButtonOnMailSending(context: Context, onBackPressed: () -> Unit, mailFrom: String, mailTo: String, password: String, theme: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                val mailSender =
                    MailSender()
                mailSender.sendMail(message, mailFrom, mailTo, password, theme)
                Toast.makeText(
                    context,
                    "Обращение успешно отправлено",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text("Отправить")
        }
        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text("Назад")
        }
    }
}
