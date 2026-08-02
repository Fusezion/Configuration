package dev.lyric.configuration

import dev.lyric.configuration.property.ConfigType
import dev.lyric.configuration.property.DefaultConfigProperty
import dev.lyric.configuration.property.NullableConfigProperty
import dev.lyric.configuration.property.RequiredConfigProperty
import dev.lyric.configuration.property.SectionConfigProperty
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

abstract class Config(protected val storage: ConfigStorage) {

	internal fun backingStorage(): ConfigStorage = storage

	fun <T : Any> require(
		path: String,
		configType: ConfigType<T>,
		exceptionMessage: () -> String = { "Missing required config value at path '$path'" }
	): ReadWriteProperty<Config, T> = RequiredConfigProperty(storage, path, configType, exceptionMessage)

	fun <T : Any> nullable(
		path: String,
		configType: ConfigType<T>
	): ReadWriteProperty<Config, T?> = NullableConfigProperty(storage, path, configType)

	fun <T : Any> default(
		path: String,
		configType: ConfigType<T>,
		default: T
	): ReadWriteProperty<Config, T> = DefaultConfigProperty(storage, path, configType, default)

	fun <T : Config> section(path: String, factory: (ConfigStorage) -> T): ReadOnlyProperty<Config, T> =
		SectionConfigProperty(storage, path, factory)

}