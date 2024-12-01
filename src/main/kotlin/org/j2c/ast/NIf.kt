package org.j2c.ast

import org.j2c.indentBlock

class NIf(val condition: Node): ControlFlowNode("NIf") {
    val ifBranch = arrayListOf<Node>()
    val elseBranch = arrayListOf<Node>() // Will remain empty if irrelevant
    // These four for JNI
    fun ifSize() = ifBranch.size
    fun getIfElement(i: Int) = ifBranch[i]
    fun elseSize() = elseBranch.size
    fun getElseElement(i: Int) = elseBranch[i]
    override fun toString(): String {
        var str = "if($condition) {\n"
        var ifBlock = ""
        ifBranch.forEach {
            ifBlock += "$it\n"
        }
        str += "\t" + indentBlock(ifBlock).trim() + "\n}"
        if(elseBranch.size > 0) {
            var elseBlock = ""
            elseBranch.forEach {
                elseBlock += "$it\n"
            }
            str += " else {\n\t" + indentBlock(elseBlock).trim() + "\n}"
        }
        return str
    }
}