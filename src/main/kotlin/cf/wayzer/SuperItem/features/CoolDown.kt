package cf.wayzer.SuperItem.features

import cf.wayzer.SuperItem.Feature
import cf.wayzer.SuperItem.Main
import cf.wayzer.util.BarUtil
import org.bukkit.entity.Player
import java.util.*

/**
 * 冷却功能
 * @param defaultTime 默认冷却时间(ms)
 */
class CoolDown(override val defaultData: Long) : Feature<Long>(), Feature.OnPostLoad {
    private val coolDown = mutableMapOf<UUID, Long>()

    /**
     * 为玩家设置冷却
     */
    fun add(p: Player) {
        coolDown[p.uniqueId] = System.currentTimeMillis() + data
    }

    /**
     * 判断冷却是否完成
     * @param tip 是否发送提示
     */
    fun isCoolDownOK(p: Player, tip: Boolean = true): Boolean {
        val time = coolDown.getOrDefault(p.uniqueId, 0L)
        return if (time > System.currentTimeMillis()) {
            if (tip)
                BarUtil.sendToPlayer(p, "§c===§c§l冷却中 §7( §e" + (time - System.currentTimeMillis()) + "Ms §7) §c===")
            false
        } else {
            true
        }
    }

    /**
     * 取消玩家的冷却
     */
    fun remove(p: Player) {
        coolDown.remove(p.uniqueId)
    }

    override fun onPostLoad(main: Main) {
        item.coolDown = this
    }
}