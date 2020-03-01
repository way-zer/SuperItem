package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import cf.wayzer.util.RecipeUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

/**
 * 为物品绑定合成配方
 * 需要在 ItemInfo 后使用
 * @see ItemInfo
 * @param defaultData 合成配方
 * @see RecipeUtil
 */
class Recipe(override val defaultData: String) : Feature<String>(), Feature.OnPostLoad, Feature.OnDisable,Feature.HasListener,Listener {
    override val listener: Listener
        get() = this
    private lateinit var recipe:Recipe
    override fun onPostLoad(main: Main) {
        recipe = RecipeUtil.getByString(ItemStack(Material.AIR), data)
        main.server.addRecipe(recipe)
    }

    override fun onDisable(main: Main) {
        val iter = main.server.recipeIterator()
        iter.forEachRemaining {
            if(it==recipe)iter.remove()
        }
    }

    @EventHandler
    fun onCreate(event:PrepareItemCraftEvent){
        if(event.recipe==recipe){
            event.inventory.result = item.get<ItemInfo>().newItemStack(event.viewers[0] as? Player)
        }
    }
}
