import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.Effect.EffectData
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffectType

class FlashingShield : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.SHIELD, "${ChatColor.AQUA}闪光盾牌", listOf(
                "${ChatColor.WHITE}右键使 5 格内所有玩家获得 5 秒的致盲效果")))
        sfx = require(Effect(
                EffectData(PotionEffectType.BLINDNESS, 140),
                EffectData(PotionEffectType.NIGHT_VISION, 90)))
        require(CoolDown(15000))
    }

    @EventHandler
    fun onPlayerRightClick(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
            if (isItem(e.player.inventory.itemInMainHand) && get<Permission>().hasPermission(e.player)) {
                applyEffects(e.player)
            }
        }
    }

    @EventHandler
    fun onPlayerRightClickOnSomething(e: PlayerInteractAtEntityEvent) {
        if (isItem(e.player.inventory.itemInMainHand) && get<Permission>().hasPermission(e.player)) {
            applyEffects(e.player)
        }
    }

    private fun applyEffects(player: Player) { /// XXX: Performance?
        val players = player.world.players
        players.remove(player)
        val basePos = player.location
        players.forEach {
            if (basePos.distanceSquared(it.location) < 25) {
                sfx.setEffect(it, 0)
                sfx.setEffect(it, 1)
            }
        }
    }

    private lateinit var sfx: Effect
}
