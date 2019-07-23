package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClass

object CompilationConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromClass(Bukkit::class, Item::class)
    }
    defaultImports(Item::class, ImportClass::class,Material::class)
    defaultImports.append("cf.wayzer.SuperItem.features.*")
    refineConfiguration{
        onAnnotations(ImportClass::class){ context->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.filter { it.annotationClass== ImportClass::class }?: listOf()
            val classes = annotations.map { Class.forName((it as ImportClass).name).kotlin}
            val diagnostics = classes.map { ScriptDiagnostic("[Info]PluginDependency: ${it.java.name}",ScriptDiagnostic.Severity.INFO) }
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                jvm {
                    dependenciesFromClass(*classes.toTypedArray())
                }
            }.asSuccess(diagnostics)
        }
    }
})
private fun JvmScriptCompilationConfigurationBuilder.dependenciesFromClass(vararg classes: KClass<out Any>) {
    classes.flatMap {c->
        val classLoader = c.java.classLoader
        classpathFromClass(classLoader,c) ?:let {
            val clp = "${c.java.canonicalName.replace('.', '/')}.class"
            val url = classLoader.getResource(clp)
            url?.toURI()?.schemeSpecificPart?.let { listOf(File(it.removePrefix("file:").split("!")[0])) }
        }?: emptyList()
    }.let(this::updateClasspath)
}