package com.derongan.minecraft.mineinabyss.Ascension;

import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.MineInAbyss;
import com.derongan.minecraft.mineinabyss.Player.PlayerData;
import com.derongan.minecraft.mineinabyss.World.AbyssWorldManager;
import com.derongan.minecraft.mineinabyss.World.Layer;
import com.derongan.minecraft.mineinabyss.World.Section;
import com.derongan.minecraft.mineinabyss.World.SectionUtils;
import com.derongan.minecraft.mineinabyss.util.TickUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.bukkit.attribute.Attribute;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.derongan.minecraft.mineinabyss.World.MinecraftConstants.WORLD_HEIGHT;

public class AscensionListener implements Listener {
    private AbyssContext context;
    private Set<UUID> recentlyMovedPlayers;

    public AscensionListener(AbyssContext context) {
        this.context = context;

        recentlyMovedPlayers = new HashSet<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent moveEvent) {
        Player player = moveEvent.getPlayer();

        if (recentlyMovedPlayers.contains(player.getUniqueId()))
            return;

        AbyssWorldManager manager = context.getWorldManager();

        if (!manager.isAbyssWorld(player.getWorld().getName()))
            return;

        Location from = moveEvent.getFrom();
        Location to = moveEvent.getTo();

        double changeY = to.getY() - from.getY();

        PlayerData playerData = context.getPlayerDataMap().get(player.getUniqueId());

        Section currentSection = playerData.getCurrentSection();

        if(playerData.isAffectedByCurse()){
            double dist = playerData.getDistanceAscended();
            playerData.setDistanceAscended(Math.max(dist + changeY,0));

            if(dist >= 10){
                playerData.getCurrentLayer().getAscensionEffects().forEach(a->{
                   a.build().applyEffect(player,10);
                });
                playerData.setDistanceAscended(0);
            }
        }

        if(playerData.isAnchored())
            return;

        if (changeY > 0) {
            Section newSection = manager.getSectonAt(currentSection.getIndex() - 1);

            if (newSection == null)
                return;

            int shared = SectionUtils.getSharedBlocks(currentSection, newSection);

            if (to.getY() > WORLD_HEIGHT - .3 * shared) {
                teleportBetweenSections(playerData, to, currentSection, newSection);
            }

        } else if (changeY < 0) {
            Section newSection = manager.getSectonAt(currentSection.getIndex() + 1);

            if (newSection == null)
                return;

            int shared = SectionUtils.getSharedBlocks(currentSection, newSection);

            if (to.getY() < .3 * shared) {
                teleportBetweenSections(playerData, to, currentSection, newSection);
            }
        }
    }

    private void teleportBetweenSections(PlayerData data, Location to, Section oldSection, Section newSection) {
        Location newLoc = SectionUtils.getCorrespondingLocation(oldSection, newSection, to);

        Vector oldVelocity = data.getPlayer().getVelocity();
        data.getPlayer().teleport(newLoc);
        data.getPlayer().setVelocity(oldVelocity);

        recentlyMovedPlayers.add(data.getPlayer().getUniqueId());

        Bukkit.getScheduler().scheduleSyncDelayedTask(
                MineInAbyss.getInstance(),
                () -> recentlyMovedPlayers.remove(data.getPlayer().getUniqueId()),
                TickUtils.milisecondsToTicks(500)
        );


        data.setCurrentSection(newSection);
        data.setCurrentLayer(newSection.getLayer());
        data.setDistanceAscended(0); // Reset distance

        if (newSection.getLayer() != oldSection.getLayer()) {
            data.getPlayer().sendTitle(newSection.getLayer().getName(), newSection.getLayer().getSub(), 50, 10, 20);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent deathEvent) {
        Player player = deathEvent.getEntity();

        // Catching for bad stuff
        if (player == null)
            return;

        AbyssWorldManager manager = context.getWorldManager();

        Layer layerOfDeath = context.getPlayerDataMap().get(player.getUniqueId()).getCurrentLayer();

        context.getPlayerDataMap().get(player.getUniqueId()).setCurrentSection(manager.getSectonAt(0));
        context.getPlayerDataMap().get(player.getUniqueId()).setCurrentLayer(manager.getLayerAt(0));

        if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
        }

        deathEvent.setDeathMessage(deathEvent.getDeathMessage() + layerOfDeath.getDeathMessage());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent){
        Player player = playerTeleportEvent.getPlayer();
        PlayerData data = context.getPlayerDataMap().get(player.getUniqueId());
    }
}
