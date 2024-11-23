package org.j2c

import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import javassist.bytecode.Mnemonic
import org.j2c.assembly.NClass
import org.j2c.assembly.NFieldDeclaration
import org.j2c.assembly.NMethodDeclaration
import org.j2c.assembly.Node
import org.j2c.assembly.clearNClasses
import org.j2c.assembly.popNClass
import org.j2c.assembly.rules.NoRule
import org.j2c.assembly.rules.Rule
import org.j2c.assembly.rules.RuleContainer
import org.j2c.development.registerUnknownOpcode
import org.j2c.exceptions.InfiniteLoopException
import org.j2c.exceptions.UnknownOpcodeException
import org.j2c.llvm.LLVM
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.net.URLClassLoader
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError

val rules = arrayListOf<Rule>()
lateinit var classLoader: URLClassLoader
val pool = ClassPool(ClassPool.getDefault())

private val scheduled = mutableSetOf<String>()
fun schedule(name: String) = scheduled.add(name)

private val alreadyParsed = mutableSetOf<String>()
fun isAlreadyParsed(name: String) = alreadyParsed.contains(name)
fun beginProgress(name: String) = scheduled.remove(name)
fun finishedProgress(name: String) = alreadyParsed.add(name)

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

                    // Catch infinite loops (indicates a big problem)
                    val alreadyVisitedPositions = mutableSetOf<Int>()

                    val stack = Stack<Node>()
                    while (instructions.hasNext()) {
                        val pos = instructions.next()
                        val opcode = instructions.byteAt(pos)

                        if(alreadyVisitedPositions.contains(pos)) {
                            InfiniteLoopException().printStackTrace()
                            break
                        }
                        alreadyVisitedPositions.add(pos)

                        rules.find { it.opcode == opcode }?.predicate?.invoke(instructions, pos, const, vars, stack)
                            ?: run {
                                UnknownOpcodeException(Mnemonic.OPCODE[opcode]).printStackTrace()
                                registerUnknownOpcode(Mnemonic.OPCODE[opcode])
                            }
                    }
                    NMethodDeclaration(
                        nclass,
                        it.name,
                        it.returnType.javaType.typeName,
                        ArrayList(it.parameters.map { it.type.javaType.typeName }),
                        stack.toList() as ArrayList<Node>
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
fun init(path: String) {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages("org.j2c.assembly.rules")
            .filterInputsBy(FilterBuilder().includePackage("org.j2c.assembly.rules"))
            .setScanners(SubTypesScanner(false))
    )
    val classes = reflections.getSubTypesOf(Any::class.java)
    classes.map { it.kotlin }.filter { it.hasAnnotation<RuleContainer>() }.forEach { clazz ->
        val properties = clazz.declaredMemberProperties
        properties.filter { !it.hasAnnotation<NoRule>() }.forEach { prop ->
            rules.add((prop as KProperty1<Any?, Rule>).get(clazz.objectInstance))
        }
    }

    classLoader = URLClassLoader(arrayOf(File(path).toURI().toURL()), ClassLoader.getSystemClassLoader())
    pool.appendClassPath(path)
}
fun parseAndRunForEachClass(firstClassName: String, predicate: (NClass) -> Unit) {
    schedule(firstClassName)
    while(scheduled.size > 0) {
        val currentScheduled = scheduled.toMutableSet()
        scheduled.clear()
        currentScheduled.forEach {
            val v = parse(it)
            if(v != null) predicate.invoke(v)
        }
        clearNClasses()
    }
}
fun main(args: Array<String>) {
    init(args[0])
    parseAndRunForEachClass(args[1]) {
        LLVM.createAST(it)
        LLVM.compileCurrentAST()
    }
}