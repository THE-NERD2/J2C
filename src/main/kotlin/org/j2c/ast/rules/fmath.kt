package org.j2c.ast.rules

import javassist.bytecode.Opcode
import org.j2c.ast.NBinOp
import org.j2c.ast.rules.api.Rule
import org.j2c.ast.rules.api.RuleContainer

@RuleContainer
object FMATH {
    val FADD = Rule(Opcode.FADD) { state ->
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NBinOp("f", "+", v1, v2))
    }
    val FSUB = Rule(Opcode.FSUB) { state ->
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NBinOp("f", "-", v1, v2))
    }
    val FMUL = Rule(Opcode.FMUL) { state ->
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NBinOp("f", "*", v1, v2))
    }
    val FDIV = Rule(Opcode.FDIV) { state ->
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NBinOp("f", "/", v1, v2))
    }
}