package com.bignerdranch.android.safecity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.DataClasses.Crime
import com.bignerdranch.android.safecity.HelperClass.AuthManager
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.Managers.ScalarsApiManager
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.Red
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Мои отметки", false, onBackPressed = { onBackPressed() })
                        ScrollableList()
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                    NavigationBar(3, context = LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun ScrollableList() {
    val crimes = remember { mutableStateListOf<Crime>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchCrimeList(crimes)
    }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 56.dp)) {
        crimes.forEachIndexed { index, crime ->
            ListItem(
                crime,
                onDeleteClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = ScalarsApiManager.crimeApiService.deleteCrime(crime.id)
                        if (response.equals("Удалено")) {
                            crimes.remove(crime)
                        }else{
                            Toast.makeText(
                                context,
                                response,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
            Divider()
        }
    }
}

suspend fun fetchCrimeList(crimes: MutableList<Crime>) {
    val crimeList = GsonApiManager.crimeApiService.getAllCrimesOfWitness(AuthManager.getUsername())
    crimes.clear()
    crimes.addAll(crimeList)
}

@Composable
fun ListItem(item: Crime, onDeleteClick: () -> Unit) {
    val crimeExpanded = remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { crimeExpanded.value = !crimeExpanded.value },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.type + " - " + item.comment, Modifier.weight(1f), color = Blue)
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Red)
        }
    }

    if (crimeExpanded.value) {
        val info = "Дата: ${item?.timeCrime?.replace('T', ' ')}\n" +
                "Адрес: ${item.city} ${item.street} ${item.house}\n" +
                "Вид: ${item.kind}\n"
        var victims = "Пострадавшие:"
        item.victims.forEach { victim ->
            victims+="\n   Пол: ${victim.gender}, Возраст: ${victim.age}"
        }
        Text(info+victims, Modifier.padding(horizontal = 16.dp))
    }
}
