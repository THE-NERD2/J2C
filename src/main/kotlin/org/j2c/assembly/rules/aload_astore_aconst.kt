package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NAssignment
import org.j2c.assembly.NNull
import org.j2c.assembly.NReference

@RuleContainer
object ALOAD {
    val ALOAD = Rule(Opcode.ALOAD) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        stack.add(NReference(vars[i] ?: "???"))
    }
    val ALOAD_0 = Rule(Opcode.ALOAD_0) { _, _, _, vars, stack ->
        stack.add(NReference(vars[0] ?: "???"))
    }
    val ALOAD_1 = Rule(Opcode.ALOAD_1) { _, _, _, vars, stack ->
        stack.add(NReference(vars[1] ?: "???"))
    }
    val ALOAD_2 = Rule(Opcode.ALOAD_2) { _, _, _, vars, stack ->
        stack.add(NReference(vars[2] ?: "???"))
    }
    val ALOAD_3 = Rule(Opcode.ALOAD_3) { _, _, _, vars, stack ->
        stack.add(NReference(vars[3] ?: "???"))
    }
}

@RuleContainer
object ASTORE {
    val ASTORE = Rule(Opcode.ASTORE) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        val newV = stack.pop()
        vars[i] = "avar$i"
        stack.add(NAssignment("avar$i", newV))
    }
    val ASTORE_0 = Rule(Opcode.ASTORE_0) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "avar0"
        stack.add(NAssignment("avar0", newV))
    }
    val ASTORE_1 = Rule(Opcode.ASTORE_1) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "avar1"
        stack.add(NAssignment("avar1", newV))
    }
    val ASTORE_2 = Rule(Opcode.ASTORE_2) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[2] = "avar2"
        stack.add(NAssignment("avar2", newV))
    }
    val ASTORE_3 = Rule(Opcode.ASTORE_3) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[3] = "avar3"
        stack.add(NAssignment("avar3", newV))
    }
}

@RuleContainer
object ACONST {
    val ACONST_NULL = Rule(Opcode.ACONST_NULL) { _, _, _, _, stack ->
        stack.add(NNull())
    }
}