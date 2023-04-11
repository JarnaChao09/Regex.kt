package regex.ast

import regex.Jump
import regex.Match
import regex.RegexBytecode
import regex.Split

sealed interface Regexpression {
    var next: Regexpression?

    fun generateRegexString(): String

    fun generateBytecode(offset: Int = 0): RegexBytecode

    fun deepCopy(): Regexpression
}

data class Literal(val value: String, override var next: Regexpression? = null) : Regexpression {
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
    override fun generateRegexString(): String {
        return "(?:${expression.generateRegexString()})*${this.next?.generateRegexString() ?: ""}"
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
    override fun generateRegexString(): String {
        return "(?:${expression.generateRegexString()})+${this.next?.generateRegexString() ?: ""}"
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