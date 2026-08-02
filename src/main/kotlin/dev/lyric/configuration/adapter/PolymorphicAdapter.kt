package dev.lyric.configuration.adapter

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import org.bukkit.configuration.MemoryConfiguration

/**
 * Adapts a *family* of [Config] subclasses that share a common supertype, dispatching to the right
 * one based on a discriminator field (`type` by default) read out of the raw value. This is what
 * powers things like GUI action lists, where each list element can be a different concrete type:
 *
 * ```yaml
 * my-actions:
 *   LEFT:
 *     - type: 'run_command'
 *       command: "spawn"
 *     - type: 'play_sound'
 *       sound: 'minecraft:entity.item.pickup'
 *       volume: 0.5
 * ```
 *
 * Usage:
 * ```kotlin
 * object Actions : PolymorphicRegistry<Action>() {
 *     init {
 *         register("run_command", ::RunCommandAction)
 *         register("play_sound", ::PlaySoundAction)
 *     }
 * }
 *
 * TypeAdapterRegistry.register(Action::class, PolymorphicAdapter(Actions))
 *
 * // elsewhere, completely unremarkable - just a normal Config-backed type:
 * val myActions: Map<ClickType, List<Action>> by require(
 *     "my-actions",
 *     mapConfigType(ClickType::class, listConfigType(Action::class))
 * )
 * ```
 *
 * The discriminator field itself is stripped before the concrete class sees the rest of the data,
 * so subclasses don't need (and shouldn't declare) a `type`/`by require("type", ...)` property of
 * their own - it's metadata for dispatch, not part of the type's own schema.
 */
@Suppress("unused")
class PolymorphicAdapter<Base : Config>(
	private val registry: PolymorphicRegistry<Base>,
	private val typeKey: String = "type"
) : TypeAdapter<Base> {

	override fun deserialize(raw: Any): Base {
		val map = normalizeToMap(raw)
		val id = map[typeKey]?.toString()
			?: error("Missing '$typeKey' in $map. Known types: ${registry.ids().sorted()}")

		val section = MemoryConfiguration()
		ConfigMapping.populateSection(section, map - typeKey)
		return registry.factoryFor(id)(ConfigStorage(section))
	}

	override fun serialize(value: Base): Any {
		val id = registry.idFor(value::class)
		val body = ConfigMapping.deepSerializeSection(value.backingStorage().snapshotSection())
		return linkedMapOf<String, Any?>(typeKey to id).apply { putAll(body) }
	}
}
