import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.NBT
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.util.Vector

class PortableBlackhole : Item() {
    override fun loadFeatures() {
        require(ItemInfo(
                Material.DIAMOND_HELMET,
                "${ChatColor.GOLD}便携黑洞",
                listOf("${ChatColor.WHITE}对目标造成伤害时使其被向你的方向拉扯")))
        require(NBT(
                NBT.AttributeModifier(NBT.AttributeType.Armor, 3.0, NBT.AttributeOperation.Additive)
        ))
        require(NBT(
                NBT.AttributeModifier(NBT.AttributeType.ArmorToughness, 2.0, NBT.AttributeOperation.Additive)
        ))
    }

    @EventHandler
    fun onHit(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            val player = e.damager as Player
            if (isItem(player.inventory.helmet) && permission.hasPermission(player)) {
                val playerPos = player.location
                val victimPos = e.entity.location
                val velocity = playerPos.subtract(victimPos).toVector().normalize().multiply(1.2).add(Vector(.0, .2, .0))
                e.entity.velocity.add(velocity)
            }
        }
    }
}