package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NByte
import org.j2c.assembly.NNew
import org.j2c.assembly.NOther
import org.j2c.assembly.NShort
import org.j2c.assembly.findNClassByFullName

@RuleContainer
object SIPUSH {
    val SIPUSH = Rule(Opcode.SIPUSH) { instructions, pos, _, _, stack ->
        val v = instructions.byteAt(pos + 1)
        stack.add(NShort(v.toShort()))
    }
}

@RuleContainer
object BIPUSH {
    val BIPUSH = Rule(Opcode.BIPUSH) { instructions, pos, _, _, stack ->
        val v = instructions.byteAt(pos + 1)
        stack.add(NByte(v.toByte()))
    }
}

@RuleContainer
object LDC {
    val LDC = Rule(Opcode.LDC) { instructions, pos, const, _, stack ->
        val i = instructions.byteAt(pos + 1)
        val v = const.getLdcValue(i)
        stack.push(NOther(v.toString()))
    }
}

@RuleContainer
object NEW {
    val NEW = Rule(Opcode.NEW) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val c = const.getClassInfo(i)
        stack.add(NNew(findNClassByFullName(c).cname))
    }
}

@RuleContainer
object DUP {
    val DUP = Rule(Opcode.DUP) { _, _, _, _, stack ->
        stack.add(stack.peek())
    }
}