import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.NBT
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class Complex_Chestplate : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.IRON_CHESTPLATE, "§7复合胸甲", listOf("§8§o按住shift左键打开合成台", "§8§o按住shift右键打开末影箱")))
        require(Recipe("2@130;341;58;0;307;0;0;0;0"))
        require(NBT(
                NBT.AttributeModifier(NBT.AttributeType.MovementSpeed, -0.7, NBT.AttributeOperation.MultiplicativeLast, NBT.UseSlot.Chest)
        ))
    }

    @EventHandler
    fun onUse(e: PlayerInteractEvent) {
        val p = e.player
        if (p.isSneaking && e.action != Action.PHYSICAL && isItem(p.inventory.chestplate)
                && permission.hasPermission(p)) {
            e.isCancelled = true
            if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                p.openWorkbench(p.location, true)
            } else if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                p.openInventory(p.enderChest)
            }
        }
    }
}
