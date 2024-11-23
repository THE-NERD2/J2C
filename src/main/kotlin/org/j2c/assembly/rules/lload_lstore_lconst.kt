package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NAssignment
import org.j2c.assembly.NLong
import org.j2c.assembly.NReference

@RuleContainer
object LLOAD {
    val LLOAD = Rule(Opcode.LLOAD) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        stack.add(NReference(vars[i] ?: "???"))
    }
    val LLOAD_0 = Rule(Opcode.LLOAD_0) { _, _, _, vars, stack ->
        stack.add(NReference(vars[0] ?: "???"))
    }
    val LLOAD_1 = Rule(Opcode.LLOAD_1) { _, _, _, vars, stack ->
        stack.add(NReference(vars[1] ?: "???"))
    }
    val LLOAD_2 = Rule(Opcode.LLOAD_2) { _, _, _, vars, stack ->
        stack.add(NReference(vars[2] ?: "???"))
    }
    val LLOAD_3 = Rule(Opcode.LLOAD_3) { _, _, _, vars, stack ->
        stack.add(NReference(vars[3] ?: "???"))
    }
}

@RuleContainer
object LSTORE {
    val LSTORE = Rule(Opcode.LSTORE) { instructions, pos, _, vars, stack ->
        val i = instructions.byteAt(pos + 1)
        val newV = stack.pop()
        vars[i] =  "lvar$i"
        stack.add(NAssignment("lvar$i", newV))
    }
    val LSTORE_0 = Rule(Opcode.LSTORE_0) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[0] = "lvar0"
        stack.add(NAssignment("lvar0", newV))
    }
    val LSTORE_1 = Rule(Opcode.LSTORE_1) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "lvar1"
        stack.add(NAssignment("lvar1", newV))
    }
    val LSTORE_2 = Rule(Opcode.LSTORE_2) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "lvar2"
        stack.add(NAssignment("lvar2", newV))
    }
    val LSTORE_3 = Rule(Opcode.LSTORE_3) { _, _, _, vars, stack ->
        val newV = stack.pop()
        vars[1] = "lvar3"
        stack.add(NAssignment("lvar3", newV))
    }
}

@RuleContainer
object LCONST {
    val LCONST_0 = Rule(Opcode.LCONST_0) { _, _, _, _, stack ->
        stack.add(NLong(0L))
    }
    val LCONST_1 = Rule(Opcode.LCONST_1) { _, _, _, _, stack ->
        stack.add(NLong(1L))
    }
}