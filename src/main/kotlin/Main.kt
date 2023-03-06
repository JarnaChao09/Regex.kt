import regex.dsl.regex
import regex.Regex
import regex.Match
import regex.Split
import regex.Jump
import regex.Success
import regex.dsl.oneOrMore
import regex.dsl.zeroOrMore

fun main() {
    val ideal = regex {
        oneOrMore {
            +"a"
        }
        zeroOrMore {
            +"b"
        }
    }

    println(ideal) // should print
                   // match 'a'
                   // split 0, 2
                   // split 3, 5
                   // match 'b'
                   // jump 2
                   // success

    val matcher = Regex( // equivalent regex: a+b*
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