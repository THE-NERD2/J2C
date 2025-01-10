package org.j2c.ast.rules

import javassist.bytecode.Opcode
import org.j2c.ast.NByte
import org.j2c.ast.NNew
import org.j2c.ast.NOther
import org.j2c.ast.NShort
import org.j2c.ast.findNClassByFullName
import org.j2c.ast.rules.api.Rule
import org.j2c.ast.rules.api.RuleContainer

@RuleContainer
object SIPUSH {
    val SIPUSH = Rule(Opcode.SIPUSH) { state ->
        val v = state.instructions.byteAt(state.pos + 1)
        state.stack.add(NShort(v.toShort()))
    }
}

@RuleContainer
object BIPUSH {
    val BIPUSH = Rule(Opcode.BIPUSH) { state ->
        val v = state.instructions.byteAt(state.pos + 1)
        state.stack.add(NByte(v.toByte()))
    }
}

@RuleContainer
object LDC {
    val LDC = Rule(Opcode.LDC) { state ->
        val i = state.instructions.byteAt(state.pos + 1)
        val v = state.const.getLdcValue(i)
        state.stack.add(NOther(v.toString()))
    }
}

@RuleContainer
object NEW {
    val NEW = Rule(Opcode.NEW) { state ->
        val i = state.instructions.u16bitAt(state.pos + 1)
        val c = state.const.getClassInfo(i)
        state.stack.add(NNew(findNClassByFullName(c).cname))
    }
}

@RuleContainer
object DUP {
    val DUP = Rule(Opcode.DUP) { state ->
        state.stack.add(state.stack.peek())
    }
}