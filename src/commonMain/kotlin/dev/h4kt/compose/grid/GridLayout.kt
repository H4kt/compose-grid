package dev.h4kt.compose.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastRoundToInt
import dev.h4kt.compose.grid.types.GridDataModifierNode
import dev.h4kt.compose.grid.types.GridScope
import dev.h4kt.compose.grid.types.GridScopeInstance
import dev.h4kt.compose.grid.types.GridSize
import kotlin.math.absoluteValue
import kotlin.math.min

@Composable
fun Grid(
    modifier: Modifier = Modifier,
    columns: List<GridSize>,
    rows: List<GridSize>,
    verticalSpacing: Dp,
    horizontalSpacing: Dp,
    content: @Composable GridScope.() -> Unit
) = Layout(
    modifier = modifier,
    content = {
        content(GridScopeInstance)
    }
) { measurables, constraints ->

    val horizontalSpacingPx = horizontalSpacing.roundToPx()
    val verticalSpacingPx = verticalSpacing.roundToPx()

    val widthWithoutSpacing = constraints.maxWidth - horizontalSpacingPx * columns.lastIndex
    val columnWidths = columns.calculateSizes(this, widthWithoutSpacing)

    val heightWithoutSpacing = constraints.maxHeight - verticalSpacingPx * rows.lastIndex
    val rowHeights = rows.calculateSizes(this, heightWithoutSpacing)

    val placeables = measurables.measure(
        columnWidths = columnWidths,
        rowHeights = rowHeights,
        verticalSpacing = verticalSpacingPx,
        horizontalSpacing = horizontalSpacingPx
    )

    return@Layout layout(constraints.maxWidth, constraints.maxHeight) {
        placeables.forEach { (placeable, offset) ->
            placeable.place(offset)
        }
    }
}

private fun List<Measurable>.measure(
    columnWidths: Array<Int>,
    rowHeights: Array<Int>,
    verticalSpacing: Int,
    horizontalSpacing: Int
): List<Pair<Placeable, IntOffset>> {

    val cells = Array(columnWidths.size * rowHeights.size) { false }
    var cellIndex = 0

    return mapNotNull { measurable ->

        if (cellIndex !in cells.indices) {
            return@mapNotNull null
        }

        while (cells[cellIndex]) {
            ++cellIndex

            if (cellIndex !in cells.indices) {
                return@mapNotNull null
            }
        }

        val gridData = measurable.parentData as? GridDataModifierNode
        val columnSpan = gridData?.columnSpan?.absoluteValue ?: 1
        val rowSpan = gridData?.rowSpan?.absoluteValue ?: 1

        val columnIndex = cellIndex % columnWidths.size
        val rowIndex = cellIndex / columnWidths.size

        for (x in 0..<columnSpan) {
            for (y in 0..<rowSpan) {
                val index = (rowIndex + y) * columnWidths.size + columnIndex + x
                cells[min(cells.lastIndex, index)] = true
            }
        }

        val width = columnWidths.drop(columnIndex).take(columnSpan).sum() + horizontalSpacing * columnSpan.dec()
        val height = rowHeights.drop(rowIndex).take(rowSpan).sum() + verticalSpacing * rowSpan.dec()

        val offsetX = columnWidths.take(columnIndex).sum() + horizontalSpacing * columnIndex
        val offsetY = rowHeights.take(rowIndex).sum() + verticalSpacing * rowIndex

        val placeable = measurable.measure(
            Constraints.fixed(width, height)
        )

        return@mapNotNull placeable to IntOffset(offsetX, offsetY)
    }
}

private fun List<GridSize>.calculateSizes(
    density: Density,
    total: Int
): Array<Int> {

    var sizeLeft = total
    val sizes = Array(size) { 0 }

    forEachIndexed { index, size ->

        if (size !is GridSize.Fixed) {
            return@forEachIndexed
        }

        val valuePx = with(density) {
            size.value.roundToPx()
        }

        val finalValue = min(sizeLeft, valuePx)

        sizes[index] = finalValue
        sizeLeft -= finalValue

    }

    val totalFractions = sumOf {
        when (it) {
            GridSize.Auto -> 1
            is GridSize.Fraction -> it.value
            else -> 0
        }
    }

    val fractionSize = sizeLeft / totalFractions

    forEachIndexed { index, size ->

        val fraction = when (size) {
            GridSize.Auto -> 1
            is GridSize.Fraction -> size.value
            else -> return@forEachIndexed
        }

        val value = min(
            sizeLeft,
            (fraction.toFloat() * fractionSize).fastRoundToInt()
        )

        sizes[index] += value
        sizeLeft -= value

    }

    return sizes
}
