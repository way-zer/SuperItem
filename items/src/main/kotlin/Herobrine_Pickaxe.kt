import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import cf.wayzer.SuperItem.features.Durability
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class Herobrine_Pickaxe : Item() {

    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_PICKAXE, "§9Herobrine’s pickaxe", listOf("§e§oLucky!!")) { im, _ ->
            im.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, false)
        })

        require(Recipe("2@0;377;0;377;279;377;0;377;0"))
        require(Durability(2000))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBreak(e: BlockBreakEvent) {
        if (e.isCancelled)
            return
        if (e.block.type == Material.STONE) {
            val p = e.player
            if (isItem(p.inventory.itemInMainHand) && get<Permission>().hasPermission(p)) {
                spawnEntity(e.block.location)
            }
        }
    }

    private fun spawnEntity(loc: Location) {
        val item: ItemStack
        if (Item.getProbability(1, 10)) {
            item = ItemStack(Material.BREAD)
        } else if (Item.getProbability(1, 10)) {
            item = ItemStack(Material.APPLE)
        } else if (Item.getProbability(1, 20)) {
            item = ItemStack(Material.COOKED_BEEF)
        } else {
            return
        }
        Main.main.server.scheduler.runTask(Main.main) {
            val entity = loc.world.spawnEntity(loc,
                    EntityType.DROPPED_ITEM) as org.bukkit.entity.Item
            entity.itemStack = item
        }
    }
}
