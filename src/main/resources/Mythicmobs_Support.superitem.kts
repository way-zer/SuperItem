@file:PluginDependency("io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent")

import cf.wayzer.SuperItem.ItemManager
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import io.lumine.xikage.mythicmobs.drops.droppables.*
import org.bukkit.Material

require(ItemInfo(Material.NAME_TAG,"&cMythicmobs_Support", listOf("&c This Item doesn't have other effect")))

listen<MythicDropLoadEvent>{
    val name = (it.dropName as String).toUpperCase()
    if(!name.startsWith("SI_"))return@listen
    val item = ItemManager.getItem(name.substring(3))
    if(item==null)it.register(NothingDrop(it.config.line,it.config))
    else it.register(ItemDrop(it.config.line,it.config,BukkitItemStack(item.get<ItemInfo>().newItemStack())))
}

