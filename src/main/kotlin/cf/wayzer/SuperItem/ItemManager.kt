package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.Main.Companion.main
import cf.wayzer.SuperItem.features.NBT
import cf.wayzer.SuperItem.features.Permission
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.logging.Level
import kotlin.script.experimental.api.*

object ItemManager {
    private val logger = main.logger
    private val items = HashMap<String, Item>()
    private lateinit var ucl:URLClassLoader

    /**
     * 从文件夹加载Item
     * 供Main使用
     */
    @Throws(Exception::class)
    fun load() {
        val dir = File(Main.main.dataFolder, "items")
        if (!dir.exists())
            dir.mkdirs()
        ucl = URLClassLoader.newInstance(arrayOf(dir.toURI().toURL()),
                ItemManager::class.java.classLoader)
        loadDir(dir,"")
        Main.main.saveConfig()
    }

    private fun loadDir(dir:File,prefix:String){
        dir.listFiles()?.forEach { file ->
            if(file.isDirectory){
                if("lib" != file.name)
                loadDir(file,prefix+"."+file.name)
            }
            else {
                try {
                    val item:Item
                    if(file.name.endsWith("superitem.kts")){
                        logger.info("Load Item in async: ${file.name}")
                        run {
                            val result = ScriptSupporter.loadFile(file)
                            result.onSuccess {
                                val res = result.resultOrNull()!!.returnValue
                                if(res is ResultValue.Value && res.value is ScriptSupporter.SuperItemScript) {
                                    (res.value as ScriptSupporter.SuperItemScript).register()
                                    result.reports.forEachIndexed { index, rep ->
                                        logger.log(Level.WARNING,"##$index##"+rep.message,rep.exception)
                                    }
                                    return@onSuccess ResultWithDiagnostics.Success(ResultValue.Unit)
                                } else {
                                    return@onSuccess ResultWithDiagnostics.Failure(ScriptDiagnostic("非物品Kts: ${file.name}"))
                                }
                            }.onFailure {
                                logger.warning("物品Kts加载失败: ")
                                it.reports.forEachIndexed { index, rep ->
                                    logger.log(Level.WARNING,"##$index##"+rep.message,rep.exception)
                                }
                            }
                        }
                    }else if (file.name.endsWith(".class")&&!file.name.contains("$")){
                        val name = file.nameWithoutExtension
                        val c = ucl.loadClass("$prefix.$name".substring(1))
                        if (c.superclass != Item::class.java) {
                            logger.warning("非物品Class: ${file.name}")
                            return
                        }
                        item = c.getConstructor().newInstance() as Item
                        registerItem0(item)
                    }
                } catch (e: Exception) {
                    logger.log(Level.SEVERE, "注册物品失败: ${file.nameWithoutExtension}", e)
                }
            }
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
    fun registerItem(packageName:String,name:String, body: Item.Builder.()->Unit) {
        val builder = Item.Builder(packageName,name.toUpperCase())
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
            if (it is Feature.OnDisable) {
                main.addDisableListener(it)
            }
        }
        main.server.pluginManager.registerEvents(item, main)
        logger.info("注册物品成功: ${item.name}")
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
