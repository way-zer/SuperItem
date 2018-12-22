import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.potion.PotionEffectType

class CutSplitSword : Item() {
    lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.GOLD_SWORD, "§4斩裂剑", listOf("§8§o帮助你斩杀一切")))
        require(Recipe("2@0;0;399;0;399;399;280;399;0"))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.REGENERATION, 200, 2),
                Effect.EffectData(PotionEffectType.SPEED, strong = 1)
        ))
    }

    @EventHandler
    fun onheld(e: PlayerItemHeldEvent) {
        val p = e.player
        val item = p.inventory.getItem(e.newSlot)
        if (effect.hasLongTimeEffect(p, 1) || !isItem(item)) {
            effect.removeEffect(p, 1)
        } else {
            effect.setEffect(p, 1)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (e.isCancelled)
            return
        if (e.entity is Player && e.damager is Player) {
            val p = e.damager as Player
            val item = p.inventory.itemInMainHand
            if (isItem(item) && get<Permission>().hasPermission(p)) {
                playEffect(p)
            }
        }
    }

    @EventHandler
    fun ondeath(e: PlayerDeathEvent) {
        val p = e.entity.killer ?: return
        val item = p.inventory.itemInMainHand
        if (isItem(item) && get<Permission>().hasPermission(p)) {
            effect.setEffect(p, 0)
        }
    }

    private fun playEffect(p: Player) {
        val w = p.world
        // TODO: 2018/2/25 WITCH_MAGIC 的新版名称
        w.spawnParticle(Particle.CRIT_MAGIC, p.location, 10, 5)
    }
}
