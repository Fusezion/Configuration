package dev.lyric.configuration

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class FileConfig<T : Config> private constructor(
	private val file: File,
	private val resourcePath: String?,
	private val classLoader: ClassLoader?,
	private val factory: (ConfigStorage) -> T
) {

	lateinit var config: T; private set
	var isLoaded: Boolean = false; private set

	private lateinit var configuration: YamlConfiguration

	fun load() {
		if (!file.exists()) {
			file.parentFile?.mkdirs()
			val resourceStream = resourcePath?.let {
				requireNotNull(classLoader) { "resourcePath was given but no classLoader was provided" }
					.getResourceAsStream(it)
			}
			if (resourceStream != null) {
				resourceStream.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
			} else {
				file.createNewFile()
			}
		}
		configuration = YamlConfiguration.loadConfiguration(file)
		config = factory(ConfigStorage(configuration))
		isLoaded = true
	}

	fun save() {
		check(isLoaded) { "Cannot save before load() has been called" }
		configuration.save(file)
	}

	companion object {

		fun <T : Config> of(file: File, factory: (ConfigStorage) -> T): FileConfig<T> =
			FileConfig(file, null, null, factory)

		fun <T : Config> of(
			file: File,
			resourcePath: String,
			classLoader: ClassLoader,
			factory: (ConfigStorage) -> T
		): FileConfig<T> = FileConfig(file, resourcePath, classLoader, factory)

	}

}