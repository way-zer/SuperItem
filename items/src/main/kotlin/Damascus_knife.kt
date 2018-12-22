import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffectType

class Damascus_knife : Item() {
    lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.IRON_SWORD, "§3大马士革刀",
                listOf("§b§o特殊的冶炼方式", "§b§o让大马士革刀的纹路中", "§b§o含有一种奇特的化学物质", "§b§o小小的砍伤足以致人死地")))
        require(Recipe("2@0;0;265;0;265;265;280;42;0"))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.POISON, 300)
        ))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (e.isCancelled) return
        if (e.damager is Player) {
            val p = e.damager as Player
            if (isItem(p.inventory.itemInMainHand) && get<Permission>().hasPermission(p)) {
                if (!e.entity.isDead) {
                    effect.setEffect(e.entity as LivingEntity)
                }
            }
        }
    }
}
