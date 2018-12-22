package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import cf.wayzer.util.RecipeUtil

/**
 * 为物品绑定合成配方
 * 需要在 ItemInfo 后使用
 * @see ItemInfo
 * @param default 合成配方
 * @see RecipeUtil
 */
class Recipe(override val defaultData: String) : Feature<String>(), Feature.OnPostLoad, Feature.OnDisable {
    override fun onPostLoad(main: Main) {
        val recipe = RecipeUtil.getByString(item.get<ItemInfo>().itemStack, data)
        main.server.addRecipe(recipe)
    }

    override fun onDisable(main: Main) {
    }
}
