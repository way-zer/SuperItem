package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.ScriptSupporter
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
        displayName = "SuperItem Kotlin Script",
        fileExtension = "superitem.kts",
        compilationConfiguration = ScriptSupporter.Configuration::class
)
abstract class SuperItemScript(name:String): Item.Builder("scripts",name)