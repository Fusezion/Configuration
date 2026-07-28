@file:Suppress("unused", "UNCHECKED_CAST")

package dev.lyric.configuration.adapter

class ListAdapter<T : Any>(private val elementAdapter: TypeAdapter<T>) : TypeAdapter<List<T>> {

	override fun serialize(value: List<T>): Any = value.map { elementAdapter.serialize(it) }

	override fun deserialize(raw: Any): List<T> {
		val list = raw as? List<*> ?: error("Expected a list but got ${raw::class.simpleName}")
		return list.map { elementAdapter.deserialize(it as Any) }
	}
}

class SetAdapter<T : Any>(private val elementAdapter: TypeAdapter<T>) : TypeAdapter<Set<T>> {

	override fun serialize(value: Set<T>): Any = value.map { elementAdapter.serialize(it) }

	override fun deserialize(raw: Any): Set<T> {
		val list = raw as? List<*> ?: error("Expected a list but got ${raw::class.simpleName}")
		return list.map { elementAdapter.deserialize(it as Any) }.toSet()
	}

}

class MapAdapter<K : Any, V : Any>(
	private val keyAdapter: TypeAdapter<K>,
	private val valueAdapter: TypeAdapter<V>
) : TypeAdapter<Map<K, V>> {


	override fun serialize(value: Map<K, V>): Any =
		value.entries.associate { (key, value) -> keyAdapter.serialize(key).toString() to valueAdapter.serialize(value) }

	override fun deserialize(raw: Any): Map<K, V> {
		val map = normalizeToMap(raw)
		return map.entries.associate { (rawKey, rawValue) ->
			val value = rawValue ?: error("Null value for key '$rawKey' in map")
			keyAdapter.deserialize(rawKey) to valueAdapter.deserialize(value)
		}
	}

}
