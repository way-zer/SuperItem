import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffectType

class Life_Syringe : Item() {
    lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.FIREWORK, "§7生命注射器", listOf("§e§o超量补充生命")))
        require(Recipe("2@0;0;262;0;322;0;322;0;0"))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.ABSORPTION, 6000, 3),
                Effect.EffectData(PotionEffectType.HEAL, 20, 4)
        ))
    }

    @EventHandler
    fun onUse(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            val p = e.player
            val item = e.item
            if (isItem(item) && permission.hasPermission(p)) {
                e.isCancelled = true
                effect.setEffect(p, 0)
                effect.setEffect(p, 1)
                p.playSound(p.location, Sound.ENTITY_SHEEP_SHEAR, 1f, 1f)
                p.consumeItem(item)
            }
        }
    }
}
