package org.j2c.parsing

import javassist.CtClass
import javassist.NotFoundException
import javassist.bytecode.Mnemonic
import org.j2c.*
import org.j2c.ast.*
import org.j2c.development.registerUnknownOpcode
import org.j2c.exceptions.InfiniteLoopException
import org.j2c.exceptions.UnknownOpcodeException
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError
import kotlin.reflect.jvm.javaField

@OptIn(ExperimentalStdlibApi::class)
fun parse(name: String): NClass? {
    beginProgress(name)

    var nclass: NClass? = null
    try {
        val kclass: KClass<*>
        val ctclass: CtClass

        kclass = classLoader.loadClass(name).kotlin
        nclass = NClass(kclass.qualifiedName!!, kclass.simpleName!!)
        ctclass = pool.get(name)

        kclass.members.forEach {
            if (it is KProperty) {
                NFieldDeclaration(nclass, it.name, (it.javaField?.type ?: it.returnType.javaType).typeName)
            } else if (it is KFunction) {
                try {
                    // Process method code
                    val methodInfo = ctclass.getDeclaredMethod(it.name).methodInfo
                    val instructions = methodInfo.codeAttribute.iterator()
                    val const = methodInfo.constPool
                    val vars = mutableMapOf<Int, String>()
                    it.parameters.forEachIndexed { i: Int, v: KParameter -> vars[i] = "param$i" }

                    val stack = RetargetableCodeStack()
                    state = ParsingState(instructions, const, vars, stack)
                    while (instructions.hasNext()) {
                        val pos = instructions.next()
                        val opcode = instructions.byteAt(pos)
                        state.pos = pos

                        try {
                            rules.find { it.opcode == opcode }?.predicate?.invoke(state)
                                ?: run {
                                    UnknownOpcodeException(Mnemonic.OPCODE[opcode]).printStackTrace()
                                    registerUnknownOpcode(Mnemonic.OPCODE[opcode])
                                }
                        } catch(e: InfiniteLoopException) {
                            e.printStackTrace()
                            break
                        }
                    }
                    NMethodDeclaration(
                        nclass,
                        it.name,
                        it.returnType.javaType.typeName,
                        ArrayList(it.parameters.map { it.type.javaType.typeName }),
                        stack.getTopBlock()
                    )
                } catch (_: NotFoundException) {
                }
            }
        }
    } catch(_: KotlinReflectionInternalError) {
        popNClass()
    } catch(_: Exception) {
        popNClass()
    } finally {
        finishedProgress(name)
    }
    return nclass
}