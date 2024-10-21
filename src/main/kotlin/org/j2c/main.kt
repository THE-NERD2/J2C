package org.j2c

import javassist.ClassPool
import javassist.NotFoundException
import javassist.bytecode.Mnemonic
import org.j2c.assembly.NClass
import org.j2c.assembly.NFieldDeclaration
import org.j2c.assembly.NMethodDeclaration
import org.j2c.assembly.Node
import org.j2c.assembly.getClasses
import org.j2c.assembly.popNClass
import org.j2c.assembly.rules.NoRule
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
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaField

val rules = arrayListOf<Rule>()
lateinit var classLoader: URLClassLoader
val pool = ClassPool()

@OptIn(ExperimentalStdlibApi::class)
fun parse(name: String): NClass? {
    try {
        val kclass = classLoader.loadClass(name).kotlin
        val nclass = NClass(kclass.qualifiedName!!, kclass.simpleName!!)
        val ctclass = pool.get(name)

        kclass.members.forEach {
            if(it is KProperty) {
                NFieldDeclaration(nclass, it.name, (it.javaField?.type ?: it.returnType.javaType).typeName)
            } else if(it is KFunction) {
                try {
                    // Process method code
                    val methodInfo = ctclass.getDeclaredMethod(it.name).methodInfo
                    val instructions = methodInfo.codeAttribute.iterator()
                    val const = methodInfo.constPool
                    val vars = mutableMapOf<Int, String>()
                    it.parameters.forEachIndexed { i: Int, v: KParameter -> vars[i] = "param$i" }

                    val stack = Stack<Node>()
                    while (instructions.hasNext()) {
                        val pos = instructions.next()
                        val opcode = instructions.byteAt(pos)

                        rules.find { it.opcode == opcode }?.predicate?.invoke(instructions, pos, const, vars, stack) ?: run {
                            UnknownOpcodeException(Mnemonic.OPCODE[opcode]).printStackTrace()
                            registerUnknownOpcode(Mnemonic.OPCODE[opcode])
                        }
                    }
                    NMethodDeclaration(nclass, it.name, it.returnType.javaType.typeName, ArrayList(it.parameters.map { it.type.javaType.typeName }), stack.toList() as ArrayList<Node>)
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
    classes.map { it.kotlin }.filter { it.hasAnnotation<RuleContainer>() }.forEach { clazz ->
        val properties = clazz.declaredMemberProperties
        properties.filter { !it.hasAnnotation<NoRule>() }.forEach { prop ->
            rules.add((prop as KProperty1<Any?, Rule>).get(clazz.objectInstance))
        }
    }

    classLoader = URLClassLoader(arrayOf(File(path).toURI().toURL()), ClassLoader.getSystemClassLoader())
    pool.appendClassPath(path)
}
fun main(args: Array<String>) {
    init(args[0])
    parse(args[1])
    getClasses().forEach(LLVM::createAST)
    LLVM.finishCodeGen()
}