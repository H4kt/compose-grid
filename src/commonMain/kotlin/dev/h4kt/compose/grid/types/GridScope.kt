package dev.h4kt.compose.grid.types

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

@Immutable
@LayoutScopeMarker
interface GridScope {

    @Stable
    fun Modifier.rowSpan(value: Int): Modifier

    @Stable
    fun Modifier.columnSpan(value: Int): Modifier

}
