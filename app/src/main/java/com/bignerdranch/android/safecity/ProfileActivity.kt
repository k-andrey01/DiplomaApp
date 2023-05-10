package com.bignerdranch.android.safecity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Профиль", false, onBackPressed = { onBackPressed() })
                        InfoWithButtons(
                            login = "danusup@gmail.com",
                            firstName = "Danil",
                            lastName = "Usupov",
                            gender = "Male",
                            dateOfBirth = "16/12/2001",
                            LocalContext.current
                        )
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                    NavigationBar(4, context = LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun InfoWithButtons(
    login: String,
    firstName: String,
    lastName: String,
    gender: String,
    dateOfBirth: String,
    context: Context
) {
    UserInfoRow(
        login = login,
        firstName = firstName,
        lastName = lastName,
        gender = gender,
        dateOfBirth = dateOfBirth
    )
    Spacer(modifier = Modifier.height(65.dp))
    Buttons(context)
}

@Composable
fun Buttons(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(alignment = CenterHorizontally)
        ) {
            Text("Изменить пароль")
        }
        Button(
            onClick = {
                val intent = Intent(context, SendingMailActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(alignment = CenterHorizontally)
        ) {
            Text("Вопросы и предложения")
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(alignment = CenterHorizontally)
        ) {
            Text("Выйти")
        }
    }
}


@Composable
fun UserInfoRow(
    login: String,
    firstName: String,
    lastName: String,
    gender: String,
    dateOfBirth: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        UserInfoItem("Пользователь", login)
        UserInfoItem("Имя", firstName)
        UserInfoItem("Фамилия", lastName)
        UserInfoItem("Пол", gender)
        UserInfoItem("Дата рождения", dateOfBirth)
    }
}

@Composable
fun UserInfoItem(name: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            name,
            color = Blue,
            modifier = Modifier.weight(5f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            value,
            color = SkyBlue,
            modifier = Modifier.weight(7f)
        )
    }
}