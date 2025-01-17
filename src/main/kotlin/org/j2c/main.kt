package org.j2c

import javassist.ClassPool
import org.j2c.ast.rules.api.NoRule
import org.j2c.ast.rules.api.Rule
import org.j2c.ast.rules.api.RuleContainer
import org.j2c.llvm.LLVM
import org.j2c.parsing.parseAndRunForEachClass
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.net.URLClassLoader
import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

val rules = arrayListOf<Rule>()
lateinit var classLoader: URLClassLoader
val pool = ClassPool(ClassPool.getDefault())

fun init(path: String) {
    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackages("org.j2c.ast.rules")
            .filterInputsBy(FilterBuilder().includePackage("org.j2c.ast.rules"))
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
    parseAndRunForEachClass(args[1]) {
        LLVM.createAST(it)
        LLVM.compileCurrentAST()
    }
}