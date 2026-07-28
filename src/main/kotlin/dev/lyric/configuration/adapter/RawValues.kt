package dev.lyric.configuration.adapter

import org.bukkit.configuration.ConfigurationSection

internal fun normalizeToMap(raw: Any): Map<String, Any?> = when (raw) {
	is ConfigurationSection -> raw.getValues(false)
	is Map<*, *> -> raw.entries.associate { (key, value) -> key.toString() to value }
	else -> error("Expected a map or configuration section but got ${raw::class.simpleName}")
}