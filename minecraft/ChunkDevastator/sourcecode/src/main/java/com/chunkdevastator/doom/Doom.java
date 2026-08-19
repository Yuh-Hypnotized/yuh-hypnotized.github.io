package com.chunkdevastator.doom;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * A pending chunk destruction ("doom"): the chunk column that will be voided
 * when the countdown elapses.
 */
public record Doom(ResourceKey<Level> dimension, ChunkPos chunkPos, long startTick, int durationTicks) {

    public long remainingTicks(long serverTick) {
        return Math.max(0L, startTick + durationTicks - serverTick);
    }

    public boolean isDone(long serverTick) {
        return serverTick >= startTick + durationTicks;
    }
}
