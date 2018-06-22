import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Ipecac_Agent : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.POTION, "§2吐根药剂", listOf("§a§o女巫留下的药水"), 4) { im, _ ->
            if (im is PotionMeta) {
                im.addCustomEffect(PotionEffect(PotionEffectType.CONFUSION, 300, 0, false, false), true)
                im.addCustomEffect(PotionEffect(PotionEffectType.POISON, 20, 0, false, false), true)
            }
            im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        })

    }

    @EventHandler
    fun onDrink(e: PlayerItemConsumeEvent) {
        val p = e.player
        if (isItem(e.item) && permission.hasPermission(p)) {
            val ball = p.world.spawnEntity(p.location.add(0.0, 1.0, 0.0), EntityType.FIREBALL) as Fireball
            ball.shooter = p
            ball.velocity = p.location.direction.multiply(2)
        }
    }
}
