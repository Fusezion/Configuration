package dev.lyric.configuration

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.MemoryConfiguration

class ConfigStorage internal constructor(
	private val section: ConfigurationSection,
	private val pathPrefix: String = ""
) {
	private fun resolvedPath(path: String): String = if (pathPrefix.isEmpty()) path else "$pathPrefix.$path"

	fun get(path: String): Any? = section.get(resolvedPath(path))

	fun set(path: String, value: Any?) {
		section.set(resolvedPath(path), value)
	}

	fun contains(path: String): Boolean = section.contains(resolvedPath(path))

	fun section(path: String): ConfigStorage = ConfigStorage(section, resolvedPath(path))

	fun raw(): ConfigurationSection = section

	companion object {
		fun memory(): ConfigStorage = ConfigStorage(MemoryConfiguration())
	}

}
