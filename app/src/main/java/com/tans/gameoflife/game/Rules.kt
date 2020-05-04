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

typealias Rule2 = (LifeModel2) -> Unit

object DefaultRule2 : Rule2 {

    override fun invoke(life: LifeModel2) {
        synchronized(life) {
            repeat(life.mapSize.height * life.mapSize.width) { meIndex ->
                val aliveCount = life.getAroundAliveCount(meIndex)
                val meIsAlive = life.life[meIndex].isAlive
                when {
                    meIsAlive && (aliveCount == 2 || aliveCount == 3) -> life.aliveLifeCache[meIndex] = true
                    !meIsAlive && aliveCount == 3 -> life.aliveLifeCache[meIndex] = true
                    else -> life.aliveLifeCache[meIndex] = false
                }
            }
            life.syncLifeWithCache()
        }
    }

}