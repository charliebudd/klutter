package dev.buijs.klutter.gradle.dsl


interface KlutterDSL<T> {
    fun configure(lambda: T.() -> Unit): KlutterDTO
}

interface KlutterDSLBuilder {
    fun build(): KlutterDTO
}

interface KlutterDTO