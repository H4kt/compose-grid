package dev.h4kt.compose.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
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

    val cells = measurables.measure(
        columnWidths = columnWidths,
        rowHeights = rowHeights,
        verticalSpacing = verticalSpacingPx,
        horizontalSpacing = horizontalSpacingPx
    )

    val width = cells
        .toList()
        .chunked(columnWidths.size)
        .maxOf { row ->
            val uniqueItems = row.toSet().filterNotNull()
            val itemsWidth = uniqueItems.sumOf { it.width }
            val spacing = horizontalSpacingPx * uniqueItems.size.dec().coerceAtLeast(0)

            return@maxOf itemsWidth + spacing
        }

    val height = cells
        .mapIndexed { index, placeable ->
            val columnIndex = index % columnWidths.size
            return@mapIndexed columnIndex to placeable
        }
        .groupBy { (columnIndex) -> columnIndex }
        .values
        .map {
            it.map { (_, placeable) -> placeable }
        }
        .maxOf { column ->
            val uniqueItems = column.toSet().filterNotNull()
            val itemsHeight = uniqueItems.sumOf { it.height }
            val spacing = verticalSpacingPx * uniqueItems.size.dec().coerceAtLeast(0)

            return@maxOf itemsHeight + spacing
        }

    return@Layout layout(width, height) {
        val placed = mutableSetOf<Placeable>()

        cells.forEachIndexed { index, placeable ->
            if (placeable == null || placeable in placed) {
                return@forEachIndexed
            }

            val columnIndex = index % columnWidths.size
            val rowIndex = index / columnWidths.size

            val x = columnWidths.take(columnIndex).sum() + horizontalSpacingPx * columnIndex
            val y = rowHeights.take(rowIndex).sum() + verticalSpacingPx * rowIndex

            placeable.place(x, y)
            placed += placeable
        }
    }
}

private fun List<Measurable>.measure(
    columnWidths: Array<Int>,
    rowHeights: Array<Int>,
    verticalSpacing: Int,
    horizontalSpacing: Int
): Array<Placeable?> {
    val cells = Array<Placeable?>(columnWidths.size * rowHeights.size) { null }
    var cellIndex = 0

    forEach { measurable ->
        if (cellIndex !in cells.indices) {
            return@forEach
        }

        while (cells[cellIndex] != null) {
            ++cellIndex

            if (cellIndex !in cells.indices) {
                return@forEach
            }
        }

        val gridData = measurable.parentData as? GridDataModifierNode
        val columnSpan = gridData?.columnSpan?.absoluteValue ?: 1
        val rowSpan = gridData?.rowSpan?.absoluteValue ?: 1

        val columnIndex = cellIndex % columnWidths.size
        val rowIndex = cellIndex / columnWidths.size

        val width = columnWidths.drop(columnIndex).take(columnSpan).sum() + horizontalSpacing * columnSpan.dec()
        val height = rowHeights.drop(rowIndex).take(rowSpan).sum() + verticalSpacing * rowSpan.dec()

        val constraints = Constraints.fixed(width, height)
        val placeable = measurable.measure(constraints)

        for (x in 0..<columnSpan) {
            for (y in 0..<rowSpan) {
                val index = (rowIndex + y) * columnWidths.size + columnIndex + x
                cells[min(cells.lastIndex, index)] = placeable
            }
        }
    }

    return cells
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

    val totalFractions = asSequence()
        .filterIsInstance<GridSize.Fraction>()
        .sumOf { it.value }

    val fractionSize = if (totalFractions > 0) sizeLeft / totalFractions else 0

    forEachIndexed { index, size ->
        if (size !is GridSize.Fraction) {
            return@forEachIndexed
        }

        val value = min(
            sizeLeft,
            (size.value.toFloat() * fractionSize).fastRoundToInt()
        )

        sizes[index] += value
        sizeLeft -= value
    }

    return sizes
}
