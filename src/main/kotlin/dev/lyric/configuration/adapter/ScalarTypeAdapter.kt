package dev.lyric.configuration.adapter

import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST")
class ScalarTypeAdapter<Result : Any, Primitive : Any>(
	private val serialize: (Result) -> Primitive,
	private val deserialize: (Primitive) -> Result,
	private val rawType: KClass<Primitive>
) : TypeAdapter<Result> {

	override fun deserialize(raw: Any): Result {
		require(rawType.isInstance(raw)) {
			"Expected a ${rawType.simpleName} but got ${raw::class.simpleName} (value: '$raw')"
		}
		return deserialize(raw)
	}

	override fun serialize(value: Result): Any = serialize(value)

}