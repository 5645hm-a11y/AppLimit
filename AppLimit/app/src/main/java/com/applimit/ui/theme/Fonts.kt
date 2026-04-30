package com.applimit.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.applimit.R

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal)
)

// Rubik mapped: Normal(400) → rubik_regular, Medium(500) → rubik_bold as closest available,
// SemiBold(600)/Bold(700) → rubik_bold.
// When rubik_medium.ttf is added to res/font, replace the W500 entry.
val Rubik = FontFamily(
    Font(R.font.rubik_regular, FontWeight.Normal),
    Font(R.font.rubik_regular, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.rubik_bold,    FontWeight.Medium),
    Font(R.font.rubik_bold,    FontWeight.SemiBold),
    Font(R.font.rubik_bold,    FontWeight.Bold),
)
