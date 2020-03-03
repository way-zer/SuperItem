package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.features.NBT.API.set
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack

/**
 * 为物品添加耐久功能
 * 需要在 ItemInfo 后
 * @see ItemInfo
 */
class Durability(override val defaultData: Int) : Feature<Int>(), Feature.HasListener {
    override val listener: Listener
        get() = mListener

    /**
     * 设置耐久
     * @param item 需要设置的物品
     * @param now 值(默认 满)
     */
    fun setDurability(item: ItemStack, now: Int = data) {
        val nbt = NBT.API.readOrCreate(item)
        nbt["SICD"] = now
        nbt["SIMD"] = data
        NBT.API.write(item, nbt)
    }

    /**
     * 获取当前耐久
     * @param item 需要获取的物品
     * @return 获得到的耐久(错误的物品为-1)
     */
    fun getDurability(item: ItemStack): Int {
        val nbt = NBT.API.read(item)
        return if (nbt?.hasKey("SICN") == true) nbt.getInteger("SICN")
        else -1
    }

    companion object {
        private class MListener : Listener {
            @EventHandler(ignoreCancelled = true)
            fun onDurability(e: PlayerItemDamageEvent) {
                if (e.item.durability < 1)
                    return
                val nbt = NBT.API.read(e.item)
                if (nbt?.hasKey("SIMD") == true) {
                    val max = nbt.getInteger("SIMD")
                    var cur = nbt.getInteger("SICD")
                    cur -= e.damage
                    if (cur < 0) {
                        e.damage = 99999
                    } else {
                        nbt["SICD"] = cur
                        NBT.API.write(e.item, nbt)
                        cur = ((max.toDouble()) / cur * e.item.type.maxDurability).toInt()
                        e.damage = e.item.durability - cur
                    }
                }
            }
        }

        private val mListener = MListener()
    }
}
