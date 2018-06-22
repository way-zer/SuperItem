import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Durability
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material

class Obsidian_Sword : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.STONE_SWORD, "§9黑曜石剑", listOf("§7§o用黑曜石制作的耐久极高的剑")))

        require(Recipe("2@0;49;0;0;49;0;0;280;0"))
        require(Durability(6000))
    }
}
