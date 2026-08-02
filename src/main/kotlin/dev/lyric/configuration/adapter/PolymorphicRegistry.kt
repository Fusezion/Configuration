package dev.lyric.configuration.adapter

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import kotlin.reflect.KClass

/**
 * A two-way mapping between a string discriminator (e.g. `"run_command"`) and a concrete [Config]
 * subclass, for families of types where the concrete type is only known once you've read a field
 * out of the config (GUI actions, conditions, etc). Pair this with [PolymorphicAdapter] to actually
 * plug the family into a [dev.lyric.configuration.property.ConfigType].
 *
 * Registration is explicit constructor references, no reflection involved:
 *
 * ```kotlin
 * object Actions : PolymorphicRegistry<Action>() {
 *     init {
 *         register("run_command", ::RunCommandAction)
 *         register("play_sound", ::PlaySoundAction)
 *         register("message", ::MessageAction)
 *     }
 * }
 *
 * TypeAdapterRegistry.register(Action::class, PolymorphicAdapter(Actions))
 * ```
 */
open class PolymorphicRegistry<Base : Config> {

	private class Entry<T : Config>(val id: String, val factory: (ConfigStorage) -> T)

	private val byId = mutableMapOf<String, Entry<out Base>>()
	private val byClass = mutableMapOf<KClass<out Base>, Entry<out Base>>()

	inline fun <reified T : Base> register(id: String, noinline factory: (ConfigStorage) -> T) {
		register(id, T::class, factory)
	}

	fun <T : Base> register(id: String, klass: KClass<T>, factory: (ConfigStorage) -> T) {
		require(id !in byId) { "'$id' is already registered" }
		require(klass !in byClass) { "${klass.simpleName} is already registered under id '${byClass.getValue(klass).id}'" }
		val entry = Entry(id, factory)
		byId[id] = entry
		byClass[klass] = entry
	}

	@Suppress("UNCHECKED_CAST")
	fun factoryFor(id: String): (ConfigStorage) -> Base =
		(byId[id] ?: error("Unknown type '$id'. Known types: ${byId.keys.sorted()}")).factory as (ConfigStorage) -> Base

	fun idFor(klass: KClass<out Base>): String =
		byClass[klass]?.id ?: error("${klass.simpleName} is not registered in this PolymorphicRegistry")

	fun ids(): Set<String> = byId.keys.toSet()
}
