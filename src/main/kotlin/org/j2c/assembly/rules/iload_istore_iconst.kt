package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NAssignment
import org.j2c.assembly.NInt
import org.j2c.assembly.NReference

@RuleContainer
object ILOAD {
    val ILOAD = Rule(Opcode.ILOAD) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        stack.add(NReference(vars[i] ?: "???"))
    }
    val ILOAD_0 = Rule(Opcode.ILOAD_0) { _, _, _, vars, stack ->
        stack.add(NReference(vars[0] ?: "???"))
    }
    val ILOAD_1 = Rule(Opcode.ILOAD_1) { _, _, _, vars, stack ->
        stack.add(NReference(vars[1] ?: "???"))
    }
    val ILOAD_2 = Rule(Opcode.ILOAD_2) { _, _, _, vars, stack ->
        stack.add(NReference(vars[2] ?: "???"))
    }
    val ILOAD_3 = Rule(Opcode.ILOAD_3) { _, _, _, vars, stack ->
        stack.add(NReference(vars[3] ?: "???"))
    }
}

@RuleContainer
object ISTORE {
    val ISTORE = Rule(Opcode.ISTORE) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        val newV = stack.pop()
        vars[i] = "ivar$i"
        stack.add(NAssignment("ivar$i", newV))
    }
    val ISTORE_0 = Rule(Opcode.ISTORE_0) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[0] = "ivar0"
        stack.add(NAssignment("ivar0", newV))
    }
    val ISTORE_1 = Rule(Opcode.ISTORE_1) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "ivar1"
        stack.add(NAssignment("ivar1", newV))
    }
    val ISTORE_2 = Rule(Opcode.ISTORE_2) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[2] = "ivar2"
        stack.add(NAssignment("ivar2", newV))
    }
    val ISTORE_3 = Rule(Opcode.ISTORE_3) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[3] = "ivar3"
        stack.add(NAssignment("ivar3", newV))
    }
}

@RuleContainer
object ICONST {
    val ICONST_0 = Rule(Opcode.ICONST_0) { _, _, _, _, stack ->
        stack.add(NInt(0))
    }
    val ICONST_1 = Rule(Opcode.ICONST_1) { _, _, _, _, stack ->
        stack.add(NInt(1))
    }
    val ICONST_2 = Rule(Opcode.ICONST_2) { _, _, _, _, stack ->
        stack.add(NInt(2))
    }
    val ICONST_3 = Rule(Opcode.ICONST_3) { _, _, _, _, stack ->
        stack.add(NInt(3))
    }
    val ICONST_4 = Rule(Opcode.ICONST_4) { _, _, _, _, stack ->
        stack.add(NInt(4))
    }
    val ICONST_5 = Rule(Opcode.ICONST_5) { _, _, _, _, stack ->
        stack.add(NInt(5))
    }
    val ICONST_M1 = Rule(Opcode.ICONST_M1) { _, _, _, _, stack ->
        stack.add(NInt(-1))
    }
}