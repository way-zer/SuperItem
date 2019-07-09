package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * 药水工具库
 * @param defaultData Item使用到的所有 PotionEffect
 */
class Effect(override vararg val defaultData: EffectData) : Feature<Array<out Effect.EffectData>>(), Feature.OnDisable, Feature.HasListener, Listener {
    class PotionEffectTypeAdapter : TypeAdapter<PotionEffectType>() {
        override fun read(reader: JsonReader): PotionEffectType? {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }
            return PotionEffectType.getByName(reader.nextString())
        }

        override fun write(writer: JsonWriter, value: PotionEffectType?) {
            if (value == null) {
                writer.nullValue()
            } else
                writer.value(value.name)
        }

    }

    data class EffectData(
            @JsonAdapter(PotionEffectTypeAdapter::class)
            val type: PotionEffectType,
            val time: Int = Long_Time,
            val strong: Int = 0,
            val isAmbient: Boolean = false,
            val hasParticles: Boolean = false,
            val icon: Boolean = true
    )

    private val players = List(defaultData.size) {
        mutableSetOf<Player>()
    }

    override val listener: Listener
        get() = this

    /**
     * 为玩家添加效果
     * @param index 药水在data中的序号(建议当data唯一时使用默认(0))
     */
    fun setEffect(entity: LivingEntity, index: Int = 0) {
        val effect = data[index]
        val potionEffect = PotionEffect(effect.type, effect.time, effect.strong, effect.isAmbient, effect.hasParticles, effect.icon)
        if (entity.addPotionEffect(potionEffect, true)) {
            if (effect.time >= Long_Time && entity is Player)
                players[index].add(entity)
        }
    }

    /**
     * 查看是否添加长期效果
     * @param index 药水在data中的序号(建议当data唯一时使用默认(0))
     */
    fun hasLongTimeEffect(entity: LivingEntity, index: Int = 0) = players[index].contains(entity)

    /**
     * 为玩家移除效果
     * 退出和插件关闭会自动处理
     * @param index 药水在data中的序号(建议当data唯一时使用默认(0))
     */
    fun removeEffect(entity: LivingEntity, index: Int = 0) {
        val effect = data[index]
        entity.removePotionEffect(effect.type)
        if (effect.time >= Long_Time && entity is Player)
            players[index].remove(entity)
    }

    override fun onDisable(main: Main) {
        players.forEachIndexed { index, mutableSet ->
            mutableSet.forEach {
                removeEffect(it, index)
            }
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        players.forEachIndexed { index, mutableSet ->
            if (mutableSet.contains(e.player)) {
                removeEffect(e.player, index)
            }
        }
    }

    companion object {
        const val Long_Time = 20 * 60 * 60
    }
}