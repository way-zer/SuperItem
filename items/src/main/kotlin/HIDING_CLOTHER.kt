import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.Main
import cf.wayzer.SuperItem.features.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.potion.PotionEffectType
import java.util.*

class HIDING_CLOTHER : Item() {
    private val players = HashSet<Player>()
    private lateinit var effect: Effect

    override fun loadFeatures() {
        require(ItemInfo(Material.CHAINMAIL_CHESTPLATE, "§9隐衣", listOf("§7§o潜行时隐身!!")))
        require(CoolDown(5000))
        require(Recipe("2@381;348;0;348;303;348;0;348;0"))
        require(Durability(2000))
        effect = require(Effect(
                Effect.EffectData(PotionEffectType.INVISIBILITY, hasParticles = true)
        ))
    }

    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        if (e.plugin === Main.main)
            for (p in players)
                p.removePotionEffect(PotionEffectType.INVISIBILITY)
    }

    @EventHandler
    fun onShift(e: PlayerToggleSneakEvent) {
        val p = e.player
        if (e.isSneaking) {
            if (isItem(p.inventory.chestplate) && get<Permission>().hasPermission(p) && get<CoolDown>().isCoolDownOK(p)) {
                get<CoolDown>().add(p)
                players.add(p)
                effect.setEffect(p)
            }
        } else if (players.contains(p)) {
            p.removePotionEffect(PotionEffectType.INVISIBILITY)
            players.remove(p)
        }
    }
}
