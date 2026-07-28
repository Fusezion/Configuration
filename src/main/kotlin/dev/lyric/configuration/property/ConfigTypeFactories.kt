package dev.lyric.configuration.property

import kotlin.reflect.KClass

// Simple Config Type
inline fun <reified T : Any> configType(): ConfigType<T> = SimpleConfigType(T::class)
fun <T : Any> configType(klass: KClass<T>): ConfigType<T> = SimpleConfigType(klass)

// List Config Type
inline fun <reified T : Any> listConfigType(): ConfigType<List<T>> = ListConfigType(T::class)
fun <T : Any> listConfigType(elementClass: KClass<T>): ConfigType<List<T>> = ListConfigType(elementClass)
fun <T : Any> listConfigType(elementType: ConfigType<T>): ConfigType<List<T>> = ListConfigType(elementType)

// Set Config Type
inline fun <reified T : Any> setConfigType(): ConfigType<Set<T>> = SetConfigType(T::class)
fun <T : Any> setConfigType(elementClass: KClass<T>): ConfigType<Set<T>> = SetConfigType(elementClass)
fun <T : Any> setConfigType(elementType: ConfigType<T>): ConfigType<Set<T>> = SetConfigType(elementType)

// Map Config Type
fun <V : Any> mapConfigType(valueClass: KClass<V>): ConfigType<Map<String, V>> = MapConfigType(valueClass)
fun <V : Any> mapConfigType(valueType: ConfigType<V>): ConfigType<Map<String, V>> = MapConfigType(valueType)

fun <K : Any, V : Any> mapConfigType(keyClass: KClass<K>, valueClass: KClass<V>): ConfigType<Map<K, V>> = MapConfigType(keyClass, valueClass)
fun <K : Any, V : Any> mapConfigType(keyType: ConfigType<K>, valueType: ConfigType<V>): ConfigType<Map<K, V>> = MapConfigType(keyType, valueType)