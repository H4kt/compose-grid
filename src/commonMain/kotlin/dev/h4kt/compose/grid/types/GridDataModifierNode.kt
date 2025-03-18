package dev.h4kt.compose.grid.types

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.unit.Density

internal class GridDataModifierNode(
    var rowSpan: Int?,
    var columnSpan: Int?,
) : Modifier.Node(), ParentDataModifierNode {
    override fun Density.modifyParentData(parentData: Any?) = this@GridDataModifierNode
}
