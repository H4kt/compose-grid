package dev.h4kt.compose.grid.dsl

import androidx.compose.ui.unit.Dp
import dev.h4kt.compose.grid.types.GridSize
import kotlin.jvm.JvmInline

@JvmInline
value class SizeBuilder(
    internal val value: MutableList<GridSize> = mutableListOf()
) {

    @GridDslMarker
    fun auto() {
        value += GridSize.Auto
    }

    @GridDslMarker
    fun fraction(value: Int) {
        this.value += GridSize.Fraction(value)
    }

    @GridDslMarker
    fun fixed(value: Dp) {
        this.value += GridSize.Fixed(value)
    }

    internal fun build() = value

}

fun sizes(block: SizeBuilder.() -> Unit): List<GridSize> {
    return SizeBuilder().apply(block).build()
}
