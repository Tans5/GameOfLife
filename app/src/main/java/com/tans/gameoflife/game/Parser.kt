package com.tans.gameoflife.game

import com.tans.gameoflife.gameViewSize
import com.tans.gameoflife.settings.SIZE_MAX
import kotlin.math.max

typealias ParseResult = Pair<LifeModel, Rule>

typealias Parser = (code: String) -> ParseResult



object GollyParseError : Throwable("Parse code occur error!")

val DEFAULT_GOLLY_CODE = listOf<String>(
    "x = 365, y = 1, rule = B3/S23\n365o!",
    "x = 86, y = 86, rule = B3/S23\n" +
            "41b2o\$41b2o4\$41bo\$40b3o\$39bo3bo\$38bob3obo\$39b5o14\$42b3o\$41bo3bo\$40bo5b\n" +
            "o\$40b2obob2o3\$43bo\$42bobo\$42bobo\$43bo2\$43b2o\$43b2o3\$77bo\$25b2o49bobo\$\n" +
            "24bobo32b2o15b2obo\$8bo14bo6b2o2b2o23bobo14b2ob2o3b2o\$7bobo13bo2bo2bo2b\n" +
            "ob2o18b2o6bo13b2obo4b2o\$2o4bob2o13bo6b2o18b2obo2bo2bo2bo13bobo\$2o3b2ob\n" +
            "2o14bobo23b2o2b2o6bo14bo\$6bob2o15b2o32bobo\$7bobo49b2o\$8bo3\$41b2o\$41b2o\n" +
            "2\$42bo\$41bobo\$41bobo\$42bo3\$39b2obob2o\$39bo5bo\$40bo3bo\$41b3o14\$42b5o\$\n" +
            "41bob3obo\$42bo3bo\$43b3o\$44bo4\$43b2o\$43b2o!\n"
)
/**
 * Code demo:
 * x = 11, y = 11, rule = B3/S23
 * 2b4o$bo4bob2o$bo4bo3bo$4b2o4bo$b2o4bo2bo$o2bo3bo2bo$o2bo4b2o$o4b2o$o3b
 * o4bo$b2obo4bo$5b4o!
 */
object GollyCodeParser : Parser {

    override fun invoke(code: String): ParseResult {
        try {
            val result = code.split('\n', limit = 2)
            val sizeRuleString = result[0]
            val lifeString = result[1].trim().replace("\n", "")
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
            val squareData = parseToSquareData(size.width, size.height, lifeString)

            val lifeModel = LifeModel(
                mapSize = size,
                life = MutableList(size.width * size.height) { index ->
                    val x = index % size.width
                    val y = index / size.width
                    val isAlive = squareData.isAlive(x, y)
                    Cell(x, y, isAlive)
                }
            )
            return lifeModel.parseResultToBestSize() to rule
        } catch (e: Throwable) {
            throw GollyParseError
        }
    }

    fun parseToLineDurationData(lifeString: String): Pair<LineDurationData, String> {
        val regex = "(([1-9]*)([ob])).*".toRegex()
        val groups = regex.find(lifeString)?.groupValues
        if (groups?.size != 4) {
            throw GollyParseError
        } else {
            val length = if (groups[2].isEmpty()) {
                1
            } else {
                groups[2].toInt()
            }
            val type = if (groups[3] == "o") {
                LineDurationType.Alive
            } else {
                LineDurationType.Dead
            }
            return LineDurationData(length = length, type = type) to lifeString.substring(groups[1].length until lifeString.length)
        }
    }

    fun parseToSquareData(width: Int, height: Int, lifeString: String): SquareData {
        val iterator = lifeString.split("$").iterator()
        var lines: List<LineData> = emptyList()
        val regex1 = "(([1-9]*[b|o])*)([1-9]+)($|!)".toRegex()
        val regex2 = "([1-9]+)($|!)".toRegex()
        while (iterator.hasNext()) {
            val lifeStringSub = iterator.next()
            val isFinished = if (!regex2.matches(lifeStringSub)) {
                val (line, isFinished) = parseToLineData(width, lifeStringSub)
                lines += line
                isFinished
            } else {
                lifeStringSub.contains("!")
            }
            val result = regex1.find(lifeStringSub)
            if (result != null) {
                val emptyLine = result.groupValues[3].toInt().let {
                    if (result.groupValues[1].isEmpty()) {
                        it
                    } else {
                        it - 1
                    }
                }
                repeat(emptyLine) {
                    lines += emptyLineData(width)
                }
            }
            if (isFinished) break
        }
        when (height - lines.size) {
            in Int.MIN_VALUE until 0 -> throw GollyParseError
            0 -> {}
            else -> {
                repeat(height - lines.size) { lines += emptyLineData(width) }
            }
        }
        return SquareData(width, height, lines)
    }

    fun parseToLineData(width: Int, lifeString: String): Pair<LineData, Boolean> {
        var isFinish = false
        var isLineFinish = false
        var remainString = lifeString
        var durations: List<LineDurationData> = emptyList()
        val regexLineFinish = "[1-9]+$".toRegex()
        val regexFinish = "[1-9]+!".toRegex()
        while (!isLineFinish && !isFinish) {
            val (duration, remain) = parseToLineDurationData(remainString)
            if (remain.trim().isEmpty() || regexLineFinish.matches(remain.trim())) {
                isLineFinish = true
            } else if (remain.trim() == "!") {
                isFinish = true
            } else if (regexFinish.matches(remain.trim())) {
                isLineFinish = true
                isFinish = true
            }
            remainString = remain
            durations += duration
        }
        val usedSize = durations.sumBy { it.length }
        when (width - usedSize) {
            in Int.MIN_VALUE until 0 -> throw GollyParseError
            0 -> {}
            else -> durations += emptyDurationData(width - usedSize)
        }
        return LineData(width, durations) to isFinish
    }

    enum class LineDurationType { Alive, Dead }

    data class LineDurationData(val length: Int, val type: LineDurationType)

    fun emptyDurationData(length: Int) = LineDurationData(length, LineDurationType.Dead)

    data class LineData(val width: Int, val durations: List<LineDurationData>) {

        fun isAlive(x: Int): Boolean {
            if (x !in 0 until width) {
                throw GollyParseError
            }
            var count = durations[0].length
            var index = 0
            while (x !in 0 until count) {
                index ++
                count += durations[index].length
            }
            return durations[index].type == LineDurationType.Alive
        }
    }

    fun emptyLineData(width: Int) = LineData(width = width, durations = listOf(LineDurationData(length = width, type = LineDurationType.Dead)))

    data class SquareData(val width: Int, val height: Int, val lines: List<LineData>) {

        fun isAlive(x: Int, y: Int): Boolean {
            if (x !in 0 until width || y !in 0 until height) {
                throw GollyParseError
            }
            return lines[y].isAlive(x)
        }

    }

    fun LifeModel.parseResultToBestSize(): LifeModel {
        val width = mapSize.width
        val height = mapSize.height
        if (width > SIZE_MAX || height > SIZE_MAX) {
            throw GollyParseError
        }
        val newWidth = when {
            width * 3 <= SIZE_MAX -> width * 3
            width * 2 <= SIZE_MAX -> width * 2
            else -> SIZE_MAX
        }
        val newHeight = when {
            height * 3 <= SIZE_MAX -> height * 3
            height * 2 <= SIZE_MAX -> height * 2
            else -> SIZE_MAX
        }
        val bestSize = max(newWidth, newHeight)
        val fixedSize = Size(bestSize, bestSize).fixSizeWithGameViewSize(gameViewSize)
        return this.margin(
            start = (fixedSize.width - width) / 2,
            end = fixedSize.width - width - (fixedSize.width - width) / 2,
            top = (fixedSize.height - height) / 2,
            bottom = fixedSize.height - height - (fixedSize.height - height) / 2
        )
    }

}