package cf.wayzer.SuperItem

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class Main : JavaPlugin() {
    val gson by lazy {
        GsonBuilder().setPrettyPrinting()
//                .registerTypeAdapter(PotionEffectType::class.java, Effect.PotionEffectTypeAdapter())
                .create()!!
    }
    private val configF = File(dataFolder, "config.json")
    private lateinit var config: JsonObject
    override fun onEnable() {
        main = this
//        Thread.currentThread().contextClassLoader = Main::class.java.classLoader
        config = if (configF.exists())
            gson.fromJson(configF.readText(), JsonObject::class.java)!!
        else JsonObject()
        try {
            ItemManager.load(config)
            getCommand("SuperItem").executor = Commander()
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "发生严重错误,请关闭此插件,并与作者联系", e)
        }
    }

    override fun saveConfig() {
        configF.writeText(gson.toJson(config))
    }

    override fun onDisable() {
        set.forEach {
            it.onDisable(this)
        }
    }

    companion object {
        lateinit var main: Main
            private set
    }

    private val set = mutableSetOf<Feature.OnDisable>()

    /**
     * 添加插件Disable的Listener
     * 供Feature使用
     */
    fun addDisableListener(listener: Feature.OnDisable) {
        set.add(listener)
    }
}
