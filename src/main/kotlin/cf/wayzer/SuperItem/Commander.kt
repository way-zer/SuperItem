package cf.wayzer.SuperItem

import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

class Commander : CommandExecutor {

    override fun onCommand(s: CommandSender, paramCommand: Command, paramString: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            when (args[0].toLowerCase()) {
                "list" -> {
                    listItem(s, args)
                    return true
                }
                "get" -> {
                    getItem(s, args)
                    return true
                }
                "give" -> {
                    giveItem(s, args)
                    return true
                }
                "load" ->{
                    loadItem(s,args)
                    return true
                }
            }
        }
        help(s)
        return true
    }

    private fun giveItem(s: CommandSender, args: Array<String>) {
        if (args.size < 3) {
            s.sendMessage("§c请输入参数")
            return
        }
        if (!s.hasPermission("SuperItem.command.give")) {
            s.sendMessage("§c没有权限")
            return
        }
        val player = Bukkit.getPlayer(args[2])
        if (player == null) {
            s.sendMessage("§c找不到玩家")
            return
        }
        val item = ItemManager.getItem(args[1]) ?: let {
            val id = Integer.valueOf(args[1]) ?: let {
                s.sendMessage("§c请输入正确的数字")
                return
            }

            val list = ItemManager.getItems().toTypedArray()
            if (id < 0 || id >= list.size) {
                s.sendMessage("§c请输入正确的ID")
                return
            }
            list[id]
        }
        if (item.givePlayer(player))
            s.sendMessage("§a给予成功")
        else{
            item.drop(player.location,player)
            player.sendMessage("§a背包已满，已掉落")
            s.sendMessage("§e背包已满，已掉落")
        }
    }

    private fun getItem(s: CommandSender, args: Array<String>) {
        if (args.size < 2) {
            s.sendMessage("§c请输入ID")
            return
        }
        if (s !is Player) {
            s.sendMessage("§c不能使用控制台运行")
            return
        }
        if (!s.hasPermission("SuperItem.command.get")) {
            s.sendMessage("§c没有权限")
            return
        }
        val item = ItemManager.getItem(args[1]) ?: let {
            val id = Integer.valueOf(args[1]) ?: let {
                s.sendMessage("§c请输入正确的数字")
                return
            }

            val list = ItemManager.getItems().toTypedArray()
            if (id < 0 || id >= list.size) {
                s.sendMessage("§c请输入正确的ID")
                return
            }
            list[id]
        }
        if (item.givePlayer(s))
            s.sendMessage("§a给予成功")
    }

    private fun listItem(s: CommandSender, args: Array<String>) {
        if (!s.hasPermission("SuperItem.command.list")) {
            s.sendMessage("§c没有权限")
            return
        }
        var pages = 1
        val list = ItemManager.getItems().toTypedArray()
        val maxpages = (list.size - 1) / 10 + 1
        if (args.size > 1) {
            pages = try {
                Integer.valueOf(args[1])
            } catch (e: Exception) {
                1
            }

        }

        if (pages < 1)
            pages = 1
        else if (pages > maxpages) {
            pages = maxpages
        }

        s.sendMessage("§a=========== §b已开启  Item 列表 §a============")
        var i = pages * 10 - 10
        while (i < list.size && i < pages * 10) {
            s.sendMessage(String.format("§e%03d §a|§e %-20s §a|§e %s",
                    i, list[i].name, list[i].get<ItemInfo>().newItemStack().itemMeta?.displayName))
            i++
        }
        s.sendMessage("§a================   §7$pages/$maxpages   §a================")
    }

    private fun loadItem(s:CommandSender, args: Array<String>){
        if (!s.hasPermission("SuperItem.command.load")) {
            s.sendMessage("§c没有权限")
            return
        }
        if (args.size < 2) {
            s.sendMessage("§c请输入Path")
            return
        }
        val file = File(ItemManager.rootDir,args[1])
        if (!file.exists())
            return s.sendMessage("§c请输入Path")
        if(file.extension!="kts")
            return s.sendMessage("§c只有脚本允许重载")
        val name = file.name.removeSuffix(".superitem.kts")
        ItemManager.getItem(name)?.let{
            s.sendMessage("§a发现旧实例存在，尝试卸载")
            ItemManager.unregisterItem(it)
            s.sendMessage("§a发现旧实例存在，卸载成功")
        }
        s.sendMessage("§a开始加载脚本，可能会有所卡顿")
        ItemManager.loadFile(file,""){
            if(it==null)return@loadFile s.sendMessage("§c脚本加载失败，查看后台以了解错误信息")
            ItemManager.registerItem(it)
            s.sendMessage("§a脚本加载成功")
        }
    }

    private fun help(s: CommandSender) {
        s.sendMessage("§c========= §c§lSuper Item§c =========")
        s.sendMessage("§a §l-------§5 By: §5§lWay__Zer §a§l-------")
        s.sendMessage("§c========== §c§l使用说明§c ==========")
        s.sendMessage("§a§l+§5/SuperItem list <页码> §e打开Item列表")
        s.sendMessage("§a§l+§5/SuperItem get <ID/ClassName> §e获取Item")
        s.sendMessage("§a§l+§5/SuperItem give <ID/ClassName> <PLAYER> §e给予玩家Item")
        s.sendMessage("§a§l+§5/SuperItem load <Path> §e加载或重载Item")
    }

}
