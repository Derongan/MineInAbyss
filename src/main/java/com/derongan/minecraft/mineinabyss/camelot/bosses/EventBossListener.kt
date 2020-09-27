package com.derongan.minecraft.mineinabyss.camelot.bosses

import com.derongan.minecraft.mineinabyss.camelot.Events
import com.derongan.minecraft.mineinabyss.configuration.MineInAbyssMainConfig
import com.google.common.util.concurrent.AtomicDouble
import com.mineinabyss.idofront.messaging.color
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.roundToInt

object EventBossListener : Listener {
    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        Events.registedBosses[e.entity.uniqueId]
                ?.scores
                ?.getOrPut(e.damager.uniqueId, { AtomicDouble(0.0) })
                ?.addAndGet(e.damage)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBossDie(e: PlayerDeathEvent) {
        val player = e.entity
        val bossData = Events.registedBosses[player.uniqueId] ?: return
        bossData.lives -= 1
        if (bossData.lives > 0) {
            e.deathMessage = ("&7${bossData.displayName}&c${e.deathMessage?.removePrefix(player.name)}. &7&o" +
                    if (bossData.lives != 1) "${bossData.lives} lives remain." else "Their last life remains.")
                    .color()
            return
        }
        val sortedScores = bossData.scores.map { it }.sortedBy { (_, value) -> value.get() }

        var i = 1
        Bukkit.broadcastMessage("""
            &6&l-=&7&l${bossData.displayName}&6&l has fallen!=-
            &6Top Damagers:
            ${ //print top 3 players (or less if 3 aren't present)
            sortedScores.take(3).joinToString(separator = "\n") {
                "&6#${i++} &7${Bukkit.getPlayer(it.key)?.displayName}: &8${it.value.get().roundToInt()}"
            }
        }
            """.trimIndent().color())

        Events.registedBosses -= player.uniqueId
        e.deathMessage = ("&c&o${player.killer?.displayName}&c&o got the final blow. &7&o" +
                when (Events.registedBosses.size) {
                    0 -> "No more remain."
                    1 -> "One more remains."
                    else -> "${Events.registedBosses.size} others remain."
                }).color()
    }

    @EventHandler
    fun damagingSnowballs(e: ProjectileHitEvent) {
        val snowball = e.entity as? Snowball ?: return
        val hit = e.hitEntity as? LivingEntity ?: return
        hit.damage(snowball.item.itemMeta?.lore?.find { it.startsWith("Damage: ") }
                ?.removePrefix("Damage: ")?.toDouble() ?: 0.0)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun disableDeathMessagesInWorld(e: PlayerDeathEvent) {
        if (e.entity.world.name in MineInAbyssMainConfig.data.disableDeathMessagesIn)
            e.deathMessage = null
    }
}