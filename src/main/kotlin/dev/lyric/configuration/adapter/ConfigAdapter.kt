package dev.lyric.configuration.adapter

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import org.bukkit.configuration.MemoryConfiguration

@Suppress("unused")
class ConfigAdapter<T : Config>(
	private val factory: (ConfigStorage) -> T
) : TypeAdapter<T> {

	override fun serialize(value: T): Any =
		ConfigMapping.deepSerializeSection(value.backingStorage().snapshotSection())

	override fun deserialize(raw: Any): T {
		val map = normalizeToMap(raw)
		val section = MemoryConfiguration()
		ConfigMapping.populateSection(section, map)
		return factory(ConfigStorage(section))
	}
}
