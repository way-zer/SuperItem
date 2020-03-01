package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(
        displayName = "SuperItem Kotlin Script",
        fileExtension = "superitem.kts",
        compilationConfiguration = CompilationConfiguration::class
)
open class SuperItemScript(name:String): Item.Builder("scripts",name)