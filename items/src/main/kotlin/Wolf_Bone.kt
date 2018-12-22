import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class Wolf_Bone : Item() {

    private var runnable: BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            val iterator = infos.iterator()
            while (iterator.hasNext()) {
                val info = iterator.next()
                if (info.isTimeOut) {
                    info.remove()
                    iterator.remove()
                }
            }
        }
    }

    override fun loadFeatures() {
        require(ItemInfo(Material.BONE, "§7傀儡法杖", listOf("§2§o召唤傀儡与你并肩作战")))

        require(Recipe("2@0;397:2;0;0;397:2;0;0;280;0"))
    }

    init {
        runnable.runTaskTimerAsynchronously(Main.main, 100, 20)
    }

    private val infos = HashSet<EntityInfo>()

    private inner class EntityInfo(private val player: Player) {
        private val entitys = arrayOfNulls<Wolf>(3)
        private var time: Long = 0

        fun spawn() {
            time = System.currentTimeMillis() + 20000
            for (j in 0..2) {
                entitys[j] = player.world.spawnEntity(player.location, EntityType.WOLF) as Wolf
                val wolf = entitys[j]!!
                wolf.owner = player
                wolf.maxHealth = 7.0
                wolf.health = 7.0
            }
        }

        fun remove() {
            entitys.indices
                    .map { entitys[it]!! }
                    .forEach { it.remove() }
        }

        fun isEntity(entity: Entity): Boolean {
            return entitys.indices
                    .map { entitys[it]!! }
                    .contains(entity)
        }

        val isTimeOut: Boolean
            get() = time <= System.currentTimeMillis()
    }

    @EventHandler
    fun onuse(e: PlayerInteractEvent) {
        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
            val p = e.player
            val item = e.item
            if (isItem(item) && get<Permission>().hasPermission(p)) {
                e.isCancelled = true
                if (item.amount <= 1)
                    item.type = Material.AIR
                else
                    item.amount = item.amount - 1
                p.itemOnCursor = item
                p.updateInventory()

                val info = EntityInfo(p)
                infos.add(info)
                info.spawn()
            }
        }
    }

    @EventHandler
    fun onSpawn(e: PlayerInteractEntityEvent) {
        if (e.rightClicked is Wolf) {
            for (info in infos) {
                if (info.isEntity(e.rightClicked)) {
                    e.isCancelled = true
                    return
                }
            }
        }
    }

    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        if (e.plugin === Main.main) {
            for (info in infos)
                info.remove()
        }
    }
}
