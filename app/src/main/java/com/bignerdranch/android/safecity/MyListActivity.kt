package com.bignerdranch.android.safecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.Red
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue

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
//    suspend fun fetchCrimeList() {
//
//    }
//    val crimes = GsonApiManager.crimeApiService.getAllCrimesForMap()
    val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        items.forEachIndexed { index, item ->
            ListItem(item, onDeleteClick = { /*TODO*/ })
            Divider()
        }
    }
}

@Composable
fun ListItem(item: String, onDeleteClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item, Modifier.weight(1f), color = Blue)
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Red)
        }
    }
}
