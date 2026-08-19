package com.chunkdevastator.doom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chunkdevastator.ChunkDevastatorConstants;
import com.chunkdevastator.net.DoomPayloads;
import com.chunkdevastator.nuke.ChunkNuker;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * Server-side heart of the mod. Holds all active dooms per dimension, ticks them,
 * persists them (SPEC: countdown survives death/logout/restart) and fires the
 * chunk voiding.
 */
public final class DoomManager {
    private static DoomManager instance;

    private final MinecraftServer server;
    private final Map<ResourceKey<Level>, Map<ChunkPos, Doom>> dooms = new HashMap<>();
    private final List<RestoredDoom> pending = new ArrayList<>(); // persisted dooms for not-yet-loaded dimensions
    private ChunkDevastatorData data;
    private boolean clearing = false;

    private DoomManager(MinecraftServer server) {
        this.server = server;
    }

    public static DoomManager get(MinecraftServer server) {
        if (instance == null || instance.server != server) {
            instance = new DoomManager(server);
        }
        return instance;
    }

    /** Called on SERVER_STARTED (or lazily from tick): restore persisted dooms. */
    public void load() {
        if (data != null) {
            return;
        }
        data = ChunkDevastatorData.get(server);
        for (ChunkDevastatorData.Entry e : data.getDooms()) {
            pending.add(new RestoredDoom(e.dimension(), new ChunkPos(e.chunkX(), e.chunkZ()), e.remainingTicks()));
        }
    }

    /** Called on SERVER_STOPPING: persist every active + pending doom with remaining ticks. */
    public void save() {
        if (data == null) {
            return;
        }
        long tick = server.getTickCount();
        List<ChunkDevastatorData.Entry> entries = new ArrayList<>();
        for (Map<ChunkPos, Doom> byDim : dooms.values()) {
            for (Doom d : byDim.values()) {
                entries.add(new ChunkDevastatorData.Entry(d.dimension(), d.chunkPos().x(), d.chunkPos().z(), d.remainingTicks(tick)));
            }
        }
        for (RestoredDoom r : pending) {
            entries.add(new ChunkDevastatorData.Entry(r.dimension(), r.chunkPos().x(), r.chunkPos().z(), r.remainingTicks()));
        }
        data.setDooms(entries);
    }

    /**
     * Arm a doom for the chunk the player was standing in when hurt.
     * Same-chunk repeats are ignored (the running countdown is not reset).
     */
    public void arm(ServerLevel level, ChunkPos pos) {
        if (clearing) {
            return; // recursion guard: clearing must never arm new dooms
        }
        Map<ChunkPos, Doom> byDim = dooms.computeIfAbsent(level.dimension(), k -> new HashMap<>());
        if (byDim.containsKey(pos)) {
            return;
        }
        activate(new Doom(level.dimension(), pos, server.getTickCount(), ChunkDevastatorConstants.COUNTDOWN_TICKS));
    }

    private void activate(Doom doom) {
        dooms.computeIfAbsent(doom.dimension(), k -> new HashMap<>()).put(doom.chunkPos(), doom);
        broadcastStart(doom);
        if (data != null) {
            data.setDirty();
        }
    }

    /** Called every server tick (END_SERVER_TICK). */
    public void tick() {
        if (data == null) {
            load(); // safety net: overworld exists by the first tick
        }
        long tick = server.getTickCount();

        // Promote persisted dooms whose dimension is loaded again.
        for (Iterator<RestoredDoom> it = pending.iterator(); it.hasNext(); ) {
            RestoredDoom r = it.next();
            if (server.getLevel(r.dimension()) == null) {
                continue;
            }
            it.remove();
            long remaining = r.remainingTicks();
            long startTick = tick - (ChunkDevastatorConstants.COUNTDOWN_TICKS - remaining);
            activate(new Doom(r.dimension(), r.chunkPos(), startTick, ChunkDevastatorConstants.COUNTDOWN_TICKS));
        }

        // Fire due dooms.
        List<Doom> due = new ArrayList<>();
        for (Map<ChunkPos, Doom> byDim : dooms.values()) {
            byDim.values().removeIf(d -> {
                if (d.isDone(tick)) {
                    due.add(d);
                    return true;
                }
                return false;
            });
        }
        for (Doom d : due) {
            ServerLevel level = server.getLevel(d.dimension());
            if (level != null) {
                clearing = true;
                try {
                    ChunkNuker.voidChunk(level, d.chunkPos());
                } finally {
                    clearing = false;
                }
            }
            broadcastCleared(d);
        }
        if (!due.isEmpty()) {
            if (data != null) {
            data.setDirty();
        }
        }
    }

    /** Full state re-sync for a freshly joined player (SPEC 2.4). */
    public void syncAll(ServerPlayer player) {
        long tick = server.getTickCount();
        List<DoomPayloads.DoomSyncEntry> entries = new ArrayList<>();
        for (Map<ChunkPos, Doom> byDim : dooms.values()) {
            for (Doom d : byDim.values()) {
                entries.add(new DoomPayloads.DoomSyncEntry(d.dimension(), d.chunkPos().x(), d.chunkPos().z(), d.remainingTicks(tick)));
            }
        }
        for (RestoredDoom r : pending) {
            entries.add(new DoomPayloads.DoomSyncEntry(r.dimension(), r.chunkPos().x(), r.chunkPos().z(), r.remainingTicks()));
        }
        ServerPlayNetworking.send(player, new DoomPayloads.DoomSyncPayload(entries));
    }

    private void broadcastStart(Doom doom) {
        long remaining = doom.remainingTicks(server.getTickCount());
        DoomPayloads.DoomStartPayload payload =
                new DoomPayloads.DoomStartPayload(doom.dimension(), doom.chunkPos().x(), doom.chunkPos().z(), remaining);
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, payload);
        }
    }

    private void broadcastCleared(Doom doom) {
        DoomPayloads.DoomClearedPayload payload =
                new DoomPayloads.DoomClearedPayload(doom.dimension(), doom.chunkPos().x(), doom.chunkPos().z());
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(p, payload);
        }
    }

    private record RestoredDoom(ResourceKey<Level> dimension, ChunkPos chunkPos, long remainingTicks) {
    }
}