package cf.wayzer.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.material.MaterialData

@Suppress("DEPRECATION")
/**
 * 用于解析并获取配方
 */
object RecipeUtil {
    private val CHARS = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9')

    class GetRecipeException constructor(message: String, cause: Throwable? = null) : Exception(message, cause, true, false)

    /**
     * 解析配方字符串
     * 格式:
     *  [small(1)/big(2)/shapeless(3)]@items
     * @sample getByString(item,"2@130;341;58;0;307;0;0;0;0")
     * 获取大合成表
     * 130 341  58
     *  -  307  -    ===>  item
     *  -   -   -
     */
    @Throws(RecipeUtil.GetRecipeException::class)
    fun getByString(item: ItemStack, str: String): Recipe {
        try {
            val split = str.split("@".toRegex())
            if (split.size != 2)
                throw GetRecipeException("格式错误: " + str)
            val type1 = split[0]
            val items = split[1]
            return when (type1.toLowerCase()) {
                "1", "small" -> getSmallRecipe(item, items)
                "2", "big" -> getBigRecipe(item, items)
                "3", "shapeless" -> getShapelessRecipe(item, items)
                else -> throw GetRecipeException("格式错误: " + str)
            }
        } catch (e: NumberFormatException) {
            throw GetRecipeException("无效的数字: ", e)
        }

    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getSmallRecipe(item: ItemStack, items: String): ShapedRecipe {
        val recipe = ShapedRecipe(item)
        val strs = items.split(";".toRegex())
        if (strs.size != 4)
            throw GetRecipeException("数量错误(4): " + items)
        recipe.shape("12", "34")
        for (i in 0..3) {
            val data = getData(strs[i])
            recipe.setIngredient(CHARS[i], data)
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getBigRecipe(item: ItemStack, items: String): ShapedRecipe {
        val recipe = ShapedRecipe(item)
        val strs = items.split(";".toRegex())
        if (strs.size != 9)
            throw GetRecipeException("数量错误(9): " + items)
        recipe.shape("123", "456", "789")
        for (i in 0..8) {
            if (strs[i].isEmpty() || strs[i] == "0")
                continue
            val data = getData(strs[i])
            if (data.itemType == Material.AIR)
                throw GetRecipeException("找不到指定物品: " + strs[i])
            recipe.setIngredient(CHARS[i], data)
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getShapelessRecipe(item: ItemStack, items: String): ShapelessRecipe {
        val recipe = ShapelessRecipe(item)
        val strs = items.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in 0..(if (strs.size > 9) 9 else strs.size) - 1) {
            val data = getData(strs[i])
            recipe.addIngredient(data)
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getData(str: String): MaterialData {
        try {
            if (str.contains(":")) {
                val split = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val i1 = Integer.valueOf(split[0])!!
                val i2 = Integer.valueOf(split[1])!!.toByte()
                return MaterialData(i1, i2)
            } else {
                return MaterialData(Integer.valueOf(str)!!)
            }
        } catch (e: NumberFormatException) {
            throw GetRecipeException("错误的数字: " + str, e)
        }
    }

}
