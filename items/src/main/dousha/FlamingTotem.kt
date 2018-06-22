import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class FlamingTotem : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.WOOD_HOE, "${ChatColor.DARK_PURPLE}燃烧图腾", listOf(
                "${ChatColor.WHITE}对目标造成伤害时使其${ChatColor.DARK_RED}${ChatColor.BOLD}燃烧${ChatColor.RESET} 2 秒"
        )))
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            val player = e.damager as Player
            if (isItem(player.inventory.itemInOffHand) && permission.hasPermission(player)) {
                e.entity.fireTicks = 40
            }
        }
    }
}
