# Superitem
一个容易扩展的MC(我的世界)插件
* 使用Kotlin编写(支持Java)
* 每个物品都是一个独立的class文件(运行时加载)
* 可以自己编写Feature(见下方说明)
* 每个物品都可以自动生成详细的配置文件(从名字到药水效果,取决于Feature)

## 授权
保留[本站](https://github.com/way-zer/SuperItem/)链接并保留指令帮助页的作者信息  
欢迎fork并提出pull请求,有任何疑问请提出issue
## 怎样写一个item
```kotlin
    package cf.wayzer.example
    
    //更多例子,看 items/src/main/kotin/
    class Damascus_knife : Item() {
        lateinit var effect: Effect
    
        override fun loadFeatures() {
            require(ItemInfo(Material.IRON_SWORD, "§3大马士革刀",
                    listOf("§b§o特殊的冶炼方式", "§b§o让大马士革刀的纹路中", "§b§o含有一种奇特的化学物质", "§b§o小小的砍伤足以致人死地")))
            require(Recipe("2@0;0;265;0;265;265;280;42;0"))
            require(Effect(
                    Effect.EffectData(PotionEffectType.POISON, 300)
            ))
        }
    
        @EventHandler(priority = EventPriority.LOWEST)
        fun onAttack(e: EntityDamageByEntityEvent) {
            if (e.isCancelled) return
            if (e.damager is Player) {
                val p = e.damager as Player
                if (isItem(p.inventory.itemInMainHand) && get<Permission>().hasPermission(p)) {
                    if (!e.entity.isDead) {
                        get<Effect>().setEffect(e.entity as LivingEntity)
                    }
                }
            }
        }
    }

```
1. 继承Item类
2. 覆写函数loadFeatures():  
    这个函数中,应当require所有你需要的Feature(Permission和Texture默认require)
3. Item 也是个 *Listener*,你可以编写任何你需要的事件处理器

## 这样编写自定义 Feature
### 什么是 *Feature*
* Feature 能够实现一类功能(自定义配方,自定义耐久度等)
* Feature 是Item和*配置文件*的桥梁(Feature的data)
### 例子
> 如果不是 *Item*,你应该放在包*lib*中 
```kotlin
package cf.wayzer.example.lib

class Durability(override val defaultData: Int) : Feature<Int>(), Feature.HasListener {
    override val listener: Listener
        get() = mListener

    //Feature 给Item提供的函数
    fun setDurability(item: ItemStack, now: Int = data) {}

    fun getDurability(item: ItemStack): Int {}

    companion object {
        private class MListener : Listener {
            @EventHandler(ignoreCancelled = true)
            fun onDurability(e: PlayerItemDamageEvent) {
                // ...
            }
        }

        private val mListener = MListener()
    }
}
```
#### 关于 *defaultData*
* 可用基本类型,也可以使用class
* 用于生成配置文件,读取配置文件请使用data变量
* 你可以使用Feature的构造参数来构造它

#### 其他
* 如果需要处理事件(Event),你应该实现(implement)接口*HasListener*
* 如果依赖其他Feature,你应该实现(implement)接口*onPostLoad*(在所有Feature加载完后调用)
* 如果需要在*插件关闭前*处理数据(例如,清理药水效果),你应该实现(implement)接口 *OnDisable*