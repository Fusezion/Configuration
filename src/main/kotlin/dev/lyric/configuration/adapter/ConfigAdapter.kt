package dev.lyric.configuration.adapter

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration

@Suppress("unused")
class ConfigAdapter<T : Config>(
	private val factory: (ConfigStorage) -> T
) : TypeAdapter<T> {

	override fun serialize(value: T): Any =
		deepSerializeSection(value.backingStorage().snapshotSection())

	override fun deserialize(raw: Any): T {
		val map = normalizeToMap(raw)
		val section = MemoryConfiguration()
		populateSection(section, map)
		return factory(ConfigStorage(section))
	}

	companion object {

		private fun populateSection(section: ConfigurationSection, map: Map<String, Any?>) {
			map.forEach { (key, rawValue) ->
				when (rawValue) {
					is Map<*, *> -> populateSection(
						section.createSection(key),
						rawValue.entries.associate { (k, v) -> k.toString() to v }
					)

					else -> section.set(key, rawValue)
				}
			}
		}

		private fun deepSerializeSection(section: ConfigurationSection): Map<String, Any?> =
			section.getValues(false).mapValues { (_, value) -> deepSerializeValue(value) }

		private fun deepSerializeValue(value: Any?): Any? = when (value) {
			is ConfigurationSection -> deepSerializeSection(value)
			is Map<*, *> -> value.entries.associate { (k, v) -> k.toString() to deepSerializeValue(v) }
			is List<*> -> value.map { deepSerializeValue(it) }
			else -> value
		}
	}
}
