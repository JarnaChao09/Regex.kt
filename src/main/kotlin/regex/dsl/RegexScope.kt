package regex.dsl

import regex.Regexp
import regex.ast.*

@RegexMarker
class RegexScope(val body: MutableList<Regexpression> = mutableListOf()) {
    operator fun String.unaryPlus() {
        this@RegexScope.body.add(Literal(this@unaryPlus))
    }

    operator fun Regexpression.unaryPlus() {
        this@RegexScope.body.add(this@unaryPlus.deepCopy())
    }

    fun build(): Regexpression {
        val ret = this.body[0]

        this.body.reduce { acc, next ->
            acc.next = next
            next
        }

        return ret
    }
}

fun RegexScope.zeroOrMore(block: RegexScope.() -> Unit) {
    this.body.add(ZeroOrMore(RegexScope().apply(block).build()))
}

fun RegexScope.oneOrMore(block: RegexScope.() -> Unit) {
    this.body.add(OneOrMore(RegexScope().apply(block).build()))
}

fun RegexScope.optional(block: RegexScope.() -> Unit) {
    this.body.add(Optional(RegexScope().apply(block).build()))
}

fun RegexScope.alteration(block: RegexScope.() -> Unit) {
    this.body.add(Alteration(RegexScope().apply(block).body.toList()))
}

fun regex(block: RegexScope.() -> Unit): Regexp = Regexp(RegexScope().apply(block).build())