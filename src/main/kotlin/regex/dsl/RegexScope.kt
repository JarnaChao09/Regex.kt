package regex.dsl

import regex.Regexp
import regex.ast.Literal
import regex.ast.OneOrMore
import regex.ast.Regexpression
import regex.ast.ZeroOrMore

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

inline fun RegexScope.zeroOrMore(crossinline block: RegexScope.() -> Unit) {
    this.body.add(ZeroOrMore(RegexScope().apply(block).build()))
}

inline fun RegexScope.oneOrMore(crossinline block: RegexScope.() -> Unit) {
    this.body.add(OneOrMore(RegexScope().apply(block).build()))
}

inline fun regex(crossinline block: RegexScope.() -> Unit): Regexp = Regexp(RegexScope().apply(block).build())