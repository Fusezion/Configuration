package dev.lyric.configuration.adapter

import dev.lyric.configuration.serializer.ObjectSerializer
import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST")
class SerializerAdapter<T : Any>(
	private val serializer: ObjectSerializer<T>
): TypeAdapter<T> {

	override fun serialize(value: T): Any = serializer.serialize(value)

	override fun deserialize(raw: Any): T = serializer.deserialize(normalizeToMap(raw))

	companion object {
		fun <T : Any> register(klass: KClass<T>, serializer: ObjectSerializer<T>) {
			TypeAdapterRegistry.register(klass, SerializerAdapter(serializer))
		}

		internal fun <T : Any> findCompanionSerializer(klass: KClass<T>): ObjectSerializer<T>? = try {
			klass.java.getDeclaredField("Companion")
				.apply { isAccessible = true }
				.get(null) as? ObjectSerializer<T>
		} catch (_: NoSuchFieldException) {
			null
		}
	}
}