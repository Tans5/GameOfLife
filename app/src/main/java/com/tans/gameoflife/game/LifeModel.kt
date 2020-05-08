package com.tans.gameoflife.game

data class Size(val width: Int,
                val height: Int) {
    override fun toString(): String {
        return "$width * $height"
    }
}


data class Cell(
    val x: Int,
    val y: Int,
    var isAlive: Boolean = false
)

 data class LifeModel(
     val mapSize: Size,
     val life: MutableList<Cell>,
     val aliveLifeCache: MutableList<Boolean> = MutableList(mapSize.width * mapSize.height) { false }
 ) {
     fun syncLifeWithCache() {
         var index: Int = 0
         while (index < life.size) {
             life[index].isAlive = aliveLifeCache[index]
             index ++
         }
     }
 }

fun Size.emptyLifeModel(): LifeModel = LifeModel(
    mapSize = this,
    life = MutableList(width * height) { index ->
        val x = index % width
        val y = index / width
        Cell(x, y, false)
    },
    aliveLifeCache = MutableList(width * height) {  false }
)