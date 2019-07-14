package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.scripts.SuperItemScript
import cf.wayzer.libraryManager.Dependency
import cf.wayzer.libraryManager.LibraryManager
import org.bukkit.Bukkit
import org.jetbrains.kotlin.script.util.DependsOn
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.configurationDependencies
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.*
import kotlin.script.experimental.jvm.util.classpathFromClass
import kotlin.script.experimental.jvm.util.classpathFromClassloader
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

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
        jvm {
            dependenciesFromClass(ScriptSupporter::class.java.classLoader,Bukkit::class,Item::class)
        }
        defaultImports(Item::class, DependsOn::class)
        defaultImports.append("cf.wayzer.SuperItem.features.*")
        refineConfiguration{
            onAnnotations(DependsOn::class){ context->
                val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.filter { it.annotationClass== DependsOn::class }?: listOf()
                val classes = annotations.map { Class.forName((it as DependsOn).value).kotlin}
                val diagnostics = classes.map { ScriptDiagnostic("[Info]PluginDependency: ${it.java.name}") }
                ScriptCompilationConfiguration(context.compilationConfiguration) {
                    jvm{
                        dependenciesFromClass(ScriptSupporter::class.java.classLoader, *classes.toTypedArray())
                    }
                }.asSuccess(diagnostics)
            }
        }
    })

    fun loadFile(f: File):ResultWithDiagnostics<EvaluationResult>{
        LibraryManager(Paths.get("./libs/")).apply {
            addMavenCentral()
            require(Dependency("org.jetbrains.kotlin:kotlin-script-runtime:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-common:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.intellij.deps:trove4j:1.0.20181211"))
            require(Dependency("org.jetbrains.kotlin:kotlin-daemon-client:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-jvm-host:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-compiler:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-scripting-compiler-impl:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-compiler:${Main.kotlinVersion}"))
            require(Dependency("org.jetbrains.kotlin:kotlin-script-util:${Main.kotlinVersion}"))
            loadToClasspath()
        }

        val hostConfiguration = ScriptingHostConfiguration(defaultJvmScriptingHostConfiguration){
            configurationDependencies(JvmDependency(classpathFromClassloader(Main::class.java.classLoader)?:listOf()))
        }
        val conf = createJvmCompilationConfigurationFromTemplate<SuperItemScript> (hostConfiguration)
        return BasicJvmScriptingHost(hostConfiguration).eval(f.toScriptSource(),conf, ScriptEvaluationConfiguration {
            jvm {
                baseClassLoader(ScriptSupporter::class.java.classLoader)
            }
            constructorArgs(f.name.split(".")[0].toUpperCase())
        })
    }
}