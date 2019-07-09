import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.util.BarUtil
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.material.Colorable
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class Eggs_Gun : Item() {

    private val blocks = HashSet<BlockInfo>()
    private val players = HashSet<Player>()

    private inner class BlockInfo(internal var block: Block) {
        internal var old_color: DyeColor
        internal var time: Byte = 0

        init {
            val data = block.blockData as Colorable
            old_color = data.color?:DyeColor.WHITE
            time = 20
            data.color = DyeColor.RED
            block.blockData = data as BlockData
        }

        fun setback() {
            val data = block.blockData as Colorable
            data.color = old_color
            block.blockData = data as BlockData
        }
    }

    private var runnable: BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            for (block in HashSet(blocks)) {
                block.time--
                if (block.time <= 0) {
                    block.setback()
                    blocks.remove(block)
                }
            }
        }
    }

    override fun loadFeatures() {
        @Suppress("DEPRECATION")
        require(ItemInfo(Material.LEGACY_IRON_BARDING, "§7雪球来复枪", listOf("§e§o这是一件好玩的并且值得炫耀的玩具!")))

        require(CoolDown(100))
    }

    init {
        runnable.runTaskTimer(Main.main, 100L, 10L)// TODO: 2018/2/13 新Feature
    }

    @EventHandler
    fun onShoot(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            val p = e.player
            if (isItem(e.item) && get<Permission>().hasPermission(p) && get<CoolDown>().isCoolDownOK(p)) {
                e.isCancelled = true
                get<CoolDown>().add(p)
                val snow = p.world.spawnEntity(p.location.add(0.0, 1.0, 0.0), EntityType.SNOWBALL) as Snowball
                snow.shooter = p
                snow.velocity = p.location.direction.multiply(3)
                snow.customName = "§7雪球来复枪"
                p.playSound(p.location, Sound.ENTITY_ITEM_PICKUP, 0.8f, 0.8f)
            }
        } else if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            val p = e.player
            if (isItem(e.item) && get<Permission>().hasPermission(p)) {
                e.isCancelled = true
                if (players.contains(p)) {
                    players.remove(p)
                    BarUtil.sendToPlayer(p, "§c====关闭伤害模式====")
                } else {
                    players.add(p)
                    BarUtil.sendToPlayer(p, "§c====开启伤害模式====")
                }
            }
        }
    }

    @EventHandler
    fun onHit(e: ProjectileHitEvent) {
        if (e.entityType == EntityType.SNOWBALL && "§7雪球来复枪" == e.entity.customName
                && e.entity.shooter is Player) {
            val snow = e.entity
            val loc = snow.location.block.location
            for (i in -1..1)
                for (i1 in -1..1)
                    for (i11 in -1..1)
                        if (i == 0 || i1 == 0 || i11 == 0) {
                            val b = loc.world!!.getBlockAt(loc.blockX + i, loc.blockY + i1,
                                    loc.blockZ + i11)
                            if(b.blockData is Colorable && (b.blockData as Colorable).color != DyeColor.RED){
                                blocks.add(BlockInfo(b))
                            }
                        }
        }
    }

    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent) {
        if (e.isCancelled)
            return
        if (e.damager is Snowball) {
            if ("§7雪球来复枪" == e.damager.customName) {
                val p = (e.damager as Snowball).shooter as Player
                if (!players.contains(p))
                    return
                if (e.entityType == EntityType.PLAYER) {
                    if (players.contains(e.entity)) {
                        e.damage = 3.0
                    } else {
                        BarUtil.sendToPlayer(p, "§c====对方没有开启伤害模式====")
                    }
                } else if (e.entity is Monster) {
                    e.damage = 4.0
                }
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (e.block.blockData is Colorable && (e.block.blockData as Colorable).color == DyeColor.RED) {
            for (block in blocks) {
                if (block.block.location == e.block.location) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPiston(e: BlockPistonExtendEvent) {
        for (b in e.blocks) {
            for (block in blocks) {
                if (block.block.location == b.location) {
                    e.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPiston(e: BlockPistonRetractEvent) {
        for (b in e.blocks) {
            for (block in blocks) {
                if (block.block.location == b.location) {
                    e.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        if (e.plugin === Main.main) {
            for (block in blocks)
                block.setback()
        }
    }
}
