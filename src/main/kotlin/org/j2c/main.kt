package org.j2c

import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import javassist.bytecode.Mnemonic
import javassist.bytecode.Opcode
import org.j2c.assembly.NClass
import org.j2c.assembly.findNClassByFullName
import org.j2c.assembly.getClasses
import org.j2c.assembly.popNClass
import org.j2c.assembly.rules.Rule
import org.j2c.assembly.rules.RuleContainer
import org.j2c.development.registerUnknownOpcode
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
import kotlin.reflect.jvm.javaField

val rules = arrayListOf<Rule>()
lateinit var classLoader: URLClassLoader
val pool = ClassPool()

@OptIn(ExperimentalStdlibApi::class)
fun parse(name: String): NClass? {
    try {
        val kclass: KClass<*>
        val nclass: NClass
        val ctclass: CtClass

        kclass = classLoader.loadClass(name).kotlin
        nclass = NClass(kclass.qualifiedName!!, kclass.simpleName!!)
        ctclass = pool.get(name)

        kclass.members.forEach {
            if(it is KProperty) {
                nclass.NFieldDeclaration(it.name, (it.javaField?.type ?: it.returnType.javaType).typeName)
            } else if(it is KFunction) {
                try {
                    // Process method code
                    val methodInfo = ctclass.getDeclaredMethod(it.name).methodInfo
                    val instructions = methodInfo.codeAttribute.iterator()
                    val const = methodInfo.constPool
                    val vars = mutableMapOf<Int, String>()
                    it.parameters.forEachIndexed { i: Int, v: KParameter -> vars.put(i, "param$i") }

                    val stack = Stack<String>()
                    while (instructions.hasNext()) {
                        val pos = instructions.next()
                        val opcode = instructions.byteAt(pos)

                        rules.find { it.opcode == opcode }?.predicate?.invoke(instructions, pos, const, vars, stack) ?: run {
                            UnknownOpcodeException(Mnemonic.OPCODE[opcode]).printStackTrace()
                            registerUnknownOpcode(Mnemonic.OPCODE[opcode])
                        }
                    }
                    nclass.NMethodDeclaration(it.name, it.returnType.javaType.typeName, it.parameters.map { it.type.javaType.typeName }, stack.toList())
                } catch(_: NotFoundException) {}
            }
        }
        return nclass
    } catch(_: Exception) {
        popNClass()
        return null
    }
}
fun init(path: String) {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages("org.j2c.assembly.rules")
            .filterInputsBy(FilterBuilder().includePackage("org.j2c.assembly.rules"))
            .setScanners(SubTypesScanner(false))
    )
    val classes = reflections.getSubTypesOf(Any::class.java)
    classes.filter { it.isAnnotationPresent(RuleContainer::class.java) }.forEach { clazz ->
        val properties = clazz.kotlin.declaredMemberProperties
        properties.forEach { prop ->
            rules.add((prop as KProperty1<Any?, Rule>).get(clazz.kotlin.objectInstance))
        }
    }

    classLoader = URLClassLoader(arrayOf(File(path).toURI().toURL()), ClassLoader.getSystemClassLoader())
    pool.appendClassPath(path)
}
fun main(args: Array<String>) {
    init(args[0])
    parse(args[1])
    LLVM.beginCodeGen()
    getClasses().forEach {
        LLVM.createAST(it)
        LLVM.generateCurrentAST()
    }
    LLVM.finishCodeGen()
}