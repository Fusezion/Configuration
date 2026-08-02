@file:Suppress("UNCHECKED_CAST", "UnstableApiUsage")

package dev.lyric.configuration.adapter

import dev.lyric.configuration.Config
import dev.lyric.configuration.ConfigStorage
import dev.lyric.configuration.property.ConfigType
import dev.lyric.configuration.property.ListConfigType
import dev.lyric.configuration.property.MapConfigType
import dev.lyric.configuration.property.SetConfigType
import dev.lyric.configuration.property.SimpleConfigType
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import java.util.UUID
import kotlin.reflect.KClass

private object MissingAdapter

object TypeAdapterRegistry {

	private val cache = mutableMapOf<KClass<*>, Any>()

	init {
		register(String::class, PrimitiveAdapter.STRING)
		register(Char::class, PrimitiveAdapter.CHAR)
		register(Boolean::class, PrimitiveAdapter.BOOLEAN)
		register(Byte::class, PrimitiveAdapter.BYTE)
		register(Short::class, PrimitiveAdapter.SHORT)
		register(Int::class, PrimitiveAdapter.INT)
		register(Long::class, PrimitiveAdapter.LONG)
		register(Float::class, PrimitiveAdapter.FLOAT)
		register(Double::class, PrimitiveAdapter.DOUBLE)
		register(UUID::class, ScalarTypeAdapter({ it.toString() }, { UUID.fromString(it) }, String::class))
		register(BlockData::class, ScalarTypeAdapter({ it.asString }, { Bukkit.createBlockData(it) }, String::class))
		register(ItemType::class, RegistryAdapter(RegistryKey.ITEM, ItemType::class))
		register(DataComponentType::class, RegistryAdapter(RegistryKey.DATA_COMPONENT_TYPE, DataComponentType::class))
		register(Enchantment::class, RegistryAdapter(RegistryKey.ENCHANTMENT, Enchantment::class))
	}

	fun <T : Any> register(klass: KClass<T>, adapter: TypeAdapter<T>) {
		cache[klass] = adapter
	}

	fun <T : Any> resolve(configType: ConfigType<T>): TypeAdapter<T> = when (configType) {
		is ListConfigType<*> -> ListAdapter(resolveSimple(configType.elementType)) as TypeAdapter<T>
		is SetConfigType<*> -> SetAdapter(resolveSimple(configType.elementType)) as TypeAdapter<T>
		is MapConfigType<*, *> -> resolveMap(configType) as TypeAdapter<T>
		is SimpleConfigType<*> -> resolveSimple(configType)
	}

	private fun resolveMap(configType: MapConfigType<*, *>): TypeAdapter<Map<Any, Any>> {
		val keyAdapter = resolve(configType.keyType as ConfigType<Any>)
		val valueAdapter = resolve(configType.valueType as ConfigType<Any>)
		return MapAdapter(keyAdapter, valueAdapter)
	}

	private fun <T : Any> resolveSimple(configType: ConfigType<T>): TypeAdapter<T> {
		val klass = configType.klass as KClass<T>
		when (val cached = cache[klass]) {
			is TypeAdapter<*> -> return cached as TypeAdapter<T>
			is MissingAdapter -> error(missingAdapterMessage(klass))
			else -> Unit
		}

		val resolved = findFallbackAdapter(klass)
		cache[klass] = resolved ?: MissingAdapter
		return resolved ?: error(missingAdapterMessage(klass))
	}

	private fun <T : Any> findFallbackAdapter(klass: KClass<T>): TypeAdapter<T>? = when {
		klass.java.isEnum -> EnumAdapter(klass) as TypeAdapter<T>
		ConfigurationSerializable::class.java.isAssignableFrom(klass.java) ->
			BukkitAdapter(klass as KClass<out ConfigurationSerializable>) as TypeAdapter<T>
		Config::class.java.isAssignableFrom(klass.java) ->
			ConfigAdapter(configFactory(klass as KClass<out Config>)) as TypeAdapter<T>
		else -> SerializerAdapter.findCompanionSerializer(klass)?.let { SerializerAdapter(it) }
	}

	private fun <T : Config> configFactory(klass: KClass<T>): (ConfigStorage) -> T {
		val constructor = try {
			klass.java.getDeclaredConstructor(ConfigStorage::class.java).apply { isAccessible = true }
		} catch (e: NoSuchMethodException) {
			error(
				"${klass.simpleName} has no (ConfigStorage) constructor, so it can't be built automatically. " +
						"Register a TypeAdapterRegistry.register(${klass.simpleName}::class, ConfigAdapter { storage -> ... }) instead."
			)
		}
		return { storage -> constructor.newInstance(storage) }
	}

	private fun missingAdapterMessage(klass: KClass<*>) =
		"No TypeAdapter registered for ${klass.simpleName}. " +
				"Register one via TypeAdapterRegistry.register(...), SerializerAdapter.register(...), " +
				"or give it a companion object : ObjectSerializer<${klass.simpleName}>."

}
