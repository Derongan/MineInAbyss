package com.derongan.minecraft.mineinabyss;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.Player.PlayerData;
import com.derongan.minecraft.mineinabyss.World.AbyssWorldManager;
import com.derongan.minecraft.mineinabyss.World.AbyssWorldManagerImpl;
import com.derongan.minecraft.mineinabyss.World.EntityChunkManager;
import com.derongan.minecraft.mineinabyss.World.EntityChunkManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Stores context for the plugin, such as the plugin instance
 */
public class AbyssContext {
    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private Plugin plugin;
    private Logger logger;
    private Configuration config;

    private WorldManager realWorldManager;

    private AbyssWorldManager worldManager;
    private EntityChunkManager entityChunkManager;

    public AbyssContext(Configuration config) {
        this.config = config;
        worldManager = new AbyssWorldManagerImpl(getConfig());

        realWorldManager = Bukkit.getServicesManager().load(WorldManager.class);

        entityChunkManager = new EntityChunkManagerImpl(this);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Configuration getConfig() {
        return config;
    }

    public AbyssWorldManager getWorldManager() {
        return worldManager;
    }
    public WorldManager getRealWorldManager() {
        return realWorldManager;
    }

    public EntityChunkManager getEntityChunkManager() {
        return entityChunkManager;
    }
}
