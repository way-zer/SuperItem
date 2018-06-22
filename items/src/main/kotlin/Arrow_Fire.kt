import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.Material
import org.bukkit.entity.LivingEntity

class Arrow_Fire : Item(), Machinery_Bow.SuperArrow {
    override fun loadFeatures() {
        require(ItemInfo(Material.ARROW, "§4箭", listOf("§c§o燃烧箭头")))

    }

    init {
        Machinery_Bow.registerArrow(this)
    }

    override val arrowName: String
        get() = "燃烧箭头"

    override fun onHit(entity: LivingEntity) {
        if (entity.fireTicks > 0)
            entity.fireTicks = entity.fireTicks + 100
        else
            entity.fireTicks = 100
    }
}
