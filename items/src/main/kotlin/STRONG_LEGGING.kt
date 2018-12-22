import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.*
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffectType

class STRONG_LEGGING : Item() {
    lateinit var effect: Effect
    override fun loadFeatures() {
        require(ItemInfo(Material.LEATHER_LEGGINGS, "§c强壮的裤子", listOf("§e§o训练好大腿的肌肉有利于增加冲刺速度")) { im, _ ->
            (im as LeatherArmorMeta).color = Color.fromRGB(11547700)
        })
        require(Recipe("2@363;363;363;363;300;363;363;0;363"))
        require(Durability(2000))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.SPEED, strong = 2)
        ))
    }

    @EventHandler
    fun onSprint(e: PlayerToggleSprintEvent) {
        val p = e.player
        if (e.isSprinting) {
            if (isItem(p.inventory.leggings) && get<Permission>().hasPermission(p)) {
                effect.setEffect(p)
            }
        } else if (effect.hasLongTimeEffect(p)) {
            effect.removeEffect(p)
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
