package cf.wayzer.SuperItem

import org.bukkit.Bukkit
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClass
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.baseClassLoader
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate
import kotlin.script.experimental.jvmhost.jvm

object ScriptSupporter {
    private fun JvmScriptCompilationConfigurationBuilder.dependenciesFromClass(classLoader: ClassLoader,vararg classes:KClass<out Any>) {
        classes.flatMap {c->
            classpathFromClass(classLoader,c)?:let {
                val clp = "${c.java.canonicalName.replace('.', '/')}.class"
                val url = classLoader.getResource(clp)
                url?.toURI()?.schemeSpecificPart?.let { listOf(File(it.removePrefix("file:").split("!")[0])) }
            }?: emptyList()
        }.let(this::updateClasspath)
    }
    object Configuration:ScriptCompilationConfiguration({
        jvm{
            dependenciesFromClass(javaClass.classLoader,Bukkit::class,Item::class)
        }
        defaultImports(Item::class)
        defaultImports.append("cf.wayzer.SuperItem.features.*")
    })
    @KotlinScript(
            displayName = "SuperItem Kotlin Script",
            fileExtension = "superitem.kts",
            compilationConfiguration = Configuration::class
    )
    abstract class SuperItemScript(name:String):Item.Builder("scripts",name)
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