package com.bignerdranch.android.safecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue
import com.bignerdranch.android.safecity.ui.theme.White
import org.intellij.lang.annotations.JdkConstants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TopBar()
                    NavigationBar(1)
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Карта",
                        modifier = Modifier.fillMaxWidth(),
                        color = White,
                        textAlign = TextAlign.Center)
                },
                backgroundColor = SkyBlue,
            )
        }
    ) { innerPadding ->
    }
}

@Composable
fun NavigationBar(selected: Int){
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxHeight()) {
        BottomAppBar {
            IconButton(onClick = { }){
                Icon(Icons.Filled.LocationOn,
                    contentDescription = "Карта",
                    tint = if (selected == 1) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = { }){
                Icon(Icons.Filled.Info,
                    contentDescription = "Анализ",
                    tint = if (selected == 2) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = { }){
                Icon(Icons.Filled.List,
                    contentDescription = "Мои отметки",
                    tint = if (selected == 3) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
            }
            Spacer(Modifier.weight(1f, true))
            IconButton(onClick = { }){
                Icon(Icons.Filled.AccountBox,
                    contentDescription = "Личный кабинет",
                    tint = if (selected == 4) SkyBlue else LocalContentColor.current.copy(alpha = ContentAlpha.medium))
            }
        }
    }
}

@Preview
@Composable
fun NavigationPreview() {
    NavigationBar(1)
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar()
}