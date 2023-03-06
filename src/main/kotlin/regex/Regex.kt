package regex

class Regex(private val bytecode: RegexBytecode) {
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

    override fun toString(): String {
        return this.bytecode.joinToString("\n")
    }
}