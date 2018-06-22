import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.Effect.Companion.Long_Time
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import org.bukkit.potion.PotionEffectType

class SpringBoots : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_BOOTS, "${ChatColor.GOLD}弹力之靴", listOf(
                "${ChatColor.WHITE}按下 Shift 或疾跑时获得${ChatColor.AQUA}跳跃提升 IV${ChatColor.RESET}效果",
                "${ChatColor.GRAY}免疫掉落伤害"
        )))
        sfx = require(Effect(Effect.EffectData(PotionEffectType.SPEED, Long_Time, 4)))
    }

    @EventHandler
    fun onPlayerCrouch(e: PlayerToggleSneakEvent) {
        if (isItem(e.player.inventory.boots) && permission.hasPermission(e.player)) {
            if (e.isSneaking) {
                sfx.setEffect(e.player)
            } else {
                sfx.removeEffect(e.player)
            }
        }
    }

    @EventHandler
    fun onPlayerSprint(e: PlayerToggleSprintEvent) {
        if (isItem(e.player.inventory.boots) && permission.hasPermission(e.player)) {
            if (e.isSprinting) {
                sfx.setEffect(e.player)
            } else {
                sfx.removeEffect(e.player)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerFall(e: EntityDamageEvent) {
        if (e.entity is Player &&
                e.cause == EntityDamageEvent.DamageCause.FALL &&
                permission.hasPermission(e.entity as Player) &&
                isItem((e.entity as Player).inventory.boots)) {
            e.isCancelled = true
        }
    }

    private lateinit var sfx: Effect
}
