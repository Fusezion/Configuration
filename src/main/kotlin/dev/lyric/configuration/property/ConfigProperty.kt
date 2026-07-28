package dev.lyric.configuration.property

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import dev.lyric.configuration.adapter.TypeAdapterRegistry
import dev.lyric.configuration.exception.ConfigException
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

sealed class ConfigProperty<T : Any>(
	protected val storage: ConfigStorage,
	val path: String,
	val configType: ConfigType<T>
) {
	protected val adapter by lazy { TypeAdapterRegistry.resolve(configType) }

	protected fun deserializeOrThroe(raw: Any): T = try {
		adapter.deserialize(raw)
	} catch (e: Exception) {
		throw ConfigException("Invalid config value at path '$path' (raw value: $raw): ${e.message}", e)
	}

	protected fun serializeOrThrow(value: T): Any = try {
		adapter.serialize(value)
	} catch (e: Exception) {
		throw ConfigException("Failed to write config value at path '$path': ${e.message}", e)
	}

}

class RequiredConfigProperty<T : Any>(
	storage: ConfigStorage,
	path: String,
	configType: ConfigType<T>,
	private val exceptionMessage: () -> String
) : ConfigProperty<T>(storage, path, configType), ReadWriteProperty<Config, T> {

	override fun getValue(thisRef: Config, property: KProperty<*>): T {
		val raw = storage.get(path) ?: throw ConfigException(exceptionMessage())
		return deserializeOrThroe(raw)
	}

	override fun setValue(thisRef: Config, property: KProperty<*>, value: T) {
		storage.set(path, serializeOrThrow(value))
	}
}

class NullableConfigProperty<T : Any>(
	storage: ConfigStorage,
	path: String,
	configType: ConfigType<T>
) : ConfigProperty<T>(storage, path, configType), ReadWriteProperty<Config, T?> {

	override fun getValue(thisRef: Config, property: KProperty<*>): T? {
		val raw = storage.get(path) ?: return null
		return deserializeOrThroe(raw)
	}

	override fun setValue(thisRef: Config, property: KProperty<*>, value: T?) {
		storage.set(path, value?.let { serializeOrThrow(it) })
	}
}

class DefaultConfigProperty<T : Any>(
	storage: ConfigStorage,
	path: String,
	configType: ConfigType<T>,
	val default: T
) : ConfigProperty<T>(storage, path, configType), ReadWriteProperty<Config, T> {

	override fun getValue(thisRef: Config, property: KProperty<*>): T {
		val raw = storage.get(path) ?: return default
		return deserializeOrThroe(raw)
	}

	override fun setValue(thisRef: Config, property: KProperty<*>, value: T) {
		storage.set(path, serializeOrThrow(value))
	}
}

class SectionConfigProperty<T : Config>(
	storage: ConfigStorage,
	path: String,
	factory: (ConfigStorage) -> T
) : ReadOnlyProperty<Config, T> {

	private val value: T = factory(storage.section(path))

	override fun getValue(thisRef: Config, property: KProperty<*>): T = value

}
