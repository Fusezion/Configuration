package dev.lyric.configuration.adapter

import org.bukkit.configuration.ConfigurationSection

internal object ConfigMapping {

	fun populateSection(section: ConfigurationSection, map: Map<String, Any?>) {
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

	fun deepSerializeSection(section: ConfigurationSection): Map<String, Any?> =
		section.getValues(false).mapValues { (_, value) -> deepSerializeValue(value) }

	private fun deepSerializeValue(value: Any?): Any? = when (value) {
		is ConfigurationSection -> deepSerializeSection(value)
		is Map<*, *> -> value.entries.associate { (k, v) -> k.toString() to deepSerializeValue(v) }
		is List<*> -> value.map { deepSerializeValue(it) }
		else -> value
	}
}
