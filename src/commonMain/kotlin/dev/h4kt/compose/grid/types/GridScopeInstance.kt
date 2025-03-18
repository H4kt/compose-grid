package dev.h4kt.compose.grid.types

import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

internal object GridScopeInstance : GridScope {

    @Stable
    override fun Modifier.rowSpan(
        value: Int
    ) = then(GridDataModifierNodeElement(rowSpan = value))

    @Stable
    override fun Modifier.columnSpan(
        value: Int
    ) = then(GridDataModifierNodeElement(columnSpan = value))

}
