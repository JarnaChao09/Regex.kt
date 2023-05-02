package regex.ast

import regex.Jump
import regex.Match
import regex.RegexBytecode
import regex.Split

sealed interface Regexpression {
    var next: Regexpression?
    val isSingleCharacter: Boolean

    fun generateRegexString(): String

    fun generateBytecode(offset: Int = 0): RegexBytecode

    fun deepCopy(): Regexpression
}

data class Literal(val value: String, override var next: Regexpression? = null) : Regexpression {
    override val isSingleCharacter: Boolean
        get() = this.next == null && this.value.length == 1

    override fun generateRegexString(): String {
        return "$value${this.next?.generateRegexString() ?: ""}"
    }

    override fun deepCopy(): Regexpression = this.copy()

    override fun generateBytecode(offset: Int): RegexBytecode {
        return buildList {
            this@Literal.value.forEach {
                add(Match(it))
            }
            this@Literal.next?.let {
                addAll(it.generateBytecode(this@buildList.size + offset))
            }
        }
    }
}

data class ZeroOrMore(val expression: Regexpression, override var next: Regexpression? = null) : Regexpression {
    override val isSingleCharacter: Boolean
        get() = this.expression.isSingleCharacter

    override fun generateRegexString(): String {
        return "${
            this.expression.generateRegexString().let {
                if (this.isSingleCharacter) {
                    it
                } else {
                    it.wrap(left = "(?:")
                }
            }
        }*${this.next?.generateRegexString() ?: ""}"
    }

    override fun deepCopy(): Regexpression = this.copy()

    override fun generateBytecode(offset: Int): RegexBytecode {
        return buildList {
            val expr1 = this@ZeroOrMore.expression.generateBytecode(1 + offset)
            add(Split(1 + offset, expr1.size + 2 + offset))
            addAll(expr1)
            add(Jump(offset))
            this@ZeroOrMore.next?.let {
                addAll(it.generateBytecode(this@buildList.size + offset))
            }
        }
    }
}

data class OneOrMore(val expression: Regexpression, override var next: Regexpression? = null) : Regexpression {
    override val isSingleCharacter: Boolean
        get() = this.expression.isSingleCharacter

    override fun generateRegexString(): String {
        return "${
            this.expression.generateRegexString().let {
                if (this.isSingleCharacter) {
                    it
                } else {
                    it.wrap(left = "(?:")
                }
            }
        }+${this.next?.generateRegexString() ?: ""}"
    }

    override fun deepCopy(): Regexpression = this.copy()

    override fun generateBytecode(offset: Int): RegexBytecode {
        return buildList {
            val expr1 = this@OneOrMore.expression.generateBytecode(offset)
            addAll(expr1)
            add(Split(offset, expr1.size + 1 + offset))
            this@OneOrMore.next?.let {
                addAll(it.generateBytecode(this@buildList.size + offset))
            }
        }
    }
}

data class Optional(val expression: Regexpression, override var next: Regexpression? = null) : Regexpression {
    override val isSingleCharacter: Boolean
        get() = this.expression.isSingleCharacter

    override fun generateRegexString(): String {
        return "${
            this.expression.generateRegexString().let {
                if (this.isSingleCharacter) {
                    it
                } else {
                    it.wrap(left = "(?:")
                }
            }
        }?${this.next?.generateRegexString() ?: ""}"
    }

    override fun generateBytecode(offset: Int): RegexBytecode {
        return buildList {
            val expr = this@Optional.expression.generateBytecode(1 + offset)
            add(Split(1 + offset, expr.size + 1 + offset))
            addAll(expr)
            this@Optional.next?.let {
                addAll(it.generateBytecode(this@buildList.size + offset))
            }
        }
    }

    override fun deepCopy(): Regexpression = this.copy()
}

data class Alteration(
    val expressions: List<Regexpression>,
    override var next: Regexpression? = null,
) : Regexpression {
    override val isSingleCharacter: Boolean
        get() = false

    override fun generateRegexString(): String {
        return "${
            this.expressions.joinToString(
                separator = "|", 
                prefix = "(?:", 
                postfix = ")", 
                transform = Regexpression::generateRegexString
            )
        }${this.next?.generateRegexString() ?: ""}"
    }

    override fun generateBytecode(offset: Int): RegexBytecode {
        return buildList {
            val (runningLength, bytecodes) = this@Alteration.expressions.scan(0 to listOf<RegexBytecode>()) { acc, expr ->
                expr.generateBytecode(acc.first + 1).let {
                    (acc.first + it.size + 2) to listOf(it)
                }
            }.let {
                it.fold(listOf<Int>() to listOf<RegexBytecode>()) { acc, pair ->
                    (acc.first + pair.first) to (acc.second + pair.second)
                }
            }

            val last = runningLength.last() - 1

            for (code in bytecodes.dropLast(1)) {
                add(Split(this@buildList.size + 1 + offset, this@buildList.size + code.size + 2 + offset))
                addAll(code)
                add(Jump(last + offset))
            }

            val lastCode = bytecodes.last()

            add(Split(this@buildList.size + 1 + offset, this@buildList.size + lastCode.size + 1 + offset))
            addAll(lastCode)

            this@Alteration.next?.let {
                addAll(it.generateBytecode(this@buildList.size + offset))
            }
        }
    }

    override fun deepCopy(): Regexpression = this.copy()

}

private fun String.wrap(left: String = "(", right: String = ")"): String {
    return "$left$this$right"
}