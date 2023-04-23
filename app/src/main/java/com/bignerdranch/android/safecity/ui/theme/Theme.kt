package com.bignerdranch.android.safecity.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val ColorPalette = lightColors(
    primary = Blue,
    primaryVariant = ArgentinianBlue,
    secondary = SkyBlue
)

//private val LightColorPalette = lightColors(
//    primary = Blue,
//    primaryVariant = ArgentinianBlue,
//    secondary = SkyBlue
//)

@Composable
fun SafeCityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = ColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}