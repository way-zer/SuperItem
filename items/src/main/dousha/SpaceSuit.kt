import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerVelocityEvent
import org.bukkit.potion.PotionEffectType

class SpaceSuit : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_HELMET,
                "${ChatColor.AQUA}航天员",
                listOf("${ChatColor.WHITE}当你的高度超过 256 格时你将会脱离地心引力")))
        sfx = require(Effect(Effect.EffectData(PotionEffectType.LEVITATION)))
    }

    @EventHandler
    fun onPlayerMove(e: PlayerVelocityEvent) {
        val position = e.player.location // TODO: What?
        if (position.y > 256.0) {
            sfx.setEffect(e.player)
        } else {
            sfx.removeEffect(e.player)
        }
    }

    private lateinit var sfx: Effect
}