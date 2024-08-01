package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.findNClassByFullName

@RuleContainer
object BIPUSH {
    val BIPUSH = Rule(Opcode.BIPUSH) { instructions, pos, _, _, stack ->
        val v = instructions.byteAt(pos + 1)
        stack.add("$v")
    }
}

@RuleContainer
object LDC {
    val LDC = Rule(Opcode.LDC) { instructions, pos, const, _, stack ->
        val i = instructions.byteAt(pos + 1)
        val v = const.getLdcValue(i)
        stack.push(v.toString())
    }
}

@RuleContainer
object NEW {
    val NEW = Rule(Opcode.NEW) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val c = const.getClassInfo(i)
        stack.add("new ${findNClassByFullName(c)?.cname ?: "???"}")
    }
}

@RuleContainer
object DUP {
    val DUP = Rule(Opcode.DUP) { _, _, _, _, stack ->
        stack.add(stack.peek())
    }
}