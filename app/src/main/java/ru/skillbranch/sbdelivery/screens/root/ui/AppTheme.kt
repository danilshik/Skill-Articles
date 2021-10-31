package ru.skillbranch.sbdelivery.screens.root.ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.R

fun MyTypography(onPrimary:Color, onBackground:Color) = Typography(
    h5 = TextStyle(
        color = onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        color = onPrimary,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    subtitle1 = TextStyle(
        color = onPrimary,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        color = onPrimary,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.ExtraLight,
        fontSize = 14.sp,
        color = onBackground
    )
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Colors(
            primary = colorResource(R.color.colorPrimary),
            primaryVariant = colorResource(R.color.colorPrimaryVariant),
            secondary = colorResource(R.color.colorSecondary),
            secondaryVariant = colorResource(R.color.colorSecondaryVariant),
            background = colorResource(R.color.colorBackground),
            surface = colorResource(R.color.colorSurface),
            error = colorResource(R.color.colorError),
            onPrimary = colorResource(R.color.colorOnPrimary),
            onSecondary = colorResource(R.color.colorOnSecondary),
            onBackground = colorResource(R.color.colorOnBackground),
            onSurface = colorResource(R.color.colorOnSurface),
            onError = colorResource(R.color.colorOnError),
            isLight = true
        ),
        typography = MyTypography(colorResource(R.color.colorOnPrimary),colorResource(R.color.colorOnBackground) ),
        content = content
    )
}

