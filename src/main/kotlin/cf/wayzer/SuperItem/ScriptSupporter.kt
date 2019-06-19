package cf.wayzer.SuperItem

import org.bukkit.Bukkit
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.util.classpathFromClassloader
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.baseClassLoader
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.jvm

object ScriptSupporter {
    object Configuration:ScriptCompilationConfiguration({
        classpathFromClassloader(ScriptSupporter::class.java.classLoader)
        classpathFromClassloader(Bukkit::class.java.classLoader)
        defaultImports(Item::class)
        defaultImports("cf.wayzer.SuperItem.features.*")
    })
    @KotlinScript(
            displayName = "SuperItem Kotlin Script",
            fileExtension = "superitem.kts",
            compilationConfiguration = Configuration::class
    )
//    abstract class SuperItemScript
    abstract class SuperItemScript(name:String):Item.Builder(name)
    fun loadFile(f: File):ResultWithDiagnostics<EvaluationResult>{
        val conf = createJvmCompilationConfigurationFromTemplate<SuperItemScript> ()
        return BasicJvmScriptingHost().eval(f.toScriptSource(),conf, ScriptEvaluationConfiguration {
            jvm{
                baseClassLoader(ScriptSupporter::class.java.classLoader)
            }
            constructorArgs(f.name.split(".")[0].toUpperCase())
        })
    }
}