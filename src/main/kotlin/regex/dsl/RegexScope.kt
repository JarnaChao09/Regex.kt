package regex.dsl

import regex.Regexp
import regex.ast.*

@RegexMarker
open class RegexScope(val body: MutableList<Regexpression> = mutableListOf()) {
    operator fun String.unaryPlus() {
        this@RegexScope.body.add(Literal(this@unaryPlus))
    }

    operator fun Regexpression.unaryPlus() {
        this@RegexScope.body.add(this@unaryPlus.deepCopy())
    }

    open fun build(): Regexpression {
        val ret = this.body[0]

        this.body.reduce { acc, next ->
            acc.next = next
            next
        }

        return ret
    }
}

class ZeroOrMoreScope : RegexScope() {
    override fun build(): Regexpression = ZeroOrMore(super.build())
}

inline fun RegexScope.zeroOrMore(crossinline block: ZeroOrMoreScope.() -> Unit) {
    this.body.add(ZeroOrMoreScope().apply(block).build())
}

class OneOrMoreScope : RegexScope() {
    override fun build(): Regexpression = OneOrMore(super.build())
}

inline fun RegexScope.oneOrMore(crossinline block: OneOrMoreScope.() -> Unit) {
    this.body.add(OneOrMoreScope().apply(block).build())
}

class OptionalScope : RegexScope() {
    override fun build(): Regexpression = Optional(super.build())
}

inline fun RegexScope.optional(crossinline block: OptionalScope.() -> Unit) {
    this.body.add(OptionalScope().apply(block).build())
}

class AlterationScope : RegexScope() {
    override fun build(): Regexpression = Alteration(this.body.toList())
}

inline fun RegexScope.alteration(crossinline block: AlterationScope.() -> Unit) {
    this.body.add(AlterationScope().apply(block).build())
}

inline fun regex(crossinline block: RegexScope.() -> Unit): Regexp = Regexp(RegexScope().apply(block).build())