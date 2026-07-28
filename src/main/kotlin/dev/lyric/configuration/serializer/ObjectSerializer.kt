package dev.lyric.configuration.serializer

interface ObjectSerializer<T : Any> {
	fun serialize(value: T): Map<String, Any?>
	fun deserialize(map: Map<String, Any?>): T
}
