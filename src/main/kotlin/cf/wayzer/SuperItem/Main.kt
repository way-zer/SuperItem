package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.features.DataStore
import cf.wayzer.libraryManager.Dependency
import cf.wayzer.libraryManager.LibraryManager
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Paths
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
        DataStore.fileDB.close()
        DataStore.memoryDB.close()
        ItemManager.getItems().toList().forEach(ItemManager::unregisterItem)
    }

    companion object {
        const val kotlinVersion = "1.3.61"
        lateinit var main: Main
            private set
        init {
            LibraryManager(Paths.get("./libs/")).apply {
                addAliYunMirror()
                require(Dependency("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"))
                require(Dependency("org.jetbrains:annotations:13.0"))

                require(Dependency("org.mapdb:mapdb:3.0.7"))
                require(Dependency("org.eclipse.collections:eclipse-collections:10.1.0"))
                require(Dependency("org.eclipse.collections:eclipse-collections-api:10.1.0"))
                require(Dependency("org.eclipse.collections:eclipse-collections-forkjoin:10.1.0"))
                require(Dependency("com.google.guava:guava:28.1-jre"))
                require(Dependency("net.jpountz.lz4:lz4:1.3.0"))
                require(Dependency("org.mapdb:elsa:3.0.0-M5"))
                loadToClassLoader(Main::class.java.classLoader)
            }
        }
    }
}
