package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NAssignment
import org.j2c.assembly.NDouble
import org.j2c.assembly.NReference

@RuleContainer
object DLOAD {
    val DLOAD = Rule(Opcode.DLOAD) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        stack.add(NReference(vars[i] ?: "???"))
    }
    val DLOAD_0 = Rule(Opcode.DLOAD_0) { _, _, _, vars, stack ->
        stack.add(NReference(vars[0] ?: "???"))
    }
    val DLOAD_1 = Rule(Opcode.DLOAD_1) { _, _, _, vars, stack ->
        stack.add(NReference(vars[1] ?: "???"))
    }
    val DLOAD_2 = Rule(Opcode.DLOAD_2) { _, _, _, vars, stack ->
        stack.add(NReference(vars[2] ?: "???"))
    }
    val DLOAD_3 = Rule(Opcode.DLOAD_3) { _, _, _, vars, stack ->
        stack.add(NReference(vars[3] ?: "???"))
    }
}

@RuleContainer
object DSTORE {
    val DSTORE = Rule(Opcode.DSTORE) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        val newV = stack.pop()
        vars[i]  = "dvar$i"
        stack.add(NAssignment("dvar$i", newV))
    }
    val DSTORE_0 = Rule(Opcode.DSTORE_0) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[0] = "dvar0"
        stack.add(NAssignment("dvar0", newV))
    }
    val DSTORE_1 = Rule(Opcode.DSTORE_1) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "dvar1"
        stack.add(NAssignment("dvar1", newV))
    }
    val DSTORE_3 = Rule(Opcode.DSTORE_3) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[3] = "dvar3"
        stack.add(NAssignment("dvar3", newV))
    }
}

@RuleContainer
object DCONST {
    val DCONST_0 = Rule(Opcode.DCONST_0) { _, _, _, _, stack ->
        stack.add(NDouble(0.0))
    }
    val DCONST_1 = Rule(Opcode.DCONST_1) { _, _, _, _, stack ->
        stack.add(NDouble(1.0))
    }
}