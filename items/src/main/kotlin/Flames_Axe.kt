import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.ItemInfo
import cf.wayzer.SuperItem.features.Permission
import cf.wayzer.SuperItem.features.Recipe
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent

class Flames_Axe : Item() {

    override fun loadFeatures() {
        require(ItemInfo(Material.IRON_AXE, "§4FLAMES AXE", listOf("§3 Use wisely")) { im, _ ->
            im.addEnchant(Enchantment.FIRE_ASPECT, 2, false)
            im.addEnchant(Enchantment.DAMAGE_ALL, 4, false)
        })

        require(CoolDown(600))
        require(Recipe("2@0;377;399;377;258;377;399;377;0"))
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            val p = e.damager as Player
            if (isItem(p.inventory.itemInMainHand)) {
                if (get<Permission>().hasPermission(p) && get<CoolDown>().isCoolDownOK(p)) {
                    get<CoolDown>().add(p)
                } else
                    e.damage = 1.0
            }
        }
    }

}
