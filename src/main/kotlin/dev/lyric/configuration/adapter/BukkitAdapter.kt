package dev.lyric.configuration.adapter

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import kotlin.reflect.KClass

@Suppress("unused", "UNCHECKED_CAST")
class BukkitAdapter<T : ConfigurationSerializable>(private val klass: KClass<T>): TypeAdapter<T> {

	@Suppress("OverrideOnly")
	override fun serialize(value: T): Any = value.serialize()

	override fun deserialize(raw: Any): T {
		val map = normalizeToMap(raw)
		return ConfigurationSerialization.deserializeObject(map, klass.java) as? T
			?: error("Failed to deserialize ${klass.java} from $map")
	}

}
