package cf.wayzer.SuperItem

import org.bukkit.event.Listener

abstract class Feature<H> where H : Any {
    /**
     * 当此Feature加载完时调用
     */
    interface OnPostLoad {
        fun onPostLoad(main: Main)
    }

    /**
     * 当插件关闭时调用
     */
    interface OnDisable {
        fun onDisable(main: Main)
    }

    /**
     * 向插件注册Listener
     */
    interface HasListener {
        val listener: Listener
    }

    /**
     * 绑定的Item
     */
    lateinit var item: Item
    abstract val defaultData: H
    /**
     * Feature的配置信息
     */
    lateinit var data: H
    val name:String
        get() = javaClass.simpleName
}
