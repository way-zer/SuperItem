import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.events.ArmorEquipEvent
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.Effect.Companion.Long_Time
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.lang.Double.max
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Level

// May cause performance issue
// since this item would have to poll position(s) of player(s), over and over!
class EnderChestplate : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_CHESTPLATE,
                "${ChatColor.GOLD}末影胸甲",
                listOf("${ChatColor.WHITE}当你装备${ChatColor.AQUA}剑${ChatColor.RESET}、${ChatColor.AQUA}斧${ChatColor.RESET}或${ChatColor.AQUA}镰${ChatColor.RESET}时，按右键向前传送最多 6 格",
                        "${ChatColor.ITALIC}（冷却 8 秒）",
                        "${ChatColor.GRAY}当你接触水或淋雨时产生${ChatColor.DARK_GRAY}虚弱 II ${ChatColor.GRAY}效果")))
        val father = Bukkit.getPluginManager().getPlugin("SuperItem")
        if (father != null) {
            Bukkit.getScheduler().runTaskTimer(
                    father,
                    { pollPlayers() },
                    0,
                    20)
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "!SuperItem> Item `EnderChestplate` cannot be loaded normally!")
        }
        sfx = require(Effect(Effect.EffectData(PotionEffectType.WEAKNESS, Long_Time, 2)))
    }

    @EventHandler
    fun onPlayerEquipChange(e: ArmorEquipEvent) {
        if (isItem(e.newArmorPiece) && permission.hasPermission(e.player)) {
            lock.writeLock().lock()
            polling.add(e.player)
            lock.writeLock().unlock()
        } else {
            if (polling.contains(e.player)) {
                lock.writeLock().lock()
                sfx.removeEffect(e.player)
                polling.remove(e.player)
                lock.writeLock().unlock()
            }
        }
    }

    @EventHandler
    fun onPlayerTryToTeleport(e: PlayerInteractEvent) {
        if (isItem(e.player.inventory.chestplate) && permission.hasPermission(e.player)) {
            if ((e.action == Action.RIGHT_CLICK_AIR
                            || e.action == Action.RIGHT_CLICK_BLOCK)
                    && isHoldingCorrectItem(e.player.inventory.itemInMainHand)) {
                teleport(e.player)
            }
        }
    }

    @EventHandler
    fun onPlayerTryToTeleport(e: PlayerInteractEntityEvent) {
        if (isItem(e.player.inventory.chestplate) && permission.hasPermission(e.player)) {
            if (isHoldingCorrectItem(e.player.inventory.itemInMainHand)) {
                teleport(e.player)
            }
        }
    }

    private fun pollPlayers() {
        polling.forEach {
            if (isInWater(it) || isSoaked(it))
                sfx.setEffect(it)
            else
                sfx.removeEffect(it)
        }
    }

    private fun isInWater(player: Player): Boolean {
        // Note: A weak player is in water... it's a pun! get it?
        return player.location.block.isLiquid && player.location.block.type == Material.WATER
    }

    private fun isSoaked(player: Player): Boolean {
        return player.world.hasStorm()
                && player.location.block.lightFromSky == (15.toByte())
    }

    private fun isHoldingCorrectItem(item: ItemStack): Boolean {
        return targetMaterialSet.contains(item.type)
    }

    private fun teleport(player: Player): Location {
        val world = player.world
        var facing = player.eyeLocation.direction.normalize()
        facing.y = max(0.0, attenuatePitch(facing.y))
        facing.multiply(Math.random() * maxDistance)
        val pos = player.location.add(facing)
        while (!isSpotOk(world, pos)) {
            facing = facing.normalize().multiply(Math.random() * maxDistance)
        }
        return facing.toLocation(world)
    }

    private fun isSpotOk(world: World, location: Location): Boolean {
        // 1. Cannot teleport into a wall
        // 2. Cannot let player fall too hard after teleportation
        // XXX: You can go through a wall if desired...
        return hasSolidGround(world, location) && !wouldBumpHead(world, location)
    }

    private fun hasSolidGround(world: World, location: Location): Boolean {
        return if (world.getBlockAt(location.subtract(Vector(0, 1, 0))).type != Material.AIR) true
        else {
            if (world.getBlockAt(location.subtract(Vector(0, 2, 0))).type != Material.AIR) true
            else {
                world.getBlockAt(location.subtract(Vector(0, 3, 0))).type != Material.AIR
            }
        }
    }

    private fun wouldBumpHead(world: World, location: Location): Boolean {
        return world.getBlockAt(location.add(Vector(0, 1, 0))).type != Material.AIR
    }

    // f(x) = x - e^((-(x+1)^2)/0.5)
    private fun attenuatePitch(pitch: Double) = pitch - Math.exp(-(pitch + 1) * (pitch + 1) / 0.5)

    private lateinit var sfx: Effect
    private val polling = HashSet<Player>()
    private val lock = ReentrantReadWriteLock()
    private val maxDistance = 6.0
    private val targetMaterialSet = setOf(
            Material.DIAMOND_SWORD,
            Material.GOLD_SWORD,
            Material.IRON_SWORD,
            Material.STONE_SWORD,
            Material.WOOD_SWORD,
            Material.DIAMOND_AXE,
            Material.GOLD_AXE,
            Material.IRON_AXE,
            Material.STONE_AXE,
            Material.WOOD_AXE,
            Material.DIAMOND_HOE,
            Material.GOLD_HOE,
            Material.IRON_HOE,
            Material.STONE_HOE,
            Material.WOOD_HOE
    )
}
