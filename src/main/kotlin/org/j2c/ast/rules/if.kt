package org.j2c.ast.rules

import javassist.bytecode.Opcode
import org.j2c.ast.NOther
import org.j2c.ast.rules.api.Rule
import org.j2c.ast.rules.api.RuleContainer

@RuleContainer
object IF {
    val IFEQ = Rule(Opcode.IFEQ) { state ->
        val v = state.stack.pop()
        val i = state.instructions.s16bitAt(state.pos + 1)
        state.stack.add(NOther("if($v == 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFNE = Rule(Opcode.IFNE) { state ->
        val v = state.stack.pop()
        val i = state.instructions.s16bitAt(state.pos + 1)
        state.stack.add(NOther("if($v != 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFGE = Rule(Opcode.IFGE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v = state.stack.pop()
        state.stack.add(NOther("if($v >= 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFGT = Rule(Opcode.IFGT) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v = state.stack.pop()
        state.stack.add(NOther("if($v > 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFLE = Rule(Opcode.IFLE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v = state.stack.pop()
        state.stack.add(NOther("if($v <= 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFLT = Rule(Opcode.IFLT) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v = state.stack.pop()
        state.stack.add(NOther("if($v < 0)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
}

@RuleContainer
object IF_A {
    val IFNULL = Rule(Opcode.IFNULL) { state ->
        val v = state.stack.pop()
        val i = state.instructions.s16bitAt(state.pos + 1)
        state.stack.add(NOther("if($v == null)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IFNONNULL = Rule(Opcode.IFNONNULL) { state ->
        val v = state.stack.pop()
        val i = state.instructions.s16bitAt(state.pos + 1)
        state.stack.add(NOther("if($v != null)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ACMPEQ = Rule(Opcode.IF_ACMPEQ) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 == $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ACMPNE = Rule(Opcode.IF_ACMPNE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 != $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
}

@RuleContainer
object IF_I {
    val IF_ICMPEQ = Rule(Opcode.IF_ICMPEQ) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 == $v2"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ICMPNE = Rule(Opcode.IF_ICMPNE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 != $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ICMPGE = Rule(Opcode.IF_ICMPGE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 >= $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ICMPGT = Rule(Opcode.IF_ICMPGT) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 > $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ICMPLE = Rule(Opcode.IF_ICMPLE) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 <= $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
    val IF_ICMPLT = Rule(Opcode.IF_ICMPLT) { state ->
        val i = state.instructions.s16bitAt(state.pos + 1)
        val v2 = state.stack.pop()
        val v1 = state.stack.pop()
        state.stack.add(NOther("if($v1 < $v2)"))
        GOTO.follow(state.instructions, state.pos, i, true)
    }
}