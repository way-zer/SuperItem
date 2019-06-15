# Superitem
An extensible custom item minecraft plugin
* written in Kotlin(Support Java)
* every item is a class file(runtime load)
* can write custom item feature
* every item has Automatically generated detailed configuration(from name to potion effect)
## For Chinese
see README.zh.md
## License
you should leave link to [this](https://github.com/way-zer/SuperItem/) and keep the author name in command help  
feel free to fork and pull requests
## How to write an item
```kotlin
    package cf.wayzer.example
    
    //for full see items/src/main/kotin/Damascus_knife
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
1. create an class extends *Item*
2. override function loadFeatures():  
    in this function,you should require features you need(Permission and Texture is default required)
3. Item is *Listener*,which means you can write your eventHandler

## How to write custom Feature
### What is a *Feature*
* Feature can use for a kind of feature.(custom Recipe,custom Durability or so on)
* Feature is a bridge of Item and configuration
### Example
if it isn't *Item*,you should put it into Package *lib* 
```kotlin
package cf.wayzer.example.lib

class Durability(override val defaultData: Int) : Feature<Int>(), Feature.HasListener {
    override val listener: Listener
        get() = mListener

    //Feature custom function(API)
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
#### About *defaultData*
you can use either primary type or custom class.
you can return it build by class parameters

#### Other
* If you need listen event,you can implement *HasListener*
* If you need other feature,you can implement *onPostLoad*
* If you need handle disableEvent(such as clearing potion effect),you can implement *OnDisable*