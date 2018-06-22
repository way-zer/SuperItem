import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class BulletPloofGrass : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_CHESTPLATE, "${ChatColor.DARK_PURPLE}反应装甲", listOf(
                "${ChatColor.WHITE}所有投掷物均只造成 1 点伤害"
        )))
    }

    @EventHandler
    fun onBeingHit(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            if (isItem(player.inventory.chestplate) && permission.hasPermission(player)) {
                if (e.damager is Projectile && e.damage > 0.0) {
                    // you know, it's unfair that you can get smashed to death
                    // with snow balls and eggs.
                    e.damage = 1.0
                }
            }
        }
    }
}