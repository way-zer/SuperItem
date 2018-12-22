import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Durability
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class Magnetic_Boot : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.IRON_BOOTS, "§3磁鞋", listOf("§b§o当你被攻击时", "§b§o有60%的几率不被击退")))

        require(Recipe("2@0;0;0;318;309;318;0;318;0"))
        require(Durability(2000))
    }

    @EventHandler
    fun onAttect(e: EntityDamageByEntityEvent) {
        if (e.entityType == EntityType.PLAYER) {
            val p = e.entity as Player
            if (isItem(p.inventory.boots) && get<Permission>().hasPermission(p)) {
                if (Item.Companion.getProbability(6, 10)) {
                    e.isCancelled = true
                    p.damage(e.damage)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun ondamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val p = e.entity as Player
            for (item in p.inventory.armorContents) {
                if (isItem(item)) {
                    if (get<Permission>().hasPermission(p)) {
                        e.damage = e.damage * 9 / 10
                    }
                    return
                }
            }
        }
    }
}
