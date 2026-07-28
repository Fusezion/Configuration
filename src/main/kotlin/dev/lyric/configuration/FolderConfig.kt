package dev.lyric.configuration

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class FolderConfig<T : Config> private constructor(
	private val folder: File,
	private val resourcePaths: List<String>,
	private val classLoader: ClassLoader?,
	private val factory: (ConfigStorage) -> T
) {

	private class Entry<T : Config>(val file: File, val configuration: YamlConfiguration, val config: T)

	private val entries = mutableMapOf<String, Entry<T>>()

	var isLoaded: Boolean = false; private set

	private fun keyFor(file: File): String {
		val relative = file.relativeTo(folder).path.replace(File.separatorChar, '/')
		return relative.substringBefore('.')
	}

	private fun sendFromResources() {
		if (resourcePaths.isEmpty()) return
		requireNotNull(classLoader) { "resourcePaths was given but no classLoader was provided" }
		resourcePaths.forEach { resourcePath ->
			val resourceStream = classLoader.getResourceAsStream(resourcePath)
				?: error("Bundled rsource '$resourcePath' not found")
			val destination = File(folder, resourcePath.substringAfter('/'))
			destination.parentFile?.mkdirs()
			resourceStream.use { input -> destination.outputStream().use { output -> input.copyTo(output) } }
		}
	}

	fun load() {
		if (!folder.exists()) {
			folder.mkdirs()
			sendFromResources()
		}
		entries.clear()
		folder.walkTopDown()
			.filter { it.isFile && it.extension.lowercase() in YAML_EXTENSIONS }
			.forEach { file ->
				val key = keyFor(file)
				val configuration = YamlConfiguration.loadConfiguration(file)
				val config = factory(ConfigStorage(configuration))
				entries[key] = Entry(file, configuration, config)
			}
		isLoaded = true
	}

	fun save() {
		check(isLoaded) { "Cannot save before load() has been called" }
		entries.values.forEach { it.configuration.save(it.file) }
	}

	fun keys(): Set<String> = entries.keys.toSet()

	fun config(key: String): T? = entries[key]?.config

	fun all(): Map<String, T> = entries.mapValues { it.value.config }

	companion object {
		private val YAML_EXTENSIONS = setOf("yml", "yaml")

		fun <T : Config> of(folder: File, factory: (ConfigStorage) -> T): FolderConfig<T> =
			FolderConfig(folder, emptyList(), null, factory)

		fun <T : Config> of(
			folder: File,
			resourcePaths: List<String>,
			classLoader: ClassLoader,
			factory: (ConfigStorage) -> T
		): FolderConfig<T> = FolderConfig(folder, resourcePaths, classLoader, factory)

	}

}