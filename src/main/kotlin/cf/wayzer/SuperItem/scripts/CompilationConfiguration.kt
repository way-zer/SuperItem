package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.ScriptSupporter
import org.bukkit.Bukkit
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClass

object CompilationConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromClass(ScriptSupporter::class.java.classLoader, Bukkit::class, Item::class)
    }
    defaultImports(Item::class, ImportClass::class)
    defaultImports.append("cf.wayzer.SuperItem.features.*")
    refineConfiguration{
        onAnnotations(JvmName::class){ context->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.filter { it.annotationClass== JvmName::class }?: listOf()
            val classes = annotations.map { Class.forName((it as JvmName).name).kotlin}
            val diagnostics = classes.map { ScriptDiagnostic("[Info]PluginDependency: ${it.java.name}",ScriptDiagnostic.Severity.INFO) }
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                jvm {
                    dependenciesFromClass(ScriptSupporter::class.java.classLoader, *classes.toTypedArray())
                }
            }.asSuccess(diagnostics)
        }
    }
})
private fun JvmScriptCompilationConfigurationBuilder.dependenciesFromClass(classLoader: ClassLoader, vararg classes: KClass<out Any>) {
    classes.flatMap {c->
        classpathFromClass(classLoader,c) ?:let {
            val clp = "${c.java.canonicalName.replace('.', '/')}.class"
            val url = classLoader.getResource(clp)
            url?.toURI()?.schemeSpecificPart?.let { listOf(File(it.removePrefix("file:").split("!")[0])) }
        }?: emptyList()
    }.let(this::updateClasspath)
}