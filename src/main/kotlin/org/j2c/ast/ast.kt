package org.j2c.ast

abstract class Node {
    companion object {
        internal var lastId = -1
    }

    val id = ++lastId
}
// Primitive types put in Java to distinguish primitives from objects
class NClass(val name: String): Node() {
    val fields = arrayListOf<NFieldDeclaration>()
    val methods = arrayListOf<NMethodDeclaration>()
    inner class NFieldDeclaration(
        val name: String,
        val type: String
    ): Node() {
        init {
            this@NClass.fields.add(this)
        }
        var bytecodeId: Int? = null
    }
    inner class NMethodDeclaration(
        val name: String,
        val ret: String,
        val args: Collection<String>
    ): Node() {
        init {
            this@NClass.methods.add(this)
        }
        val body = arrayListOf<Node>()
        var bytecodeId: Int? = null
    }
}
class NReference(val identifier: String): Node()
class NAssignment(val dest: NReference, val v: Node): Node()