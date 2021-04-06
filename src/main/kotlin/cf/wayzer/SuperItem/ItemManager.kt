package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.Main.Companion.main
import cf.wayzer.SuperItem.features.NBT
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.scripts.ScriptSupporter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level

object ItemManager {
    private val logger = main.logger
    private val items = HashMap<String, Item>()
    private lateinit var ucl: URLClassLoader
    private var asyncLoad = AtomicInteger(0)
    lateinit var rootDir: File

    /**
     * 从文件夹加载Item
     * 供Main使用
     */
    @Throws(Exception::class)
    fun load() {
        rootDir = File(Main.main.dataFolder, "items")
        if (!rootDir.exists())
            rootDir.mkdirs()
        ucl = URLClassLoader.newInstance(arrayOf(rootDir.toURI().toURL()),
                ItemManager::class.java.classLoader)
        loadDir(rootDir, "")
        if (asyncLoad.get()>0){
            logger.info("Wait for all scripts loaded")
            while (asyncLoad.get()>0) Thread.sleep(100)
        }
    }

    private fun loadDir(dir: File, prefix: String) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                if ("lib" != file.name)
                    loadDir(file, prefix + "." + file.name)
            } else {
                val name = file.nameWithoutExtension
                loadFile(file, "$prefix.$name".substring(1)) {
                    if (it != null) registerItem0(it)
                }
            }
        }
    }

    /**
     * 供插件内部使用
     */
    @Throws(Exception::class)
    fun loadFile(file: File, className: String, callBack: (Item?) -> Unit) {
        try {
            if (file.name.endsWith("superitem.kts")) {
                ScriptSupporter.init(logger)
                logger.info("Load Item in async: ${file.name}")
                asyncLoad.incrementAndGet()
                GlobalScope.launch {
                    try {
                        callBack(ScriptSupporter.load(file))
                    }finally {
                        asyncLoad.decrementAndGet()
                    }
                }.start()
                return
            } else if (file.name.endsWith(".class") && !file.name.contains("$")) {
                val c = ucl.loadClass(className)
                if (c.superclass != Item::class.java) {
                    logger.warning("非物品Class: ${file.name}")
                    return callBack(null)
                }
                return callBack(c.getConstructor().newInstance() as Item)
            }
            return callBack(null)
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "注册物品失败: $className", e)
        }
    }

    /**
     * 注册物品,可以从其他插件注册
     */
    fun registerItem(item: Item) {
        registerItem0(item)
        ConfigManager.saveForItem(item)
    }

    /**
     * 注册物品,可以从其他插件注册,使用DSL
     */
    @Suppress("unused")
    fun registerItem(packageName: String, name: String, body: Item.Builder.() -> Unit) {
        val builder = Item.Builder(packageName, name.toUpperCase())
        body(builder)
        builder.register()
    }

    private fun registerItem0(item: Item) {
        item.loadFeatures()
        item.require(Permission())
        item.features.values.flatten().forEach {
            if (it is Feature.OnPostLoad) {
                it.onPostLoad(main)
            }
            if (it is Feature.HasListener) {
                main.server.pluginManager.registerEvents(it.listener, main)
            }
        }
        item.onEnable()
        items[item.name] = item
        main.server.pluginManager.registerEvents(item, main)
        logger.info("注册物品成功: ${item.name}")
    }

    fun unregisterItem(item: Item) {
        items.remove(item.name, item)
        HandlerList.unregisterAll(item)
        item.onDisable()
        item.features.values.flatten().forEach {
            if (it is Feature.OnDisable)
                it.onDisable(main)
        }
    }

    /**
     * 通过 ItemStack 获取 Item
     * 没有对应Item返回 null
     */
    fun getItem(item: ItemStack?): Item? {
        if (item == null || item.type == Material.AIR)
            return null
        return NBT.API.read(item)?.let { getItem(it.getString("SICN")) }
    }

    /**
     * 通过 Item.class.simpleName 查询Item
     */
    fun getItem(className: String): Item? {
        return items[className.toUpperCase()]
    }

    /**
     * 获取注册了的所有Item
     */
    fun getItems(): Collection<Item> {
        return items.values
    }
}
