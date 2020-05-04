package com.tans.gameoflife.game

data class LifeModel(val life: IntArray,
                     val mapSize: Size) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LifeModel

        if (!life.contentEquals(other.life)) return false
        if (mapSize != other.mapSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = life.contentHashCode()
        result = 31 * result + mapSize.hashCode()
        return result
    }
}

data class Size(val width: Int,
                val height: Int) {
    override fun toString(): String {
        return "$width * $height"
    }
}


// x, y, isAlive
// typealias Cell = Triple<Short, Short, Boolean>\
data class Cell(
    val x: Int,
    val y: Int,
    var isAlive: Boolean = false
)

 data class LifeModel2(
     val mapSize: Size,
     val life: MutableList<Cell>
 )

fun Size.emptyLifeModel(): LifeModel2 = LifeModel2(
    mapSize = this,
    life = MutableList(width * height) { index ->
        val x = index % width
        val y = index / width
        Cell(x, y, false)
    }
)