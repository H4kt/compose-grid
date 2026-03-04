package dev.h4kt.compose.grid.types

import androidx.compose.ui.unit.Dp
import kotlin.jvm.JvmInline

sealed interface GridSize {
    @JvmInline
    value class Fraction(
        val value: Int
    ) : GridSize

    @JvmInline
    value class Fixed(
        val value: Dp
    ) : GridSize
}
