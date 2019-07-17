package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.scripts.SuperItemScript
import cf.wayzer.libraryManager.Dependency
import cf.wayzer.libraryManager.LibraryManager
import java.io.File
import java.nio.file.Paths
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.constructorArgs
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.configurationDependencies
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.classpathFromClassloader
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object ScriptSupporter {
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