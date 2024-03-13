package com.msukno.gameapprawg.ui.theme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = CutCornerShape(18.dp),
    large = CutCornerShape(topEnd = 24.dp, bottomStart = 24.dp)
)