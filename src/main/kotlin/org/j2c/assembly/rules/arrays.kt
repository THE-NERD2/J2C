package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NArrayLength

@RuleContainer
object ARRAYLENGTH {
    val ARRAYLENGTH = Rule(Opcode.ARRAYLENGTH) { _, _, _, _, stack ->
        stack.add(NArrayLength(stack.pop()))
    }
}