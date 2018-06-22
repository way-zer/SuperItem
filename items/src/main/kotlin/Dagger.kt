import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.util.BarUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class Dagger : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.FEATHER, "§f匕首", listOf("§5§o偷袭目标造成三倍伤害")))

        require(CoolDown(500))
    }

    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            val p = e.damager as Player
            if (isItem(p.inventory.itemInMainHand) && permission.hasPermission(p)) {
                if (coolDown.isCoolDownOK(p) && isBack(e.entity.location, p)) {
                    coolDown.add(p)
                    e.damage = (e.damage + 3) * 3
                    BarUtil.sendToPlayer(p, "§a===§e偷袭成功§a===")
                } else {
                    e.damage = e.damage + 4
                }
            }
        }
    }

    private fun isBack(entity: Location, p: Player): Boolean {
        return entity.direction.dot(p.location.direction) > 0.0
    }

}
