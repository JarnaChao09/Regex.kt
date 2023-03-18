import regex.*
import regex.ast.Literal
import regex.ast.OneOrMore
import regex.ast.ZeroOrMore
import regex.dsl.oneOrMore
import regex.dsl.regex
import regex.dsl.zeroOrMore

fun main() {
    val structure = Regexp(
        OneOrMore(
            Literal("a",),
            next = ZeroOrMore(
                Literal("b")
            )
        )
    )

    val dsl = regex {
        oneOrMore {
            +"a"
        }
        zeroOrMore {
            +"b"
        }
    }

    println(structure.dumpBytecodeString())

    println()

    println(dsl.dumpBytecodeString())

    println()

    // a+b*
    // match 'a'
    // split 0, 2
    // split 3, 5
    // match 'b'
    // jump 2
    // success

    println("match:")
    println("\tstructure:")
    println("\t\t  aa: ${structure.match("aa")}")   // true
    println("\t\t  ab: ${structure.match("ab")}")   // true
    println("\t\t  bb: ${structure.match("bb")}")   // false
    println("\t\t abb: ${structure.match("abb")}")  // true
    println("\t\t aab: ${structure.match("aab")}")  // true
    println("\t\taabc: ${structure.match("aabc")}") // true

    println("\tdsl:")
    println("\t\t  aa: ${dsl.match("aa")}")   // true
    println("\t\t  ab: ${dsl.match("ab")}")   // true
    println("\t\t  bb: ${dsl.match("bb")}")   // false
    println("\t\t abb: ${dsl.match("abb")}")  // true
    println("\t\t aab: ${dsl.match("aab")}")  // true
    println("\t\taabc: ${dsl.match("aabc")}") // true

    println()

    println("full match:")
    println("\tstructure:")
    println("\t\t  aa: ${structure.fullMatch("aa")}")   // true
    println("\t\t  ab: ${structure.fullMatch("ab")}")   // true
    println("\t\t  bb: ${structure.fullMatch("bb")}")   // false
    println("\t\t abb: ${structure.fullMatch("abb")}")  // true
    println("\t\t aab: ${structure.fullMatch("aab")}")  // true
    println("\t\taabc: ${structure.fullMatch("aabc")}") // false

    println("\tdsl:")
    println("\t\t  aa: ${dsl.fullMatch("aa")}")   // true
    println("\t\t  ab: ${dsl.fullMatch("ab")}")   // true
    println("\t\t  bb: ${dsl.fullMatch("bb")}")   // false
    println("\t\t abb: ${dsl.fullMatch("abb")}")  // true
    println("\t\t aab: ${dsl.fullMatch("aab")}")  // true
    println("\t\taabc: ${dsl.fullMatch("aabc")}") // false
}