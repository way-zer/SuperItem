package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Item.Companion.isItem
import cf.wayzer.SuperItem.ItemManager
import cf.wayzer.SuperItem.Main
import cf.wayzer.util.BarUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent

/**
 * **自动Require
 * 为物品绑定权限
 * @param default 物品的权限(默认superitem.name)
 */
class Permission(private val default: String? = null) : Feature<String>(), Feature.OnPostLoad, Feature.HasListener, Listener {
    override val defaultData by lazy { default ?: "superitem.${item::class.java.name}" }

    /**
     * 判断玩家是否有权限
     * @param tip 是否发送提示
     */
    fun hasPermission(p: Player, tip: Boolean = true): Boolean {
        return when {
            p.hasPermission(data) -> true
            else -> {
                if (tip)
                    BarUtil.sendToPlayer(p, "§c你没有权限使用")
                false
            }
        }
    }

    override fun onPostLoad(main: Main) {
        item.permission = this
    }

    override val listener: Listener
        get() = this

    @EventHandler
    fun onRecipe(e: CraftItemEvent) {
        ItemManager.getItem(e.recipe.result)?.let {
            if (it.isItem(e.recipe.result) && !hasPermission(e.whoClicked as Player, false)) {
                e.whoClicked.sendMessage("§c你没有权限合成这个物品")
                e.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlace(e: BlockPlaceEvent) {
        ItemManager.getItem(e.itemInHand)?.let { e.isCancelled = true }
    }
}