package org.j2c.assembly.rules

import javassist.bytecode.Opcode
import org.j2c.assembly.NBoundAssignment
import org.j2c.assembly.NBoundReference
import org.j2c.assembly.NStaticAssignment
import org.j2c.assembly.NStaticReference
import org.j2c.assembly.findNClassByFullName

@RuleContainer
object STATIC {
    val GETSTATIC = Rule(Opcode.GETSTATIC) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val fld = const.getFieldrefName(i)
        stack.add(NStaticReference(fld))
    }
    val PUTSTATIC = Rule(Opcode.PUTSTATIC) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val fld = const.getFieldrefName(i)
        val newV = stack.pop()
        stack.add(NStaticAssignment(fld, newV))
    }
}

@RuleContainer
object FIELD {
    val GETFIELD = Rule(Opcode.GETFIELD) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val obj = stack.pop()
        val fld = findNClassByFullName(const.getFieldrefClassName(i)).cname + "_" + const.getFieldrefName(i)
        stack.add(NBoundReference(obj, fld))
    }
    val PUTFIELD = Rule(Opcode.PUTFIELD) { instructions, pos, const, _, stack ->
        val i = instructions.u16bitAt(pos + 1)
        val newV = stack.pop()
        val obj = stack.pop()
        val fld = findNClassByFullName(const.getFieldrefClassName(i)).cname + "_" + const.getFieldrefName(i)
        stack.add(NBoundAssignment(obj, fld, newV))
    }
}