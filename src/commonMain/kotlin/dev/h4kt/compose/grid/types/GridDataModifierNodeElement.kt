package dev.h4kt.compose.grid.types

import androidx.compose.ui.node.ModifierNodeElement

internal data class GridDataModifierNodeElement(
    val rowSpan: Int? = null,
    val columnSpan: Int? = null
) : ModifierNodeElement<GridDataModifierNode>() {

    override fun create() = GridDataModifierNode(
        rowSpan = rowSpan,
        columnSpan = columnSpan
    )

    override fun update(node: GridDataModifierNode) {
        node.rowSpan = rowSpan
        node.columnSpan = columnSpan
    }

}
