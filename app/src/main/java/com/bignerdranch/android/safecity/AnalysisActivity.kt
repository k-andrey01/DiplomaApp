package com.bignerdranch.android.safecity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.safecity.Managers.GsonApiManager
import com.bignerdranch.android.safecity.ui.theme.Blue
import com.bignerdranch.android.safecity.ui.theme.SafeCityTheme
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import kotlin.random.Random

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
                        TopBar("Анализ", false, onBackPressed = { onBackPressed() })
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
    var selectedOption by remember { mutableStateOf(0) }

    DropdownList(selectedOption, onOptionSelected = { newOption ->
        selectedOption = newOption
    })
    Spacer(modifier = Modifier.height(50.dp))
    Chart(selectedOption = selectedOption)
}

@Composable
fun DropdownList(selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    val options = listOf("Возраст пострадавших", "Пол пострадавших", "Время суток", "Вид опасности")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .border(width = 1.dp, color = Color.Black)
        .clickable(onClick = { expanded = !expanded })
    ) {
        TextButton(
            onClick = { expanded = !expanded }
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
                    onOptionSelected(index)
                    expanded = false
                }) {
                    Text(text = option, color = Blue)
                }
            }
        }
    }
}


@Composable
fun Chart(selectedOption: Int) {
    when (selectedOption){
        0 -> { PieChartWithCountByAgeGroup() }
        1 -> { PieChartWithCountByGender() }
        2 -> { PieChartWithCountByTime() }
        3 -> { PieChartWithCountByKind() }
    }
}

@Composable
fun Diagram(slices:  List<PieChartData.Slice>, slicesDesc: MutableList<Description>){
    PieChart(
        pieChartData = PieChartData(slices = slices),
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer()
    )

    Spacer(modifier = Modifier.height(25.dp))

    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            slicesDesc.forEachIndexed { index, slice ->
                val color = slice.color.toArgb()
                val label = "${slice.label} - ${slice.count}"

                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(color))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun PieChartWithCountByTime(){
    val data = remember { mutableStateOf(emptyMap<String, Integer>()) }

    suspend fun fetchAgeList(data: MutableState<Map<String, Integer>>){
        val countByAgeGroup = GsonApiManager.crimeApiService.getCountByTime()
        data.value = countByAgeGroup
    }

    LaunchedEffect(Unit){
        fetchAgeList(data)
    }

    var slicesDesc: MutableList<Description> = mutableListOf()
    var slices = data.value.map { (age, count) ->
        val color = generateRandomColor()
        slicesDesc.add(Description(color, age, count))

        PieChartData.Slice(
            count.toFloat(),
            color
        )
    }

    Diagram(slices = slices, slicesDesc = slicesDesc)
}

@Composable
fun PieChartWithCountByAgeGroup(){
    val data = remember { mutableStateOf(emptyMap<String, Integer>()) }

    suspend fun fetchAgeList(data: MutableState<Map<String, Integer>>){
        val countByAgeGroup = GsonApiManager.victimApiService.getCountByAgeGroup()
        data.value = countByAgeGroup
    }

    LaunchedEffect(Unit){
        fetchAgeList(data)
    }

    var slicesDesc: MutableList<Description> = mutableListOf()
    var slices = data.value.map { (age, count) ->
        val color = generateRandomColor()
        slicesDesc.add(Description(color, age, count))

        PieChartData.Slice(
            count.toFloat(),
            color
        )
    }

    Diagram(slices = slices, slicesDesc = slicesDesc)
}

@Composable
fun PieChartWithCountByGender() {
    val data = remember { mutableStateOf(emptyMap<String, Integer>()) }

    suspend fun fetchGenderList(data: MutableState<Map<String, Integer>>){
        val countByGender = GsonApiManager.victimApiService.getCountByGender()
        data.value = countByGender
    }

    LaunchedEffect(Unit){
        fetchGenderList(data)
    }

    var slicesDesc: MutableList<Description> = mutableListOf()
    var slices = data.value.map { (gender, count) ->
        val color = generateRandomColor()
        slicesDesc.add(Description(color, gender, count))

        PieChartData.Slice(
            count.toFloat(),
            color
        )
    }

    Diagram(slices = slices, slicesDesc = slicesDesc)
}

@Composable
fun PieChartWithCountByKind() {
    val data = remember { mutableStateOf(emptyMap<String, Integer>()) }

    suspend fun fetchTypeList(data: MutableState<Map<String, Integer>>){
        val countByKind = GsonApiManager.crimeApiService.getCountByKind()
        data.value = countByKind
    }

    LaunchedEffect(Unit) {
        fetchTypeList(data)
    }

    var slicesDesc: MutableList<Description> = mutableListOf()
    val slices = data.value.map { (kind, count) ->
        val color = generateRandomColor()
        slicesDesc.add(Description(color, kind, count))

        PieChartData.Slice(
            count.toFloat(),
            color
        )
    }

    Diagram(slices = slices, slicesDesc = slicesDesc)
}

data class Description(
    val color: Color,
    val label: String,
    val count: Integer
)

fun generateRandomColor(): Color {
    val red = Random.nextInt(0, 256)
    val green = Random.nextInt(0, 256)
    val blue = Random.nextInt(0, 256) // Генерация случайного значения синего компонента
    return Color(red, green, blue)
}