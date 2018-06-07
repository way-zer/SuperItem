package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import java.io.File

/**
 * **自动Require
 * 设置自定义材质属性
 * 贴图加载于 texture/{name}.png
 * pack/下的pack.png和pack.mcmeta需要自行编写
 * 材质包会在 pack/ 下生成,zip打包其下所有文件即可即可使用
 */
class Texture(private val default: String? = null) : Feature<String>(), Feature.OnPostLoad {
    override val defaultData
        get() = default ?: "${item.name}.png"

    data class Data(val filename: String)

    override fun onPostLoad(main: Main) {
        val inF = File(main.dataFolder, "texture")
        val outF = File(main.dataFolder, "pack/assets/minecraft/mcpatcher/cit/superitems")
        inF.mkdirs();outF.mkdirs()

        val inFile = File(inF, data)
        val outFile = File(outF, "${item.name}.png")
        val cF = File(outF, "${item.name}.properties")

        if (!data.isEmpty() && inFile.exists()) {
            if (!outFile.exists())
                inFile.copyTo(outFile, false)
            if (!cF.exists()) {
                val cfText = """
                    |type=item
                    |items=${item.item.type.name.toLowerCase()}
                    |texture=${item.name}
                    |nbt.SICN=${item.name}
                """.trimMargin()
//                cF.createNewFile()
                cF.writeText(cfText)
            }
        }
    }
}