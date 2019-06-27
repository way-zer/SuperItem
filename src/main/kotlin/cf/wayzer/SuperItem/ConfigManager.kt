package cf.wayzer.SuperItem

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.extract
import io.github.config4k.toConfig
import java.io.File

object ConfigManager{
//    lateinit var rootConfig:Config
    lateinit var rootDir:File
    val map = mutableMapOf<Item,Config>()
    private val renderOptions = ConfigRenderOptions.defaults()
            .setJson(false)
            .setOriginComments(false)

    fun saveForItem(item:Item){
        item.configFile.writeText(item.config.root().render(renderOptions))
    }

    fun <H : Any> loadForFeature(item: Item, feature: Feature<H>){
        val config = item.config.withFallback { feature.defaultData.toConfig(feature.name)  }
        feature.data=feature.defaultData::class.java.cast(config.extract(feature.name))
        map[item]=config
    }

    fun init(pluginDir: File){
        rootDir= File(pluginDir,"configs")
//        rootConfig=ConfigFactory.parseFile(file)
    }

    fun saveAll(){
        map.keys.forEach(::saveForItem)
    }

    private fun readFile(file: File):Config{
        if(!file.exists())file.createNewFile()
        return ConfigFactory.parseFile(file)
    }

    private val Item.config:Config
            get() = map.getOrPut(this){ readFile(configFile) }

    private val Item.configFile:File
            get() = File(rootDir, "$packageName ${File.pathSeparator} $name.conf")
}