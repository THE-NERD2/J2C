package org.j2c.parsing

import org.j2c.ast.ControlFlowNode
import org.j2c.ast.Node
import org.j2c.ast.NIf
import java.util.*

class RetargetableCodeStack {
    private val blocks = arrayListOf(arrayListOf<Node>())
    fun enterBlock(block: ArrayList<Node>) = blocks.add(block)
    fun leaveBlock() = blocks.removeLast()
    fun getIfNodeInLastBlock() = blocks.last().last() as NIf
    fun getTopBlock() = blocks[0]
    fun add(element: Node) = blocks.last().add(element)
    fun pop(): Node { // This will try to get the last non-control-flow node in each block until it finds one
        val tempBlocks = blocks.map { it.clone() as ArrayList<Node> } as ArrayList<ArrayList<Node>>

        var blockIndex = blocks.size
        var nodeIndex: Int? = null
        var ret: Node? = null
        while(ret == null) {
            val block = tempBlocks.removeLast()
            nodeIndex = block.size
            do {
                ret = block.removeLastOrNull()
                nodeIndex--
            } while(ret is ControlFlowNode)
            blockIndex--
        }
        blocks[blockIndex].removeAt(nodeIndex!!)
        return ret
    }
    fun peek(): Node { // Does the same as pop()
        val tempBlocks = blocks.map { it.clone() as ArrayList<Node> } as ArrayList<ArrayList<Node>>

        var ret: Node? = null
        while(ret == null) {
            val block = tempBlocks.removeLast()
            do {
                ret = block.removeLastOrNull()
            } while(ret is ControlFlowNode)
        }
        return ret
    }
}