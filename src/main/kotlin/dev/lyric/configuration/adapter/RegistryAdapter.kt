package dev.lyric.configuration.adapter

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import org.bukkit.Keyed
import org.bukkit.Registry
import kotlin.reflect.KClass

@Suppress("unused")
class RegistryAdapter<T : Keyed>(
	private val registryKey: RegistryKey<T>,
	private val klass: KClass<T>
): TypeAdapter<T> {

	private val registry: Registry<T> by lazy { RegistryAccess.registryAccess().getRegistry(registryKey) }

	override fun serialize(value: T): Any = value.key.toString()

	override fun deserialize(raw: Any): T {
		val rawString = raw.toString()
		if (!Key.parseable(rawString))
			error("Invalid namespaced key '$rawString' for ${klass.simpleName}")
		val key = Key.key(rawString)
		return registry[key] ?: error("No ${klass.simpleName} registered under key '$key'")
	}

}
