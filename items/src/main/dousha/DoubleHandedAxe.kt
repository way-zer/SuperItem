import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DoubleHandedAxe : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.IRON_AXE, "${ChatColor.AQUA}双持战斧", listOf(
                "${ChatColor.WHITE}当你的主手和副手同时装备该武器时此武器将额外获得 +5 伤害"
        )))
    }

    @EventHandler
    fun onPlayerAttack(e: EntityDamageByEntityEvent) { /// XXX: Performance?
        if (e.damager is Player) {
            val player = e.damager as Player
            if (permission.hasPermission(player)
                    && isItem(player.inventory.itemInMainHand) && isItem(player.inventory.itemInOffHand)) {
                e.damage += 5.0
            }
        }
    }
}
