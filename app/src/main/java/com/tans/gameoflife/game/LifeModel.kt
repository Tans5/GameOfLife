package com.tans.gameoflife.game

data class LifeModel(val life: ByteArray,
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