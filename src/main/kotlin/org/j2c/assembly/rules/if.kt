package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NOther

@RuleContainer
object IF {
    val IFEQ = Rule(Opcode.IFEQ) { instructions, pos, _, _, stack ->
        val v = stack.pop()
        val i = instructions.s16bitAt(pos + 1)
        stack.add(NOther("if($v == 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFNE = Rule(Opcode.IFNE) { instructions, pos, _, _, stack ->
        val v = stack.pop()
        val i = instructions.s16bitAt(pos + 1)
        stack.add(NOther("if($v != 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFGE = Rule(Opcode.IFGE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v = stack.pop()
        stack.add(NOther("if($v >= 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFGT = Rule(Opcode.IFGT) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v = stack.pop()
        stack.add(NOther("if($v > 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFLE = Rule(Opcode.IFLE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v = stack.pop()
        stack.add(NOther("if($v <= 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFLT = Rule(Opcode.IFLT) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v = stack.pop()
        stack.add(NOther("if($v < 0)"))
        GOTO.follow(instructions, pos, i, true)
    }
}

@RuleContainer
object IF_A {
    val IFNULL = Rule(Opcode.IFNULL) { instructions, pos, _, _, stack ->
        val v = stack.pop()
        val i = instructions.s16bitAt(pos + 1)
        stack.add(NOther("if($v == null)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IFNONNULL = Rule(Opcode.IFNONNULL) { instructions, pos, _, _, stack ->
        val v = stack.pop()
        val i = instructions.s16bitAt(pos + 1)
        stack.add(NOther("if($v != null)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ACMPEQ = Rule(Opcode.IF_ACMPEQ) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 == $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ACMPNE = Rule(Opcode.IF_ACMPNE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 != $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
}

@RuleContainer
object IF_I {
    val IF_ICMPEQ = Rule(Opcode.IF_ICMPEQ) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 == $v2"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ICMPNE = Rule(Opcode.IF_ICMPNE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 != $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ICMPGE = Rule(Opcode.IF_ICMPGE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 >= $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ICMPGT = Rule(Opcode.IF_ICMPGT) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 > $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ICMPLE = Rule(Opcode.IF_ICMPLE) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 <= $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
    val IF_ICMPLT = Rule(Opcode.IF_ICMPLT) { instructions, pos, _, _, stack ->
        val i = instructions.s16bitAt(pos + 1)
        val v2 = stack.pop()
        val v1 = stack.pop()
        stack.add(NOther("if($v1 < $v2)"))
        GOTO.follow(instructions, pos, i, true)
    }
}