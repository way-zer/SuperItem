import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

class FLAMES_SKULL : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.SKULL_ITEM, "§4FLAMES SKULL", listOf("§3 Superimposed fire damage")))
    }

    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            val p = e.damager as Player
            for (item in p.inventory.armorContents) {
                if (isItem(item) && get<Permission>().hasPermission(p)) {
                    playEffect(p)
                    val old = e.entity.fireTicks
                    e.entity.fireTicks = if (old > 0) old + 5 else 5
                }
            }
        }
        if (e.entity is Player) {
            val p = e.entity as Player
            if(p.inventory.armorContents.any { isItem(it) }
                    &&get<Permission>().hasPermission(p)) {
                playEffect(p)
                val old = e.damager.fireTicks
                e.damager.fireTicks = if (old > 0) old + 15 else 15
            }
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entityType != EntityType.PLAYER)
            return
        if (FIRECAUSES.contains(e.cause)) {
            val p = e.entity as Player
            if(p.inventory.armorContents.any { isItem(it) }
                    && get<Permission>().hasPermission(p))
                e.isCancelled = true
        }
    }

    private fun playEffect(p: Player) {
        val w = p.world
        w.playEffect<Any>(p.location, Effect.LAVA_POP, null, 5)
        w.playEffect<Any>(p.location, Effect.LAVA_POP, null, 5)
        w.playEffect<Any>(p.location, Effect.LAVA_POP, null, 5)
        w.playEffect<Any>(p.location, Effect.LAVA_POP, null, 5)
    }

    companion object {
        private val FIRECAUSES = listOf(DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA)
    }
}
