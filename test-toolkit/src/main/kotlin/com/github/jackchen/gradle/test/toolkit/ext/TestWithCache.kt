package com.github.jackchen.gradle.test.toolkit.ext

@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TestWithCache(val value: Boolean = false)
