package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import org.bukkit.scheduler.BukkitRunnable

class ScheduleTask : Feature<Nothing>(),Feature.OnDisable {
    private val list = mutableListOf<BukkitRunnable>()
    override val defaultData: Nothing? = null
    override fun onDisable(main: Main) {
        list.forEach { it.cancel() }
    }
    fun create(autoCancel:Boolean =true,runH: BukkitRunnable.()->Unit):BukkitRunnable{
        val runnable = object: BukkitRunnable(){
            override fun run(){
                cancel()
                runH(this)
                if(autoCancel&&isCancelled)
                    list.remove(this)
            }
        }
        if(autoCancel)list.add(runnable)
        return runnable
    }
}
