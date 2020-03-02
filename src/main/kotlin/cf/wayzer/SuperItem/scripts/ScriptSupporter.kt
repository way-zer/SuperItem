package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import cf.wayzer.libraryManager.Dependency
import cf.wayzer.libraryManager.LibraryManager
import java.io.File
import java.nio.file.Paths
import java.util.logging.Logger

object ScriptSupporter {
    lateinit var loader:ScriptLoader
    private var inited=false
    fun init(logger:Logger){
        if(inited)return
        LibraryManager(Paths.get("./libs/")).apply {
            addAliYunMirror()
            require(Dependency("org.jetbrains.kotlin:kotlin-script-runtime:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-common:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.intellij.deps:trove4j:1.0.20181211"))
            require(Dependency("org.jetbrains.kotlin:kotlin-daemon-client:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm-host:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-compiler:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-compiler-impl:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-compiler:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-script-util:${Main.kotlinVersion}"))
        }.loadToClassLoader(Main::class.java.classLoader)
        loader = Class.forName("cf.wayzer.SuperItem.scripts.ScriptLoader").newInstance() as ScriptLoader
        loader.logger=logger
        inited =true
    }

    fun load(file: File): Item?{
        if(!inited)throw IllegalStateException("Must init first!!")
        return loader.load(file)
    }
}
