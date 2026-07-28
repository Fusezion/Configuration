package dev.lyric.configuration.adapter

interface TypeAdapter<T : Any> {
	fun serialize(value: T): Any
	fun deserialize(raw: Any): T
}