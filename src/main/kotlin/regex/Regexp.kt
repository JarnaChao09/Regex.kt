package regex

import regex.ast.Regexpression

class Regexp(private val regexExpression: Regexpression) {
    private val bytecode: RegexBytecode by lazy {
        regexExpression.generateBytecode() + listOf(Success)
    }

    fun match(input: String): Boolean {
        return this.execute(input)
    }

    fun fullMatch(input: String): Boolean {
        return this.execute(
            input,
            onSuccess = { str: String, _: Int, cursor: Int ->
                cursor == str.length
            }
        )
    }

    private inline fun execute(
        str: String,
        instructionStack: ArrayDeque<Pair<Int, Int>> = ArrayDeque(listOf(0 to 0)),
        crossinline onSuccess: (String, ip: Int, cursor: Int) -> Boolean = { _, _, _ -> true },
        crossinline onFailure: (String, ip: Int, cursor: Int) -> Boolean = { _, _, _ -> false },
    ): Boolean {
        while (instructionStack.isNotEmpty()) {
            val (ip, cursor) = instructionStack.removeFirst()
            when (val op = this.bytecode[ip]) {
                is Match -> {
                    if (cursor < str.length && str[cursor] == op.value) {
                        instructionStack.addFirst((ip + 1) to (cursor + 1))
                    }
                }
                is Split -> {
                    instructionStack.addFirst(op.jumpTo to cursor)
                    instructionStack.addFirst(op.continueAt to cursor)
                }
                is Jump -> {
                    instructionStack.addFirst(op.to to cursor)
                }
                is Success -> { return onSuccess(str, ip, cursor) }
                is Failure -> { return onFailure(str, ip, cursor) }
            }
        }
        return false
    }

    fun dumpBytecodeString(): String {
        return this.bytecode.mapIndexed { i, instruction ->
            val instructionString: String = when (instruction) {
                is Jump -> "jump ${instruction.to}"
                is Match -> "match '${instruction.value}'"
                is Split -> "split ${instruction.continueAt}, ${instruction.jumpTo}"
                Failure -> "failure"
                Success -> "success"
            }
            "$i $instructionString"
        }.joinToString(separator = "\n")
    }

    fun generateRegexString(): String = this.regexExpression.generateRegexString()
}