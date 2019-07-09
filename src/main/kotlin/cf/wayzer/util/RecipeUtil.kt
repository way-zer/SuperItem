package cf.wayzer.util

import cf.wayzer.SuperItem.Main
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

@Suppress("DEPRECATION")
/**
 * 用于解析并获取配方
 */
object RecipeUtil {
    private val CHARS = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val namespacedKey = NamespacedKey(Main.main,"Superitem")

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
                throw GetRecipeException("格式错误: $str")
            val type1 = split[0]
            val items = split[1]
            return when (type1.toLowerCase()) {
                "1", "small" -> getSmallRecipe(item, items)
                "2", "big" -> getBigRecipe(item, items)
                "3", "shapeless" -> getShapelessRecipe(item, items)
                else -> throw GetRecipeException("格式错误: $str")
            }
        } catch (e: NumberFormatException) {
            throw GetRecipeException("无效的数字: ", e)
        }

    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getSmallRecipe(item: ItemStack, items: String): ShapedRecipe {
        val recipe = ShapedRecipe(namespacedKey,item)
        val strs = items.split(";".toRegex())
        if (strs.size != 4)
            throw GetRecipeException("数量错误(4): $items")
        recipe.shape("12", "34")
        for (i in 0..3) {
            if (strs[i].isEmpty() || strs[i] == "0")
                continue
            if(strs[i].contains(":")){
                val split = strs[i].split(":")
                recipe.setIngredient(CHARS[i],getMaterial(split[0]),split[1].toInt())
            }else recipe.setIngredient(CHARS[i], getMaterial(strs[i]))
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getBigRecipe(item: ItemStack, items: String): ShapedRecipe {
        val recipe = ShapedRecipe(namespacedKey,item)
        val strs = items.split(";".toRegex())
        if (strs.size != 9)
            throw GetRecipeException("数量错误(9): $items")
        recipe.shape("123", "456", "789")
        for (i in 0..8) {
            if (strs[i].isEmpty() || strs[i] == "0")
                continue
            if(strs[i].contains(":")){
                val split = strs[i].split(":")
                recipe.setIngredient(CHARS[i],getMaterial(split[0]),split[1].toInt())
            }else recipe.setIngredient(CHARS[i], getMaterial(strs[i]))
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getShapelessRecipe(item: ItemStack, items: String): ShapelessRecipe {
        val recipe = ShapelessRecipe(namespacedKey,item)
        items.split(";".toRegex()).dropLastWhile { it.isEmpty()}.filterIndexed { index, _ -> index<9 }.forEach {
            if(it.contains(":")){
                val split = it.split(":")
                recipe.addIngredient(getMaterial(split[0]),split[1].toInt())
            }else recipe.addIngredient(getMaterial(it))
        }
        return recipe
    }

    @Throws(RecipeUtil.GetRecipeException::class)
    private fun getMaterial(str:String):Material{
        val m  = str.toIntOrNull()?.let{
            throw GetRecipeException("新版本不支持数字: $str")
        }?:Material.getMaterial(str)
        if(m==null||m==Material.AIR)
            throw GetRecipeException("找不到指定物品: $str")
        return m
    }
}
