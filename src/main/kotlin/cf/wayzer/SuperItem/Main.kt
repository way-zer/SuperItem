package cf.wayzer.SuperItem

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class Main : JavaPlugin() {
    override fun onEnable() {
        main = this
        try {
            ConfigManager.init(dataFolder)
            ItemManager.load()
            ConfigManager.saveAll()
            getCommand("SuperItem")?.setExecutor(Commander())
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "发生严重错误,请关闭此插件,并与作者联系", e)
        }
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
