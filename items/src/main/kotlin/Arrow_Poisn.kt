import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType

class Arrow_Poisn : Item(), Machinery_Bow.SuperArrow {
    lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.ARROW, "§2箭", listOf("§a§o剧毒箭头")))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.POISON, 140)
        ))
    }

    init {
        Machinery_Bow.registerArrow(this)
    }

    override val arrowName: String
        get() = "剧毒箭头"

    override fun onHit(entity: LivingEntity) {
        effect.setEffect(entity)
    }
}
