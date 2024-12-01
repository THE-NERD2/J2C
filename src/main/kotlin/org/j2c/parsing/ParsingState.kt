package org.j2c.parsing

import javassist.bytecode.CodeIterator
import javassist.bytecode.ConstPool

data class ParsingState(
    val instructions: CodeIterator,
    val const: ConstPool,
    val vars: MutableMap<Int, String>,
    val stack: RetargetableCodeStack
) {
    var pos = 0
}