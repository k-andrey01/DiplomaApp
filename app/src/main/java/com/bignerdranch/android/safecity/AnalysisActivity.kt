package com.bignerdranch.android.safecity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.ui.theme.Aqua
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import com.bignerdranch.android.safecity.ui.theme.SkyBlue
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import me.bytebeats.views.charts.simpleChartAnimation

class AnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeCityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TopBar("Анализ")
                        DropdownListWithDiagram()
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                    NavigationBar(2, context = LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun DropdownListWithDiagram() {
    DropdownList()
    Spacer(modifier = Modifier.height(50.dp))
    Chart()
}

@Composable
fun DropdownList() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .border(width = 1.dp, color = Color.Black)
        .clickable(onClick = { expanded = true })
    ) {
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(text = options[selectedOption])
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Blue,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    selectedOption = index
                    expanded = false
                }) {
                    Text(text = option, color = Blue)
                }
            }
        }
    }
}

@Composable
fun Chart() {
    PieChart(
        pieChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(30f, Blue),
                PieChartData.Slice(50f, Aqua),
                PieChartData.Slice(20f, SkyBlue)
            )
        ),
        // Optional properties.
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer()
    )
}