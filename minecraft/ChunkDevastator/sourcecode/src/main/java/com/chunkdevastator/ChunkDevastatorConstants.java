package com.chunkdevastator;

import net.minecraft.resources.Identifier;

/**
 * Shared constants for ChunkDevastator.
 * NOTE: on 26.x resource keys are net.minecraft.resources.Identifier (renamed from ResourceLocation).
 */
public final class ChunkDevastatorConstants {
    public static final String MOD_ID = "chunkdevastator";

    public static final Identifier DOOM_START_ID = Identifier.fromNamespaceAndPath(MOD_ID, "doom_start");
    public static final Identifier DOOM_SYNC_ID = Identifier.fromNamespaceAndPath(MOD_ID, "doom_sync");
    public static final Identifier DOOM_CLEARED_ID = Identifier.fromNamespaceAndPath(MOD_ID, "doom_cleared");

    /** Countdown length: 5 seconds at 20 tps. */
    public static final int COUNTDOWN_TICKS = 100;

    /** Warning colors (RGB) per remaining second: t=5 yellow ... t=1 red. */
    public static final int[] WARNING_COLORS = {
            0xFFFFC800, // 5s - yellow
            0xFFFFA000, // 4s - orange
            0xFFFF7A00, // 3s - deep orange
            0xFFFF4500, // 2s - red-orange
            0xFFFF1F00  // 1s - red
    };

    private ChunkDevastatorConstants() {
    }
}
