import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.ItemManager
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class BlueLightSaber : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_SWORD,
                "${ChatColor.DARK_PURPLE}蓝色光剑",
                listOf("${ChatColor.WHITE}当对手持有${ChatColor.RED}红色${ChatColor.RESET}光剑时伤害减半"),
                9))
    }

    @EventHandler
    fun onPokingAround(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            val attacker = e.damager as Player
            val victim = e.entity as Player
            if (isItem(attacker.inventory.itemInMainHand)
                    && get<Permission>().hasPermission(attacker)
                    && ItemManager.getItem("RedLightSaber")?.isItem(victim.inventory.itemInMainHand) == true) {
                e.damage /= 2.0
            }
        }
    }
}