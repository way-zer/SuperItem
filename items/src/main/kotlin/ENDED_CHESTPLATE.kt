import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.*
import org.bukkit.*
import org.bukkit.Effect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.LeatherArmorMeta

class ENDED_CHESTPLATE : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.LEATHER_CHESTPLATE, "§d安德的外套", listOf("§5§o使你获得安德的力量")) { im, _ ->
            (im as LeatherArmorMeta).color = Color.fromRGB(8009611)
        })

        require(Recipe("2@0;0;0;381;299;381;0;368;0"))
        require(CoolDown(4000))
        require(Durability(2000))
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if ((e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) && ITEMS.contains(e.material)) {
            val p = e.player
            if (isItem(p.inventory.chestplate)) {
                if (get<Permission>().hasPermission(p) && get<CoolDown>().isCoolDownOK(p)) {
                    e.isCancelled = true
                    val targetLoc = e.player.getTargetBlock(null as Set<Material>?, 7).location.add(0.0,
                            1.0, 0.0)
                    targetLoc.pitch = p.location.pitch
                    targetLoc.yaw = p.location.yaw
                    if (targetLoc.distanceSquared(p.location) < 7)
                        p.sendMessage(ChatColor.RED.toString() + "你不能传送那么远!")
                    else {
                        get<CoolDown>().add(p)
                        playEffect(p)
                        p.teleport(targetLoc)
                        playEffect(p)
                        p.playSound(p.location, Sound.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.rawSlot == 6 && e.clickedInventory.type == InventoryType.PLAYER)
            if (isItem(e.currentItem)) {
                if (get<Permission>().hasPermission(e.whoClicked as Player))
                    playEffect(e.whoClicked as Player)
                else
                    e.isCancelled = true
            }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun ondamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            val p = e.entity as Player
            for (item in p.inventory.armorContents) {
                if (isItem(item)) {
                    if (get<Permission>().hasPermission(p)) {
                        e.damage = e.damage * 4 / 5
                    }
                    return
                }
            }
        }
    }

    private fun playEffect(p: Player) {
        val w = p.world
        w.playEffect<Any>(p.location, Effect.PORTAL_TRAVEL, null, 5)
        w.playEffect<Any>(p.location, Effect.PORTAL_TRAVEL, null, 5)
        w.playEffect<Any>(p.location, Effect.PORTAL_TRAVEL, null, 5)
        w.playEffect<Any>(p.location, Effect.PORTAL_TRAVEL, null, 5)
    }

    companion object {
        private val ITEMS = setOf(Material.WOOD_SWORD, Material.STONE_SWORD,
                Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.WOOD_AXE, Material.STONE_AXE,
                Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE)
    }
}
