package cf.wayzer.SuperItem.scripts

import cf.wayzer.SuperItem.Item
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
        configurationDependencies(JvmDependency(classpathFromClassloader(ScriptLoader::class.java.classLoader) ?:listOf()))
    }}
    private val compilationConfiguration by lazy {createJvmCompilationConfigurationFromTemplate<SuperItemScript> (hostConfiguration) }

    suspend fun load(file: File):Item?{
        var item:Item?=null
        val result = load0(file)
        result.onSuccess {
            val res = result.valueOrThrow().returnValue
            if(res.scriptInstance is Item) {
                item = (res.scriptInstance as Item)
                result.reports.filterNot { it.severity== ScriptDiagnostic.Severity.DEBUG }.forEachIndexed { index, rep ->
                    logger.log(Level.WARNING,"##$index##"+rep.message+rep.location?.let { "($it)" },rep.exception)
                }
                return@onSuccess ResultWithDiagnostics.Success(res)
            } else {
                return@onSuccess ResultWithDiagnostics.Failure(ScriptDiagnostic("非物品Kts: ${file.name}: ${res.scriptInstance}"))
            }
        }.onFailure {
            logger.warning("物品Kts加载失败: ")
            it.reports.forEachIndexed { index, rep ->
                logger.log(Level.WARNING,"##$index##"+rep.message,rep.exception)
            }
        }
        return item
    }

    private val host by lazy {BasicJvmScriptingHost(hostConfiguration)}
    private suspend fun load0(f: File):ResultWithDiagnostics<EvaluationResult>{
        return host.compiler(f.toScriptSource(),compilationConfiguration).onSuccess {script->
            host.evaluator(script,ScriptEvaluationConfiguration {
                jvm {
                    baseClassLoader(ScriptLoader::class.java.classLoader)
                    enableScriptsInstancesSharing()
                }
                constructorArgs(f.name.split(".")[0].toUpperCase())
            })
        }
    }
}
