package com.derongan.minecraft.mineinabyss.plugin.Relic.Distribution.Rarity.RarityModifiers;

import org.bukkit.ChunkSnapshot;

@FunctionalInterface
public interface RarityModifier {
    double modify(int x, int y, int z, ChunkSnapshot snapshot, double initial);
}