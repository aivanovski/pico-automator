package com.github.aivanovski.picoautomator.cli

import clojure.java.api.Clojure
import clojure.lang.Compiler
import java.io.StringReader

fun main() {
    val content = """
        (println (format "Hello world! %s" (+ 2 40)))
    """.trimIndent()

    Clojure.`var`("clojure.core", "require")
    Compiler.load(StringReader(content))
}