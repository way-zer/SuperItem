package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import cf.wayzer.SuperItem.Main.Companion.main
import cf.wayzer.SuperItem.events.ItemStackHandleEvent
import cf.wayzer.SuperItem.features.NBT.API.set
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * 设置item的物品信息
 * @param defaultMaterial 物品的材质
 * @param defaultDamage 物品的损失或附加值
 * @param defaultName 物品的显示名
 * @param defaultLore 物品的Lore
 * @param loadOther 为物品设置其他属性
 */
class ItemInfo(
        private val defaultMaterial: Material,
        private val defaultName: String,
        private val defaultLore: List<String>,
        private val defaultDamage: Short = 0,
        private val loadOther: (ItemMeta, ItemStack) -> Unit = { _, _ -> }
) : Feature<ItemInfo.Data>(), Feature.OnPostLoad {
    interface ItemStackHandler : ((ItemStack,Player?)->Unit)
    /**
     * ItemStack初始化模板
     */
    lateinit var itemStackTemplate: ItemStack private set
    private val itemStackHandlers = mutableSetOf<ItemStackHandler>()

    override val defaultData: Data
        get() = Data(defaultMaterial, defaultDamage, defaultName, defaultLore)

    data class Data(
            val material: Material,
            val data: Short,
            val name: String,
            val lore: List<String>
    )

    /**
     * Create an ItemStack using Template and Handlers
     * @param p the player crafting
     */
    fun newItemStack(p:Player?=null):ItemStack{
        val itemStack = itemStackTemplate.clone()
        itemStackHandlers.forEach { it(itemStack,p) }
        main.server.pluginManager.callEvent(ItemStackHandleEvent(itemStack,p))
        itemStack.itemMeta = itemStack.itemMeta?.apply {
            setDisplayName(displayName.replace("&","§"))
            lore = lore?.map { it.replace("&","§") }
        }
        return itemStack
    }

    /**
     * Handler when an itemStack creates
     * Don't work with Recipe and Texture
     */
    fun registerHandler(handler: ItemStackHandler)=itemStackHandlers.add(handler)

    override fun onPostLoad(main: Main) {
        val itemStack = ItemStack(data.material, 1)

        val im = itemStack.itemMeta!!
        im.setDisplayName(data.name)
        im.lore = data.lore
        itemStack.itemMeta = im
        loadOther(im, itemStack)
        itemStack.itemMeta = im
        val nbt = NBT.API.readOrCreate(itemStack)
        nbt[NBT_TAG_NAME] = item.name
        NBT.API.write(itemStack, nbt)

        this.itemStackTemplate = itemStack
    }

    companion object {
        const val NBT_TAG_NAME="SICN"
    }
}
