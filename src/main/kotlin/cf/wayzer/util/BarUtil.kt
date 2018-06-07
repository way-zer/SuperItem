package cf.wayzer.util

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 用于发送Bar消息
 */
object BarUtil {
    var mode = 2
    private var loaded = false
    private val version: String
        get() {
            val packageName = Bukkit.getServer().javaClass.`package`.name
            return packageName.substring(packageName.lastIndexOf(".") + 1)
        }
    lateinit var class_CraftPlayer: Class<*>
    lateinit var class_EntityPlayer: Class<*>
    lateinit var class_IChatBaseComponent: Class<*>
    lateinit var class_PacketPlayOutChat: Class<*>
    lateinit var class_ChatSerializer: Class<*>
    lateinit var class_PlayerConnection: Class<*>
    lateinit var class_Packet: Class<*>
    lateinit var constructor_PacketPlayOutChat: Constructor<*>
    lateinit var method_a: Method
    lateinit var method_sendPacket: Method
    lateinit var method_getHandle: Method
    lateinit var field_playerConnection: Field

    fun load() {
        if (loaded) return
        try {
            class_CraftPlayer = Class.forName("org.bukkit.craftbukkit.$version.entity.CraftPlayer")
            class_EntityPlayer = Class.forName("net.minecraft.server.$version.EntityPlayer")
            class_IChatBaseComponent = Class.forName("net.minecraft.server.$version.IChatBaseComponent")
            class_PacketPlayOutChat = Class.forName("net.minecraft.server.$version.PacketPlayOutChat")
            class_ChatSerializer = Class
                    .forName("net.minecraft.server.$version.IChatBaseComponent\$ChatSerializer")
            class_PlayerConnection = Class
                    .forName("net.minecraft.server.$version.PlayerConnection")
            class_Packet = Class
                    .forName("net.minecraft.server.$version.Packet")

            field_playerConnection = class_EntityPlayer.getField("playerConnection")
            constructor_PacketPlayOutChat = class_PacketPlayOutChat.getConstructor(class_IChatBaseComponent,
                    Byte::class.javaPrimitiveType)
            method_getHandle = class_CraftPlayer.getMethod("getHandle")
            method_a = class_ChatSerializer.getMethod("a", String::class.java)
            method_sendPacket = class_PlayerConnection.getMethod("sendPacket", class_Packet)
        } catch (e: Exception) {
            System.err.println("[SuperItem]服务器版本异常,Bar无法正常运行,使用备用模式")
            e.printStackTrace()
            mode = 0
        }
        loaded = true
    }

    /**
     * 发送Bar消息给玩家
     */
    fun sendToPlayer(player: Player, message: String) {
        when (mode) {
            2 -> {
                mode = 1
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message))
                mode = 2
            }
            1 -> {
                load()
                try {
                    val actionComponent = method_a.invoke(null, "{\"text\":\"${cleanMessage(message)}\"}")
                    val actionPacket = constructor_PacketPlayOutChat.newInstance(actionComponent, 2.toByte())
                    method_sendPacket.invoke(field_playerConnection.get(method_getHandle.invoke(player)), actionPacket)
                } catch (e: Exception) {
                    System.err.println("[SuperItem]发送Bar失败,使用备用模式")
                    e.printStackTrace()
                    mode = 0
                }
            }
            else -> player.sendMessage("§a§l[SuperItem]§e" + message)
        }
    }

    private fun cleanMessage(message: String) = if (message.length > 64) message.substring(0, 63) else message
}
