package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * 继承后编译放到items目录下
 */
abstract class Item : Listener {
    open class Builder(override val packageName: String,override val name:String):Item(){
        override fun loadFeatures() {}

        @Suppress("unused")
        inline fun <reified T:Event>listen(ignoreCancelled:Boolean=false, priority: EventPriority = EventPriority.NORMAL, crossinline handle:(T)->Unit){
            Bukkit.getServer().pluginManager.registerEvent(T::class.java,this,priority, { _, event -> handle(event as T) },pluginMain,ignoreCancelled)
        }

        fun register(){
            ItemManager.registerItem(this)
        }
    }
    /**
     * 获取默认物品
     */
    abstract fun loadFeatures()

    val features : MutableMap<Class<*>,MutableList<Feature<*>>> = mutableMapOf()

    @Deprecated("保证扩展和安全,请使用get,1.3版本弃用",ReplaceWith("get<ItemInfo>().newItemStack()"))
    val item
        get() = get<ItemInfo>().newItemStack()

    @Deprecated("保证扩展和安全,请使用get,1.3版本弃用",ReplaceWith("get<Permission>()"))
    val permission
        get() = get<Permission>()

    @Deprecated("保证扩展和安全,请使用get,1.3版本弃用",ReplaceWith("get<CoolDown>()"))
    val coolDown
        get() = get<CoolDown>()

    open val name: String
        get() = javaClass.simpleName

    open val packageName: String
        get() = javaClass.`package`.name


    /**
     * 安全的获取指定类型的feature
     * @param c feature的类型
     * @param index feature序数(如果有多个,从0开始)
     * @exception RuntimeException 如果不存在指定类型的feature
     */
    fun <T: Feature<*>> get(c : Class<T>, index:Int=0):T {
        val res = features[c]?.getOrNull(index) ?:
            throw RuntimeException("[$name] Can't find ${c.name}[$index], may you forget require it")
        @Suppress("UNCHECKED_CAST")
        return res as T
    }

    /**
     * @see get(c,index)
     * 只能在kotlin下调用
     */
    inline fun <reified T:Feature<*>> get(index: Int=0):T = get(T::class.java,index)

    /**
     * 判断是否有对应feature
     */
    fun <T: Feature<*>> has(c : Class<T>):Boolean = features[c]?.isNotEmpty()?:false

    /**
     * @see has(c)
     * 只能在kotlin下调用
     */
    inline fun <reified T:Feature<*>> has():Boolean = has(T::class.java)

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
        inv.itemInMainHand = get<ItemInfo>().newItemStack(p)
        p.updateInventory()
        return true
    }

    fun drop(location: Location,player: Player?=null){
        location.world.dropItem(location,get<ItemInfo>().newItemStack(player))
    }

    /**
     * 判断物品是否是当前Item的道具
     */
    fun isItem(itemStack: ItemStack?): Boolean {
        return ItemManager.getItem(itemStack)?.equals(this) ?: false
    }


    /**
     * 注册feature,可通过get使用
     */
    open fun <T : Feature<out Any>> require(feature: T): T{
        feature.item=this
        ConfigManager.loadForFeature(this,feature)
        features.getOrPut(feature::class.java,::mutableListOf).add(feature)
        return feature
    }

    companion object {
        /**
         * 可以用作随机数
         */
        val random = Random()

        /**
         * 插件实例,可用于Scheduler等操作
         */
        val pluginMain = Main.main as JavaPlugin


        @Deprecated("不建议直接使用,1.3版本弃用", ReplaceWith("get<Permission>().hasPermission(p)"))
        fun Item.hasPermission(p: Player) =
                get<Permission>().hasPermission(p)

        @Deprecated("不建议直接使用,1.3版本弃用", ReplaceWith("get<CoolDown>().isCoolDownOK(p)"))
        fun Item.isCoolDownOK(p: Player) =
                get<CoolDown>().isCoolDownOK(p)

        @Deprecated("不建议直接使用,1.3版本弃用", ReplaceWith("get<CoolDown>().add(p)"))
        fun Item.addCoolDown(p: Player) =
                get<CoolDown>().add(p)

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
