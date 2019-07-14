package cf.wayzer.SuperItem

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
            getCommand("SuperItem").executor = Commander()
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "发生严重错误,请关闭此插件,并与作者联系", e)
        }
    }

    override fun onDisable() {
        ItemManager.getItems().forEach(ItemManager::unregisterItem)
    }

    companion object {
        lateinit var main: Main
            private set
        init {
            val kotlinVersion = "1.3.31"
            LibraryManager(Paths.get("./libs/")).apply {
                addMavenCentral()
                require(Dependency("org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"))
                require(Dependency("org.jetbrains:annotations:13.0"))
                require(Dependency("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.1"))
                require(Dependency("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"))
                require(Dependency("org.jetbrains.intellij.deps:trove4j:1.0.20181211"))
                require(Dependency("org.jetbrains.kotlin:kotlin-daemon-client:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-scripting-compiler:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-scripting-impl:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"))
                require(Dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"))

            }
        }
    }
}
