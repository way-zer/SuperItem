import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType

class Arrow_Blindness : Item(), Machinery_Bow.SuperArrow {
    lateinit var effect: Effect
    override fun loadFeatures() {
        require(ItemInfo(Material.ARROW, "§7箭", listOf("§e§o萤石箭头")))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.BLINDNESS, 140),
                Effect.EffectData(PotionEffectType.NIGHT_VISION, 90)
        ))
    }

    init {
        Machinery_Bow.registerArrow(this)
    }

    override val arrowName: String
        get() = "萤石箭头"

    override fun onHit(entity: LivingEntity) {
        effect.setEffect(entity, 0)
        effect.setEffect(entity, 1)
    }
}
