package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.ItemManager.require0
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * 继承后编译放到items目录下
 */
abstract class Item : Listener {
    /**
     * 获取默认物品
     */
    abstract fun loadFeatures()

    /**
     * 使用前需require(ItemInfo())
     */
    lateinit var item: ItemStack
    /**
     * 使用前需require(Permission())
     */
    lateinit var permission: Permission
    /**
     * 使用前需require(CoolDown())
     */

    lateinit var coolDown: CoolDown
    val name: String
        get() = javaClass.simpleName

    /**
     * 给予玩家道具
     */
    fun givePlayer(p: Player): Boolean {
        val inv = p.inventory
        val i = inv.firstEmpty()
        if (i == -1) {
            p.sendMessage("§c你身上没有足够的空位")
            return false
        }
        inv.setItem(i, inv.itemInMainHand)
        inv.itemInMainHand = item.clone()
        p.updateInventory()
        return true
    }

    companion object {
        /**
         * 可以用作随机数
         */
        var random = Random()

        /**
         * 插件实例,可用于Scheduler等操作
         */
        val pluginMain = Main.main as JavaPlugin

        /**
         * 判断物品是否是当前Item的道具
         */
        fun Item.isItem(itemStack: ItemStack?): Boolean {
            return ItemManager.getItem(itemStack)?.equals(this) ?: false
        }

        @Deprecated("不建议直接使用", ReplaceWith("permission.hasPermission(p)"))
        fun Item.hasPermission(p: Player) =
                permission.hasPermission(p)

        @Deprecated("不建议直接使用", ReplaceWith("coolDown.isCoolDownOK(p)"))
        fun Item.isCoolDownOK(p: Player) =
                coolDown.isCoolDownOK(p)

        @Deprecated("不建议直接使用", ReplaceWith("coolDown.add(p)"))
        fun Item.addCoolDown(p: Player) =
                coolDown.add(p)


        /**
         * @see ItemManager.require0
         */
        fun <H : Any, T : Feature<H>> Item.require(feature: T): T = require0(feature)

        /**
         * 消耗玩家指定物品
         * @param item 需要消耗的物品
         * @param setItem 设置消耗物(默认设置主手)
         */
        fun Player.consumeItem(item: ItemStack, setItem: (PlayerInventory, ItemStack?) -> Unit = { a, b -> a.itemInMainHand = b }) {
            if (item.amount > 0) {
                item.amount--
                setItem(inventory, item)
            } else
                setItem(inventory, null)
            updateInventory()
        }

        /**
         * 获取概率
         * @param min 分子
         * @param max 分母
         * @sample getProbability(2,5) 为40%的概率
         * @sample getProbability(40,100) 也是40%
         */
        @JvmStatic
        fun getProbability(min: Int, max: Int): Boolean {
            val i = random.nextInt(max)
            return i < min
        }
    }
}
