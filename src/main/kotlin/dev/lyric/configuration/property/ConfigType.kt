package dev.lyric.configuration.property

import kotlin.reflect.KClass

sealed interface ConfigType<T : Any> {
	val klass: KClass<*>
}

data class SimpleConfigType<T : Any>(override val klass: KClass<T>) : ConfigType<T> {
	companion object {
		inline operator fun <reified T : Any> invoke(): SimpleConfigType<T> = SimpleConfigType(T::class)
	}
}

data class ListConfigType<T : Any>(val elementType: ConfigType<T>) : ConfigType<List<T>> {
	override val klass: KClass<*> get() = List::class

	companion object {
		operator fun <T : Any> invoke(elementClass: KClass<T>): ListConfigType<T> =
			ListConfigType(SimpleConfigType<T>(elementClass))

		inline operator fun <reified T : Any> invoke(): ListConfigType<T> =
			ListConfigType(SimpleConfigType(T::class))
	}
}

data class SetConfigType<T : Any>(val elementType: ConfigType<T>) : ConfigType<Set<T>> {
	override val klass: KClass<*> get() = Set::class

	companion object {

		operator fun <T : Any> invoke(elementClass: KClass<T>): SetConfigType<T> =
			SetConfigType(SimpleConfigType(elementClass))

		inline operator fun <reified T : Any> invoke(): SetConfigType<T> =
			SetConfigType(SimpleConfigType(T::class))

	}

}

data class MapConfigType<K : Any, V : Any>(
	val keyType: ConfigType<K>,
	val valueType: ConfigType<V>,
) : ConfigType<Map<K, V>> {
	override val klass: KClass<*> get() = Map::class

	companion object {

		operator fun <V : Any> invoke(valueClass: KClass<V>): MapConfigType<String, V> =
			MapConfigType(SimpleConfigType(String::class), SimpleConfigType(valueClass))

		operator fun <V : Any> invoke(valueClass: ConfigType<V>): MapConfigType<String, V> =
			MapConfigType(SimpleConfigType(String::class), valueClass)

		inline operator fun <reified V : Any> invoke(): MapConfigType<String, V> =
			MapConfigType(SimpleConfigType(String::class), SimpleConfigType(V::class))

		operator fun <K : Any, V : Any> invoke(keyClass: KClass<K>, valueClass: KClass<V>): MapConfigType<K, V> =
			MapConfigType(SimpleConfigType(keyClass), SimpleConfigType(valueClass))

		inline operator fun <reified K : Any, reified V : Any> invoke(): MapConfigType<K, V> =
			MapConfigType(SimpleConfigType(K::class), SimpleConfigType(V::class))

	}

}
