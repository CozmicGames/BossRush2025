package com.cozmicgames.utils

import kotlin.reflect.KClass

fun <T : Any> getClassName(cls: KClass<T>) = cls.js.name

fun getClassByName(name: String): KClass<*>? {
    return (name.asDynamic() as? JsClass<*>)?.kotlin
}

fun <T : Any> createInstance(cls: KClass<T>): T? {
    val ctor = cls.js.asDynamic()
    return js("new ctor()") as T
}

fun <T : Any> getSupplier(cls: KClass<T>): (() -> T)? {
    val ctor = cls.js.asDynamic()
    return { js("new ctor()") as T }
}
