package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.findNClassByFullName

@RuleContainer
object STATIC {
    val GETSTATIC = Rule(Opcode.GETSTATIC) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val fld = const.getFieldrefName(i)
        stack.add(fld)
    }
}

@RuleContainer
object FIELD {
    val GETFIELD = Rule(Opcode.GETFIELD) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val obj = stack.pop()
        // Non-null assertion is temporary. TODO: null results in search for class in class pool
        val fld = findNClassByFullName(const.getFieldrefClassName(i))!!.cname + "_" + const.getFieldrefName(i)
        stack.add("$obj.$fld")
    }
    val PUTFIELD = Rule(Opcode.PUTFIELD) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val newV = stack.pop()
        val obj = stack.pop()
        // Non-null assertion is temporary. TODO: null results in search for class in class pool
        val fld = findNClassByFullName(const.getFieldrefClassName(i))!!.cname + "_" + const.getFieldrefName(i)
        stack.add("$obj.$fld = $newV")
    }
}