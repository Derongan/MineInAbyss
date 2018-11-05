package com.derongan.minecraft.mineinabyss.relic.behaviour.behaviours;

import com.derongan.minecraft.mineinabyss.relic.behaviour.CooldownRelicBehaviour;
import com.derongan.minecraft.mineinabyss.relic.behaviour.UseRelicBehaviour;
import com.derongan.minecraft.mineinabyss.relic.RelicUseListener;
import com.derongan.minecraft.mineinabyss.relic.relics.RelicType;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class IncineratorRelicBehaviour implements UseRelicBehaviour, CooldownRelicBehaviour {
    RelicType type;
    boolean onCooldown;

    @Override
    public void onUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);
        }
        if (onCooldown) {
            return;
        }

        Player player = event.getPlayer();

        List<Block> blocks = player.getLineOfSight(RelicUseListener.passable, 100);

        blocks.subList(10, blocks.size()).forEach(a -> {
            for (int i = -2; i < 5; i++) {
                for (int j = -2; j < 5; j++) {
                    for (int k = -2; k < 5; k++) {

                        Block relative = a.getRelative(i, j, k);

                        if (Math.random() > .999)
                            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, relative.getLocation(), 1);

//                        if (Math.abs(i) == 2 || Math.abs(j) == 2 || Math.abs(k) == 2) {
//                            if (relative.getType() != Material.AIR) {
//                                relative.setType(Material.COAL_BLOCK);
//                            }
//                        } else {
//                            if (Math.random() > .1)
//                                relative.setType(Material.AIR);
//                            else
//                                relative.setType(Material.FIRE);
//
//                        }
                        if (i == 0 && j == 0 && k == 0) {
                            if (Math.random() > .5) {
                                player.getWorld().spawnParticle(Particle.SMOKE_LARGE, relative.getLocation(), 1);
                                player.getWorld().playSound(relative.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5f, .5f);
                                player.getWorld().playSound(relative.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 5f, .5f);
                            }

                            player.getWorld().getNearbyEntities(relative.getLocation(), 3, 3, 3).forEach(b -> {
                                if (b instanceof LivingEntity)
                                    ((LivingEntity) b).damage(100);
                            });
                        }
                    }
                }
            }
        });
        CooldownRelicBehaviour.registerCooldown(event.getPlayer(), 200, type);
        onCooldown = true;
    }

    @Override
    public void cooledDown() {
        onCooldown = false;
    }

    @Override
    public void setRelicType(RelicType type) {
        this.type = type;
    }
}