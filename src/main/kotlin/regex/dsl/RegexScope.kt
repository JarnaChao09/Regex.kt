package regex.dsl

import regex.Regex

@RegexMarker
open class RegexScope internal constructor() {
    operator fun String.unaryPlus() {
        TODO()
    }
}

class ZeroOrMoreScope : RegexScope()

fun RegexScope.zeroOrMore(block: ZeroOrMoreScope.() -> Unit) {
    TODO()
}

class OneOrMoreScope() : RegexScope()

fun RegexScope.oneOrMore(block: OneOrMoreScope.() -> Unit) {
    TODO()
}

fun regex(block: RegexScope.() -> Unit): Regex {
    TODO()
}