package com.tans.gameoflife.game

typealias Rule = (LifeModel) -> LifeModel

object DefaultRule : Rule {

    override fun invoke(old: LifeModel): LifeModel {
        val mapSize = old.mapSize
        val oldLife = old.life

        return LifeModel(
            mapSize = mapSize,
            life = sequence<Int> {
                repeat(mapSize.height * mapSize.width) { meIndex ->
                    val aroundAliveCount = mapSize.getRoundAliveCount(meIndex, oldLife)
                    val meIsAlive: Boolean = oldLife.contains(meIndex)
                    when {
                        meIsAlive && (aroundAliveCount == 2 || aroundAliveCount == 3) -> yield(meIndex)
                        !meIsAlive && aroundAliveCount == 3 -> yield(meIndex)
                    }

                }
            }.toList().toIntArray()
        )
    }

}