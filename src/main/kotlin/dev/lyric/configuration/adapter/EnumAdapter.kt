package dev.lyric.configuration.adapter

import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST")
class EnumAdapter<T : Any>(private val klass: KClass<T>): TypeAdapter<T> {

	private val constants: Array<out Enum<*>> = klass.java.enumConstants as? Array<out Enum<*>>
		?: error("${klass.simpleName} is not an enum type")

	override fun serialize(value: T): Any = (value as Enum<*>).name

	override fun deserialize(raw: Any): T {
		val name = raw.toString()
		val match = constants.firstOrNull { it.name.equals(name, true) }
			?: error("No enum constantn amed '$name for ${klass.simpleName}")
		return match as T
	}

}
