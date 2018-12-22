import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.ItemManager
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class RedLightSaber : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_SWORD,
                "${DARK_PURPLE}红色光剑",
                listOf("${WHITE}当对手持有${AQUA}蓝色${RESET}光剑时伤害减半"),
                9))
    }

    @EventHandler
    fun onPokingAround(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            val attacker = e.damager as Player
            val victim = e.entity as Player
            if (isItem(attacker.inventory.itemInMainHand)
                    && get<Permission>().hasPermission(attacker)
                    && ItemManager.getItem("BlueLightSaber")?.isItem(victim.inventory.itemInMainHand) == true) {
                e.damage /= 2.0
            }
        }
    }
}
