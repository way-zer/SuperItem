package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.script.experimental.api.*
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

class ScriptLoader {
    lateinit var logger: Logger
    private val hostConfiguration by lazy { ScriptingHostConfiguration(defaultJvmScriptingHostConfiguration){
        configurationDependencies(JvmDependency(classpathFromClassloader(Main::class.java.classLoader) ?:listOf()))
    }}
    private val compilationConfiguration by lazy {createJvmCompilationConfigurationFromTemplate<SuperItemScript> (hostConfiguration) }

    fun load(file: File):Item?{
        var item:Item?=null
        val result = load0(file)
        result.onSuccess {
            val res = result.valueOrThrow().returnValue
            if(res.scriptInstance is Item) {
                item = (res.scriptInstance as Item)
                result.reports.filterNot { it.severity== ScriptDiagnostic.Severity.DEBUG }.forEachIndexed { index, rep ->
                    logger.log(Level.WARNING,"##$index##"+rep.message,rep.exception)
                }
                return@onSuccess ResultWithDiagnostics.Success(res)
            } else {
                return@onSuccess ResultWithDiagnostics.Failure(ScriptDiagnostic("非物品Kts: ${file.name}"))
            }
        }.onFailure {
            logger.warning("物品Kts加载失败: ")
            it.reports.forEachIndexed { index, rep ->
                logger.log(Level.WARNING,"##$index##"+rep.message,rep.exception)
            }
        }
        return item
    }

    private fun load0(f: File):ResultWithDiagnostics<EvaluationResult>{
        return BasicJvmScriptingHost(hostConfiguration).eval(f.toScriptSource(),compilationConfiguration, ScriptEvaluationConfiguration {
            jvm {
                baseClassLoader(ScriptLoader::class.java.classLoader)
            }
            constructorArgs(f.name.split(".")[0].toUpperCase())
        })
    }
}
