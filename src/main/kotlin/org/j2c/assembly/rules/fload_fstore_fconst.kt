package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NAssignment
import org.j2c.assembly.NFloat
import org.j2c.assembly.NReference

@RuleContainer
object FLOAD {
    val FLOAD = Rule(Opcode.FLOAD) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        stack.add(NReference(vars[i] ?: "???"))
    }
    val FLOAD_0 = Rule(Opcode.FLOAD_0) { _, _, _, vars, stack ->
        stack.add(NReference(vars[0] ?: "???"))
    }
    val FLOAD_1 = Rule(Opcode.FLOAD_1) { _, _, _, vars, stack ->
        stack.add(NReference(vars[1] ?: "???"))
    }
    val FLOAD_2 = Rule(Opcode.FLOAD_2) { _, _, _, vars, stack ->
        stack.add(NReference(vars[2] ?: "???"))
    }
    val FLOAD_3 = Rule(Opcode.FLOAD_3) { _, _, _, vars, stack ->
        stack.add(NReference(vars[3] ?: "???"))
    }
}

@RuleContainer
object FSTORE {
    val FSTORE = Rule(Opcode.FSTORE) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        val newV = stack.pop()
        vars[i] = "fvar$i"
        stack.add(NAssignment("fvar$i", newV))
    }
    val FSTORE_0 = Rule(Opcode.FSTORE_0) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[0] = "fvar0"
        stack.add(NAssignment("fvar0", newV))
    }
    val FSTORE_1 = Rule(Opcode.FSTORE_1) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "fvar1"
        stack.add(NAssignment("fvar1", newV))
    }
    val FSTORE_2 = Rule(Opcode.FSTORE_2) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[2] = "fvar2"
        stack.add(NAssignment("fvar2", newV))
    }
    val FSTORE_3 = Rule(Opcode.FSTORE_3) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[3] = "fvar3"
        stack.add(NAssignment("fvar3", newV))
    }
}

@RuleContainer
object FCONST {
    val FCONST_0 = Rule(Opcode.FCONST_0) { _, _, _, _, stack ->
        stack.add(NFloat(0f))
    }
    val FCONST_1 = Rule(Opcode.FCONST_1) { _, _, _, _, stack ->
        stack.add(NFloat(1f))
    }
    val FCONST_2 = Rule(Opcode.FCONST_2) { _, _, _, _, stack ->
        stack.add(NFloat(2f))
    }
}