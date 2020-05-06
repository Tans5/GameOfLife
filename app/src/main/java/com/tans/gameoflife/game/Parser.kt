package com.tans.gameoflife.game

typealias ParseResult = Pair<LifeModel, Rule>

typealias Parser = (code: String) -> ParseResult



object GollyParseError : Throwable("Parse code occur error!")
/**
 * Code demo:
 * x = 11, y = 11, rule = B3/S23
 * 2b4o$bo4bob2o$bo4bo3bo$4b2o4bo$b2o4bo2bo$o2bo3bo2bo$o2bo4b2o$o4b2o$o3b
 * o4bo$b2obo4bo$5b4o!
 */
object GollyCodeParser : Parser {

    override fun invoke(code: String): ParseResult {
        try {
            val result = code.split('\n')
            val sizeRuleString = result[0]
            val lifeString = result[1]
            val sizeRuleKeyValue = sizeRuleString.trim().split(",")
                .map {
                    val keyValue = it.split("=")
                    keyValue[0].trim() to keyValue[1].trim()
                }
                .toMap()
            val size = Size(width = sizeRuleKeyValue["x"]!!.toInt(), height = sizeRuleKeyValue["y"]!!.toInt())
            val ruleKeyValue: Map<String, IntArray> = sizeRuleKeyValue["rule"]!!.split("/").mapNotNull {
                when {
                    it.startsWith("B") -> {
                        val born = it.replace("B", "")
                            .map { c -> c.toString().toInt() }
                            .toIntArray()
                        "born" to born
                    }

                    it.startsWith("S") -> {
                        val survive = it.replace("S", "")
                            .map { c -> c.toString().toInt() }
                            .toIntArray()
                        "survive" to survive
                    }

                    else -> null
                }
            }.toMap()
            val rule = CommonRule(born = ruleKeyValue["born"]!!, survive = ruleKeyValue["survive"]!!)

        } catch (e: Throwable) {
            throw GollyParseError
        }

        // TODO: Replace it.
        return Size(50, 50).emptyLifeModel() to DefaultRule
    }

}