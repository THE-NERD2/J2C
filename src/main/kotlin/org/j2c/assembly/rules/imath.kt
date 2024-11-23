package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NBinOp

@RuleContainer
object IMATH {
    val IADD = Rule(Opcode.IADD) { _, _, _, _, stack ->
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NBinOp("i", "+", v1, v2))
    }
    val ISUB = Rule(Opcode.ISUB) { _, _, _, _, stack ->
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NBinOp("i", "-", v1, v2))
    }
    val IMUL = Rule(Opcode.IMUL) { _, _, _, _, stack ->
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NBinOp("i", "*", v1, v2))
    }
    val IDIV = Rule(Opcode.IDIV) { _, _, _, _, stack ->
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NBinOp("i", "/", v1, v2))
    }
}