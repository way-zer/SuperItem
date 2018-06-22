import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.potion.PotionEffectType

class Slime_Boot : Item() {
    lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.LEATHER_BOOTS, "§2史莱姆的靴子", listOf("§a§o这双靴子让你几乎获得了", "§a§o史莱姆的所有特质!", "§a§o按住§e§oshift§a§o蓄力后跳跃")) { im, _ ->
            (im as LeatherArmorMeta).color = Color.fromRGB(2861668)
            im.isUnbreakable = true
        })
        require(Recipe("2@0;0;0;165;301;165;0;165;0"))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.JUMP, strong = 4),
                Effect.EffectData(PotionEffectType.JUMP, strong = 3)
        ))
    }

    @EventHandler
    fun onShift(e: PlayerToggleSneakEvent) {
        val p = e.player
        if (e.isSneaking) {
            if (isItem(p.inventory.boots) && permission.hasPermission(p)) {
                effect.setEffect(p, 0)
            }
        } else if (effect.hasLongTimeEffect(p, 0)) {
            effect.removeEffect(p, 0)
        }
    }

    @EventHandler
    fun onSprint(e: PlayerToggleSprintEvent) {
        val p = e.player
        if (e.isSprinting) {
            if (isItem(p.inventory.boots) && permission.hasPermission(p)) {
                effect.setEffect(p, 1)
            }
        } else if (effect.hasLongTimeEffect(p, 1)) {
            effect.removeEffect(p, 1)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun ondamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val p = e.entity as Player
            for (item in p.inventory.armorContents) {
                if (isItem(item)) {
                    if (permission.hasPermission(p)) {
                        e.damage = e.damage * 4 / 5
                    }
                    return
                }
            }
        }
    }

    @EventHandler
    fun ondamage(e: EntityDamageEvent) {
        if (e.cause == DamageCause.FALL)
            if (e.entity is Player) {
                val p = e.entity as Player
                for (item in p.inventory.armorContents) {
                    if (isItem(item)) {
                        if (permission.hasPermission(p)) {
                            e.isCancelled = true
                        }
                        return
                    }
                }
            }
    }
}
