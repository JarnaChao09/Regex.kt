import regex.*
import regex.ast.*
import regex.dsl.*

fun main() {
//    val structure = Regexp(
//        OneOrMore(
//            Literal("a",),
//            next = ZeroOrMore(
//                Literal("b"),
//                next = Optional(Literal("c"))
//            )
//        )
//    )
//
//    val dsl = regex {
//        oneOrMore {
//            +"a"
//        }
//        zeroOrMore {
//            +"b"
//        }
//        optional {
//            +"c"
//        }
//    }
//
//    println(structure.dumpBytecodeString())
//
//    println(structure.generateRegexString())
//
//    println()
//
//    println(dsl.dumpBytecodeString())
//
//    println(dsl.generateRegexString())
//
//    println()

    val aldsl = regex {
        alteration {
            oneOrMore {
                +"a"
            }
            oneOrMore {
                +"b"
            }
            zeroOrMore {
                +"c"
            }
        }
        +"d"
    }

    println(aldsl.generateRegexString())

    println()

    println(aldsl.dumpBytecodeString())

    println()

    println(aldsl.fullMatch("aad"))
    println(aldsl.fullMatch("bbd"))
    println(aldsl.fullMatch("ccd"))
    println(aldsl.fullMatch("d"))
    println(aldsl.fullMatch("aa"))
    println(aldsl.fullMatch("bb"))
    println(aldsl.fullMatch("cc"))
    println(aldsl.fullMatch(""))
    println(aldsl.fullMatch("e"))


    // a+b*
    // match 'a'
    // split 0, 2
    // split 3, 5
    // match 'b'
    // jump 2
    // success

//    println("match:")
//    println("\tstructure:")
//    println("\t\t  aa: ${structure.match("aa")}")   // true
//    println("\t\t  ab: ${structure.match("ab")}")   // true
//    println("\t\t  bb: ${structure.match("bb")}")   // false
//    println("\t\t abb: ${structure.match("abb")}")  // true
//    println("\t\t aab: ${structure.match("aab")}")  // true
//    println("\t\taabc: ${structure.match("aabc")}") // true
//
//    println("\tdsl:")
//    println("\t\t  aa: ${dsl.match("aa")}")   // true
//    println("\t\t  ab: ${dsl.match("ab")}")   // true
//    println("\t\t  bb: ${dsl.match("bb")}")   // false
//    println("\t\t abb: ${dsl.match("abb")}")  // true
//    println("\t\t aab: ${dsl.match("aab")}")  // true
//    println("\t\taabc: ${dsl.match("aabc")}") // true
//
//    println()
//
//    println("full match:")
//    println("\tstructure:")
//    println("\t\t  aa: ${structure.fullMatch("aa")}")   // true
//    println("\t\t  ab: ${structure.fullMatch("ab")}")   // true
//    println("\t\t  bb: ${structure.fullMatch("bb")}")   // false
//    println("\t\t abb: ${structure.fullMatch("abb")}")  // true
//    println("\t\t aab: ${structure.fullMatch("aab")}")  // true
//    println("\t\taabc: ${structure.fullMatch("aabc")}") // false
//
//    println("\tdsl:")
//    println("\t\t  aa: ${dsl.fullMatch("aa")}")   // true
//    println("\t\t  ab: ${dsl.fullMatch("ab")}")   // true
//    println("\t\t  bb: ${dsl.fullMatch("bb")}")   // false
//    println("\t\t abb: ${dsl.fullMatch("abb")}")  // true
//    println("\t\t aab: ${dsl.fullMatch("aab")}")  // true
//    println("\t\taabc: ${dsl.fullMatch("aabc")}") // false
}