package com.derongan.minecraft.mineinabyss.camelot

import com.derongan.minecraft.mineinabyss.camelot.bosses.BossData
import com.derongan.minecraft.mineinabyss.mineInAbyss
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.success
import org.bukkit.Bukkit


@ExperimentalCommandDSL
object EventCommands : IdofrontCommandExecutor() {
    override val commands = commands(mineInAbyss) {
        "events" {
            "boss" {
                "ignoreddamagers" {
                    "list" {
                        action {
                            sender.success("Ignored damagers: ${Events.ignoredDamagers.joinToString { Bukkit.getPlayer(it)?.name ?: "offline" }}")
                        }
                    }
                    "remove" {
                        playerAction {
                            Events.ignoredDamagers -= player.uniqueId
                            sender.success("Removed ${player.name} from ignore damagers.")
                        }
                    }
                    "add" {
                        playerAction {
                            Events.ignoredDamagers += player.uniqueId
                            sender.success("Added ${player.name} to ignored damagers.")
                        }
                    }
                }

                "list" {
                    action {
                        sender.success("Registered bosses: ${Events.registedBosses.values.joinToString { it.displayName }}")
                    }
                }
                "add" {
                    val displayName by stringArg()
                    val lives by intArg()
                    playerAction {
                        val coloredName = displayName.replace('_', ' ').color()
                        Events.registedBosses[player.uniqueId] = BossData(coloredName, lives)
                        sender.success("\"$coloredName&a\" was added with $lives lives.".color())
                    }
                }
                "remove" {
                    playerAction {
                        Events.registedBosses -= player.uniqueId
                        sender.success("Removed ${player.displayName} from bosses.")
                    }
                }
                "reset" {
                    playerAction {
                        Events.registedBosses[player.uniqueId]?.scores?.clear()
                        sender.success("Reset ${player.displayName}'s boss damage.")
                    }
                }
            }
        }
    }
}