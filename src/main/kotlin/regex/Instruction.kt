package regex

sealed interface Instruction

data class Match(val value: Char) : Instruction

data class Split(val continueAt: Int, val jumpTo: Int) : Instruction

data class Jump(val to: Int) : Instruction

data object Success : Instruction

data object Failure : Instruction