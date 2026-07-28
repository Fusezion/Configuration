package dev.lyric.configuration.adapter

@Suppress("unused", "UNCHECKED_CAST")
class PrimitiveAdapter<T : Any> private constructor(
	private val serializeFn: (T) -> Any,
	private val deserializeFn: (Any) -> T,
): TypeAdapter<T> {

	override fun serialize(value: T): Any = serializeFn(value)
	override fun deserialize(raw: Any): T = deserializeFn(raw)

	companion object {

		val STRING: TypeAdapter<String> = PrimitiveAdapter(
			{ it },
			{ it.toString() }
		)

		val CHAR: TypeAdapter<Char> = PrimitiveAdapter(
			{ it.toString() },
			{ it.toString().also { s -> require(s.isNotEmpty()) { "Expected a single character but got an empty string" } }[0] }
		)

		val BOOLEAN: TypeAdapter<Boolean> = PrimitiveAdapter(
			{ it },
			{ it as? Boolean ?: it.toString().toBooleanStrictOrNull() ?: error("Expected a boolean but got: '$it'") }
		)

		val BYTE: TypeAdapter<Byte> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toByte() ?: it.toString().toByteOrNull() ?: error("Expected a byte but got: '$it'") }
		)

		val SHORT: TypeAdapter<Short> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toShort() ?: it.toString().toShortOrNull() ?: error("Expected a short but got: '$it'") }
		)

		val INT: TypeAdapter<Int> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toInt() ?: it.toString().toIntOrNull() ?: error("Expected an integer but got: '$it'") }
		)

		val LONG: TypeAdapter<Long> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toLong() ?: it.toString().toLongOrNull() ?: error("Expected a long but got: '$it'") }
		)

		val FLOAT: TypeAdapter<Float> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toFloat() ?: it.toString().toFloatOrNull() ?: error("Expected a float but got: '$it'") }
		)

		val DOUBLE: TypeAdapter<Double> = PrimitiveAdapter(
			{ it },
			{ (it as? Number)?.toDouble() ?: it.toString().toDoubleOrNull() ?: error("Expected a double but got: '$it'") }
		)

	}
}