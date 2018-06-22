import cf.wayzer.SuperItem.Item
import cf.wayzer.SuperItem.features.CoolDown
import cf.wayzer.SuperItem.features.Effect
import cf.wayzer.SuperItem.features.ItemInfo
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.potion.PotionEffectType
import java.util.ArrayDeque

class MudWalker : Item() {
    override fun loadFeatures() {
        require(ItemInfo(Material.DIAMOND_BOOTS,
                "${ChatColor.DARK_PURPLE}泥浆游荡者",
                listOf("${ChatColor.WHITE}当你潜行时产生 5 秒的${ChatColor.GRAY}缓慢 I${ChatColor.RESET}效果",
                        "${ChatColor.ITALIC}（冷却 10 秒）")))
        require(CoolDown(10000))
        sfx = require(Effect(Effect.EffectData(PotionEffectType.SLOW, 100, 1)))
        scheduleStuff()
    }

    @EventHandler
    fun onPlayerCrouch(e: PlayerToggleSneakEvent) {
        if (isItem(e.player.inventory.boots)
                && permission.hasPermission(e.player)
                && coolDown.isCoolDownOK(e.player)) {
            if (e.isSneaking) {
                addEffectRing(e.player.world, e.player.location, e.player)
                coolDown.add(e.player)
            }
        }
    }

    data class EffectRing(val expireWhen: Long, val world: World, val location: Location, val radiusSquared: Int, val avoid: Player)

    private fun addEffectRing(world: World, location: Location, avoid: Player) {
        effectRings.addLast(EffectRing(System.currentTimeMillis() + 5000, world, location, 2, avoid))
    }

    private fun scheduleStuff() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                Item.pluginMain,
                {
                    val now = System.currentTimeMillis()
                    if (effectRings.isNotEmpty()) {
                        effectRings.forEach {
                            val players = it.world.players
                            players.remove(it.avoid)
                            val location = it.location
                            val radius = it.radiusSquared
                            it.world.spawnParticle(Particle.TOTEM, it.location, 20, 1.0, 1.0, 1.0)
                            players.forEach {
                                if (location.distanceSquared(it.location) < radius) {
                                    sfx.setEffect(it)
                                }
                            }
                        }
                        while (effectRings.first.expireWhen < now) {
                            effectRings.removeFirst()
                        }
                    }
                },
                0,
                2
        )
    }

    val effectRings = ArrayDeque<EffectRing>()
    private lateinit var sfx: Effect
}
