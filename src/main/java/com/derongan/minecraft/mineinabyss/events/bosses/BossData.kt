package com.derongan.minecraft.mineinabyss.events.bosses

import com.google.common.util.concurrent.AtomicDouble
import java.util.*

class BossData(
        val displayName: String,
        var lives: Int,
        val scores: MutableMap<UUID, AtomicDouble> = mutableMapOf(),
)