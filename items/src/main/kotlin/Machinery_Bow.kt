import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.Durability
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Recipe
import cf.wayzer.util.BarUtil
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

class Machinery_Bow : Item() {
    interface SuperArrow {
        val arrowName: String

        fun onHit(entity: LivingEntity)
    }

    private val currentInfos = HashMap<Player, CurrentInfo>()

    private inner class CurrentInfo(internal var p: Player) {

        internal var index = -1
        internal var current = -1
        internal var arrow: SuperArrow? = null

        fun change() {
            val items = p.inventory.contents
            val arrows = items.indices.filter { items[it] != null && items[it].type == Material.ARROW }
            if (arrows.isEmpty()) {
                index = -1
                current = -1
                arrow = null
                BarUtil.sendToPlayer(p, "§c你身上没有任何箭")
                return
            } else if (arrows.size == 1 || index >= arrows.size - 1) {
                index = 0
                current = arrows[0]
            } else {
                index++
                current = arrows[index]
            }
            val item = items[current]
            arrow = getType(item)
            BarUtil.sendToPlayer(p, "§e切换箭头(" + index + "):" + if (arrow == null) "普通箭头" else arrow!!.arrowName)
        }

        fun onUse(): Boolean {
            if (current == -1)
                return false
            val item: ItemStack? = p.inventory.getItem(current)

            if (item == null) {
                change()
                return onUse()
            }

            if (arrow?.let { (it as Item).isItem(item) } ?: (item.type == Material.ARROW)) {
                p.consumeItem(item) { inv, ite ->
                    inv.setItem(current, ite)
                }
                return true
            }
            return false
        }
    }

    override fun loadFeatures() {
        require(ItemInfo(Material.BOW, "§6机械弓", listOf("§8§o可以用它来发射各种箭头")) { im, _ ->
            im.addEnchant(Enchantment.ARROW_INFINITE, 1, false)
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        })

        require(Recipe("2@131;265;131;0;261;0;0;0;0"))
        require(Durability(2000))
    }

    fun getType(item: ItemStack?): SuperArrow? {
        for (arrow in arrows) {
            if (arrow.isItem(item))
                return arrow as SuperArrow
        }
        return null
    }

    @EventHandler
    fun onShot(e: EntityShootBowEvent) {
        if (e.entityType != EntityType.PLAYER)
            return
        if (e.isCancelled)
            return
        val p = e.entity as Player
        if (isItem(e.bow) && permission.hasPermission(p)) {
            e.isCancelled = true
            currentInfos[p]?.let {
                if (it.onUse()) {
                    if (it.arrow != null)
                        e.projectile.customName = it.arrow!!.arrowName
                    e.isCancelled = false
                } else
                    BarUtil.sendToPlayer(p, "§c请选择箭头")
            } ?: let {
                val info = CurrentInfo(p)
                currentInfos.put(p, info)
                info.change()
            }
        }
    }

    @EventHandler
    fun onChange(e: PlayerInteractEvent) {
        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            val p = e.player
            if (isItem(e.item) && permission.hasPermission(p)) {
                var info: CurrentInfo? = currentInfos[p]
                if (info == null) {
                    info = CurrentInfo(p)
                    currentInfos.put(p, info)
                }
                info.change()
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent) {
        if (e.isCancelled)
            return
        if (e.damager.type == EntityType.ARROW) {
            val arrow = e.damager as Arrow
            if (arrow.shooter is Player && arrow.customName != null) {
                val name = arrow.customName
                for (a in arrows) {
                    if ((a as SuperArrow).arrowName == name) {
                        (a as SuperArrow).onHit(e.entity as LivingEntity)
                        return
                    }
                }
            }
        }
    }

    companion object {

        private val arrows = ArrayList<Item>()

        fun registerArrow(arrow: Item) {
            if (arrow is SuperArrow) {
                arrows.add(arrow)
            } else {
                throw RuntimeException("错误的物品:不符合类型")
            }
        }
    }
}
