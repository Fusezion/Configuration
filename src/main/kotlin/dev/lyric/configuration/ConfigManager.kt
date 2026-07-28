package dev.lyric.configuration

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Suppress("UNCHECKED_CAST", "unused")
class ConfigManager(private val plugin: JavaPlugin) {

	private val files = mutableMapOf<String, FileConfig<*>>()
	private val folders = mutableMapOf<String, FolderConfig<*>>()

	private val classLoader get() = plugin::class.java.classLoader

	fun <T : Config> addFile(codename: String, file: FileConfig<T>) {
		files[codename] = file
	}

	fun <T : Config> addFile(codename: String, file: File, factory: (ConfigStorage) -> T): FileConfig<T> {
		val resolvedFile = resolveAgainstDataFolder(file)
		val resourcePath = file.path.replace(File.separatorChar, '/')
		val fileConfig = if (classLoader.getResource(resourcePath) != null) {
			FileConfig.of(resolvedFile, resourcePath, classLoader, factory)
		} else {
			FileConfig.of(resolvedFile, factory)
		}
		files[codename] = fileConfig
		return fileConfig
	}

	fun <T : Config> addFolder(codename: String, file: FolderConfig<T>) {
		folders[codename] = file
	}

	fun <T : Config> addFolder(codename: String, folder: File, factory: (ConfigStorage) -> T): FolderConfig<T> {
		val resolvedFolder = resolveAgainstDataFolder(folder)
		val folderConfig = FolderConfig.of(resolvedFolder, factory)
		folders[codename] = folderConfig
		return folderConfig
	}

	fun <T : Config> addFolder(codename: String, folder: File, resourcePaths: List<String>, factory: (ConfigStorage) -> T): FolderConfig<T> {
		val resolvedFolder = resolveAgainstDataFolder(folder)
		val folderConfig = FolderConfig.of(resolvedFolder, resourcePaths, classLoader, factory)
		folders[codename] = folderConfig
		return folderConfig
	}

	private fun resolveAgainstDataFolder(file: File): File =
		if (file.isAbsolute) file else File(plugin.dataFolder, file.path)

	fun loadAll() {
		files.values.forEach { it.load() }
		folders.values.forEach { it.load() }
	}

	fun saveAll() {
		files.values.filter { it.isLoaded }.forEach { it.save() }
		folders.values.filter { it.isLoaded }.forEach { it.save() }
	}

	fun <T : Config> getFile(codename: String): FileConfig<T>? = files[codename] as? FileConfig<T>

	fun <T : Config> getFolder(codename: String): FolderConfig<T>? = folders[codename] as? FolderConfig<T>

	fun <T : Config> getFileConfig(codename: String): T? {
		val fileConfig = getFile<T>(codename) ?: return null
		return if (fileConfig.isLoaded) fileConfig.config else null
	}

	fun <T : Config> getFolderConfig(codename: String, child: String): T? = getFolder<T>(codename)?.config(child)

}