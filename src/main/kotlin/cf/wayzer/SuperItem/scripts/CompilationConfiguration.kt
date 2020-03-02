package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import cf.wayzer.libraryManager.Dependency
import cf.wayzer.libraryManager.LibraryManager
import org.bukkit.Bukkit
import org.bukkit.Material
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClass

object CompilationConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromClassloader(
                "kotlin-stdlib"
                ,classLoader = ScriptLoader::class.java.classLoader)
        dependenciesFromClass(Bukkit::class, Item::class)
    }
    defaultImports(Item::class, ImportClass::class,MavenDepends::class,Material::class)
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
        onAnnotations(MavenDepends::class){ context->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.filter { it.annotationClass== MavenDepends::class }?: listOf()
            val dependencies = annotations.map {Dependency((it as MavenDepends).name,it.repo)}
            val diagnostics = dependencies.map { ScriptDiagnostic("[Info]MavenDependency: $it",ScriptDiagnostic.Severity.INFO) }
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                jvm {
                    LibraryManager(Paths.get("lib")).apply {
                        addAliYunMirror()
                        dependencies.forEach {
                            require(it)
                        }
                        loadToClassLoader(javaClass.classLoader)
                        updateClasspath(loadFiles())
                    }
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
