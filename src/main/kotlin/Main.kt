import regex.RegexVM
import regex.Match
import regex.Split
import regex.Jump
import regex.Success
import regex.Failure

fun main() {
    val matcher = RegexVM( // equivalent regex: a+b*
        listOf(
            Match('a'),  // 0
            Split(0, 2), // 1
            Split(3, 5), // 2
            Match('b'),  // 3
            Jump(2),     // 4
            Success,     // 5
        )
    )

    println("match:")
    println("  aa: ${matcher.match("aa")}")   // true
    println("  ab: ${matcher.match("ab")}")   // true
    println("  bb: ${matcher.match("bb")}")   // false
    println(" abb: ${matcher.match("abb")}")  // true
    println(" aab: ${matcher.match("aab")}")  // true
    println("aabc: ${matcher.match("aabc")}") // true

    println()

    println("full match:")
    println("  aa: ${matcher.fullMatch("aa")}")   // true
    println("  ab: ${matcher.fullMatch("ab")}")   // true
    println("  bb: ${matcher.fullMatch("bb")}")   // false
    println(" abb: ${matcher.fullMatch("abb")}")  // true
    println(" aab: ${matcher.fullMatch("aab")}")  // true
    println("aabc: ${matcher.fullMatch("aabc")}") // false
}