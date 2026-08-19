package com.chunkdevastator.nuke;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Voids an entire chunk column (SPEC 3.2): every block except
 * minecraft:end_portal_frame / minecraft:end_portal, including bedrock and
 * block entities (inventories destroyed). Entities are untouched.
 *
 * 26.2 notes (verified by decompiling the game jar):
 *  - LevelChunk#setBlockState(pos, state, 0) DOES change the block data and
 *    internally updates heightmaps + light; flags=0 only disables side effects.
 *  - Level#setBlocksDirty(BlockPos, BlockState, BlockState) is a NO-OP on 26.2,
 *    so clients must be notified via ServerChunkCache#blockChanged(BlockPos)
 *    (queues aggregated section updates to tracking players).
 */
public final class ChunkNuker {
    private ChunkNuker() {
    }

    public static void voidChunk(ServerLevel level, ChunkPos pos) {
        LevelChunk chunk = level.getChunk(pos.x(), pos.z());
        BlockState air = Blocks.AIR.defaultBlockState();
        int minY = level.getMinY();
        int maxY = level.getMaxY(); // exclusive upper bound

        List<BlockPos> changed = new ArrayList<>();

        // 1) Void every block except survivors (end portal frame + portal block).
        for (int y = minY; y < maxY; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    BlockPos bp = pos.getBlockAt(x, y, z);
                    BlockState state = chunk.getBlockState(bp);
                    if (state.isAir()) {
                        continue;
                    }
                    if (state.is(Blocks.END_PORTAL_FRAME) || state.is(Blocks.END_PORTAL)) {
                        continue;
                    }
                    chunk.setBlockState(bp, air, 0); // flags=0: change data, no per-block side effects
                    changed.add(bp);
                }
            }
        }
        if (changed.isEmpty()) {
            return;
        }

        // 2) Destroy block entities (and their inventories) explicitly.
        for (BlockPos bp : new ArrayList<>(chunk.getBlockEntities().keySet())) {
            level.removeBlockEntity(bp);
        }
        chunk.clearAllBlockEntities(); // VERIFIED on 26.2 (compiled)

        // 3) Persist the cleared chunk.
        chunk.markUnsaved();

        // 4) Tell the clients what changed (Level.setBlocksDirty is a no-op on 26.2).
        ServerChunkCache cache = (ServerChunkCache) level.getChunkSource();
        for (BlockPos bp : changed) {
            cache.blockChanged(bp);
        }
    }
}
