package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import me.dpohvar.powernbt.api.NBTCompound
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
        val nbt = NBT.api.read(item) ?: let { NBTCompound() }
        nbt["SICD"] = now
        nbt["SIMD"] = data
        NBT.api.write(item, nbt)
    }

    /**
     * 获取当前耐久
     * @param item 需要获取的物品
     * @return 获得到的耐久(错误的物品为-1)
     */
    fun getDurability(item: ItemStack): Int {
        val nbt = NBT.api.read(item)
        return if (nbt?.containsKey("SICN") == true) nbt.getInt("SICN")
        else -1
    }

    companion object {
        private class MListener : Listener {
            @EventHandler(ignoreCancelled = true)
            fun onDurability(e: PlayerItemDamageEvent) {
                if (e.item.durability < 1)
                    return
                val nbt = NBT.api.read(e.item)
                if (nbt?.contains("SIMD") == true) {
                    val max = nbt.getInt("SIMD")
                    var cur = nbt.getInt("SICD")
                    cur -= e.damage
                    if (cur < 0) {
                        e.damage = 99999
                    } else {
                        nbt["SICD"] = cur
                        NBT.api.write(e.item, nbt)
                        cur = ((max.toDouble()) / cur * e.item.data.itemType.maxDurability).toInt()
                        e.damage = e.item.durability - cur
                    }
                }
            }
        }

        private val mListener = MListener()
    }
}