package org.j2c.assembly.rules

import javassist.bytecode.CodeIterator
import javassist.bytecode.Opcode
import org.j2c.assembly.*
import java.util.EmptyStackException
import java.util.Stack

@RuleContainer
object GOTO { // GOTO is a little weird, needs its own group
    @NoRule val posStack = Stack<Int>()
    @NoRule val followingIfStack = Stack<Boolean>()
    fun follow(instructions: CodeIterator, pos: Int, offset: Int, isIf: Boolean = false) {
        posStack.add(pos)
        followingIfStack.add(isIf)
        instructions.move(pos + offset)
    }
    fun endFollow(instructions: CodeIterator, stack: Stack<Node>) {
        try {
            // Do not go back to GOTO, but go back to else branch
            var isIf = false
            while(!isIf && followingIfStack.size > 0) {
                isIf = followingIfStack.pop()
                if (isIf) {
                    // Don't re-follow the jump! (3 because jump takes up more than 1)
                    instructions.move(posStack.pop() + 3)
                    stack.add(NOther("else"))
                } else posStack.pop()
            }
        } catch(_: EmptyStackException) {}
    }

    val GOTO = Rule(Opcode.GOTO) { instructions, pos, _, _, _ ->
        val i = instructions.s16bitAt(pos + 1)
        follow(instructions, pos, i)
    }
}

@RuleContainer
object JUMPS {
    val RETURN = Rule(Opcode.RETURN) { instructions, _, _, _, stack ->
        stack.add(NReturn())
        GOTO.endFollow(instructions, stack)
    }
    val ARETURN = Rule(Opcode.ARETURN) { instructions, _, _, _, stack ->
        val v = stack.pop()
        stack.add(NValueReturn("a", v))
        GOTO.endFollow(instructions, stack)
    }
    val IRETURN = Rule(Opcode.IRETURN) { instructions, _, _, _, stack ->
        val v = stack.pop()
        stack.add(NValueReturn("i", v))
        GOTO.endFollow(instructions, stack)
    }
    val LRETURN = Rule(Opcode.LRETURN) { instructions, _, _, _, stack ->
        val v = stack.pop()
        stack.add(NValueReturn("l", v))
        GOTO.endFollow(instructions, stack)
    }
    val FRETURN = Rule(Opcode.FRETURN) { instructions, _, _, _, stack ->
        val v = stack.pop()
        stack.add(NValueReturn("f", v))
        GOTO.endFollow(instructions, stack)
    }
    val DRETURN = Rule(Opcode.DRETURN) { instructions, _, _, _, stack ->
        val v = stack.pop()
        stack.add(NValueReturn("d", v))
        GOTO.endFollow(instructions, stack)
    }
    val ATHROW = Rule(Opcode.ATHROW) { instructions, _, _, _, stack ->
        val exception = stack.pop()
        stack.add(NAThrow(exception))
        GOTO.endFollow(instructions, stack)
    }
}