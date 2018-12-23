package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.Item.Companion.require
import cf.wayzer.SuperItem.Main.Companion.main
import cf.wayzer.SuperItem.features.NBT
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Texture
import com.google.gson.JsonObject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.logging.Level

object ItemManager {
    private val logger = main.logger
    private val items = HashMap<String, Item>()
    private lateinit var ucl:URLClassLoader
    private lateinit var config: JsonObject

    /**
     * 从文件夹加载Item
     * 供Main使用
     */
    @Throws(Exception::class)
    fun load(config: JsonObject) {
        ItemManager.config = config
        val dir = File(Main.main.dataFolder, "items")
        if (!dir.exists())
            dir.mkdirs()
        ucl = URLClassLoader.newInstance(arrayOf(dir.toURI().toURL()),
                ItemManager::class.java.classLoader)
        loadDir(dir,"")
        Main.main.saveConfig()
    }

    private fun loadDir(dir:File,prefix:String){
        dir.listFiles().forEach {
            if(it.isDirectory){
                if("lib" != it.name)
                loadDir(it,prefix+"."+it.name)
            }
            else {
                if(!it.name.endsWith(".class")||it.name.contains("$"))
                    return@forEach
                val name = it.name.split("\\.".toRegex())[0]
                try {
                    val c = ucl.loadClass("$prefix.$name")
                    if (c.superclass == Item::class.java) {
                        val item = c.getConstructor().newInstance() as Item
                        registerItem0(item)
                    }
                } catch (e: Exception) {
                    logger.log(Level.SEVERE, "注册物品失败: $name", e)
                }
            }
        }
    }

    /**
     * 注册物品,可以从其他插件注册
     */
    fun registerItem(item: Item) {
        registerItem0(item)
        Main.main.saveConfig()
    }

    private var cs: JsonObject? = null

    private fun registerItem0(item: Item) {
        val classname = item.javaClass.simpleName
        cs = if (config.has(classname))
            config.getAsJsonObject(classname)
        else {
            val cf = JsonObject()
            config.add(classname, cf)
            cf
        }
        postLoads.clear()
        item.loadFeatures()
        item.require(Permission())
        item.require(Texture())
        postLoads.forEach { it.onPostLoad(main) }

        cs = null
        Main.main.server.pluginManager.registerEvents(item, Main.main)
        items[classname] = item
        logger.info("注册物品成功: $classname")
    }

    private val postLoads : MutableList<Feature.OnPostLoad> = mutableListOf()
    /**
     * 请求Feature
     * 仅供item.require()调用
     */
    fun <H:Any,T : Feature<out H>> Item.require0(feature: T): T {
        feature.item = this
        val name = feature::class.java.simpleName
        if (!cs!!.has(name)) {
            cs!!.add(name, main.gson.toJsonTree(feature.defaultData))
        }
        @Suppress("UNCHECKED_CAST")
        (feature as Feature<H>).data = main.gson.fromJson(cs!![name], feature.defaultData::class.java)

        if (feature is Feature.OnPostLoad) {
            postLoads.add(feature)
        }
        if (feature is Feature.HasListener) {
            Main.main.server.pluginManager.registerEvents(feature.listener, Main.main)
        }
        if (feature is Feature.OnDisable) {
            Main.main.addDisableListener(feature)
        }
        return feature
    }

    /**
     * 通过 ItemStack 获取 Item
     * 没有对应Item返回 null
     */
    fun getItem(item: ItemStack?): Item? {
        if (item == null || item.type == Material.AIR)
            return null
        return NBT.api.read(item)?.let { getItem(it.getString("SICN")) }
    }

    /**
     * 通过 Item.class.simpleName 查询Item
     */
    fun getItem(className: String): Item? {
        return items[className]
    }

    /**
     * 获取注册了的所有Item
     */
    fun getItems(): Collection<Item> {
        return items.values
    }
}
