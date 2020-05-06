package com.tans.gameoflife.game

typealias Rule = (LifeModel) -> Unit

// B3/S23
object DefaultRule : CommonRule(born = intArrayOf(3), survive = intArrayOf(2, 3))

open class CommonRule(val born: IntArray, val survive: IntArray) : Rule {

    override fun invoke(life: LifeModel) {
        synchronized(life) {
            repeat(life.mapSize.height * life.mapSize.width) { meIndex ->
                val aliveCount = life.getAroundAliveCount(meIndex)
                val meIsAlive = life.life[meIndex].isAlive
                when {
                    meIsAlive && survive.contains(aliveCount) -> life.aliveLifeCache[meIndex] = true
                    !meIsAlive && born.contains(aliveCount) -> life.aliveLifeCache[meIndex] = true
                    else -> life.aliveLifeCache[meIndex] = false
                }
            }
            life.syncLifeWithCache()
        }
    }

}