package com.tans.gameoflife.game

typealias Rule = (LifeModel) -> LifeModel

object DefaultRule : Rule {

    override fun invoke(old: LifeModel): LifeModel {
        val mapSize = old.mapSize
        val oldLife = old.life

        return LifeModel(
            mapSize = mapSize,
            life = BooleanArray(mapSize.width * mapSize.height) { i ->
                val roundAliveCount = mapSize.getRoundIndex(i).map { oldLife[it] }
                    .count { it }
                val lastIsAlive = oldLife[i]
                when  {
                    lastIsAlive && roundAliveCount < 2 -> false
                    lastIsAlive && (roundAliveCount == 2 || roundAliveCount == 3) -> true
                    lastIsAlive && roundAliveCount > 3 -> false
                    !lastIsAlive && roundAliveCount == 3 -> true
                    else -> false
                }
            }
        )
    }

}