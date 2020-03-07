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
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.JvmScriptCompilationConfigurationBuilder
import kotlin.script.experimental.jvm.dependenciesFromClassloader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.classpathFromClass

object CompilationConfiguration: ScriptCompilationConfiguration({
    ide{

    }
    jvm {
        dependenciesFromClassloader(
                "kotlin-stdlib",
                "mapdb"
                ,classLoader = ScriptLoader::class.java.classLoader)
        dependenciesFromClass(Bukkit::class, Item::class)
    }
    defaultImports(Item::class, ImportClass::class,MavenDepends::class,ImportScript::class,Material::class)
    defaultImports.append("cf.wayzer.SuperItem.features.*")
    refineConfiguration{
        onAnnotations(ImportClass::class,MavenDepends::class,ImportScript::class) {context->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)
            val diagnostics = mutableListOf<ScriptDiagnostic>()
            val scriptBaseDir = (context.script as? FileBasedScriptSource)?.file?.parentFile

            val importClasses = mutableListOf<KClass<out Any>>()
            val dependencies = mutableListOf<Dependency>()
            val importScriptSources = mutableListOf<FileScriptSource>()

            annotations?.forEach {
                when(it){
                    is ImportClass -> {
                        try {
                            Class.forName(it.name).kotlin.also {cls->
                                importClasses.add(cls)
                                diagnostics.add(ScriptDiagnostic("[Info]PluginDependency: ${cls.java.name}",ScriptDiagnostic.Severity.INFO))
                            }
                        }catch (e : ClassNotFoundException){
                            diagnostics.add(ScriptDiagnostic("Can't find ImportClass: ${it.name}",ScriptDiagnostic.Severity.FATAL))
                        }
                    }
                    is MavenDepends -> {
                        Dependency(it.name,it.repo).also {d->
                            dependencies.add(d)
                            ScriptDiagnostic("[Info]MavenDependency: $d",ScriptDiagnostic.Severity.INFO)
                        }
                    }
                    is ImportScript -> {
                        importScriptSources.add(FileScriptSource(scriptBaseDir?.resolve(it.path) ?: File(it.path)))
                        ScriptDiagnostic("[Info]ImportScript: ${it.path}",ScriptDiagnostic.Severity.INFO)
                    }
                }
            }
            ScriptCompilationConfiguration(context.compilationConfiguration) {
                defaultImports.invoke(*importClasses.toTypedArray())
                jvm {
                    dependenciesFromClass(*importClasses.toTypedArray())

                    if(dependencies.isNotEmpty())
                    LibraryManager(Paths.get("libs")).apply {
                        addAliYunMirror()
                        dependencies.forEach {
                            require(it)
                        }
                        loadToClassLoader(javaClass.classLoader)
                        updateClasspath(loadFiles())
                    }
                }
                importScripts.append(importScriptSources)
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
