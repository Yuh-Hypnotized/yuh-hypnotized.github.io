package com.chunkdevastator.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chunkdevastator.net.DoomPayloads;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Client-side mirror of the server's active dooms. Ticked down locally so the
 * countdown stays smooth; the server's packets are the source of truth for
 * arming/removal and full re-sync on join.
 */
public final class ClientDoomRegistry {
    private static final float TICK_SECONDS = 1.0F / 20.0F;

    private final Map<Key, Float> remainingSeconds = new HashMap<>();

    public void add(ResourceKey<Level> dimension, int chunkX, int chunkZ, long remainingTicks) {
        remainingSeconds.put(new Key(dimension, chunkX, chunkZ), remainingTicks / 20.0F);
    }

    public void remove(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        remainingSeconds.remove(new Key(dimension, chunkX, chunkZ));
    }

    /** Full re-sync on join (SPEC 2.4). */
    public void replaceAll(List<DoomPayloads.DoomSyncEntry> entries) {
        remainingSeconds.clear();
        for (DoomPayloads.DoomSyncEntry e : entries) {
            add(e.dimension(), e.chunkX(), e.chunkZ(), e.remainingTicks());
        }
    }

    /** Called every client tick: count the dooms down locally. */
    public void tick() {
        remainingSeconds.replaceAll((key, seconds) -> seconds - TICK_SECONDS);
        remainingSeconds.values().removeIf(seconds -> seconds <= 0.0F);
    }

    /** Dooms in the given dimension (render only what the player can see anyway). */
    public List<Entry> visible(ClientLevel level) {
        List<Entry> out = new ArrayList<>();
        for (Map.Entry<Key, Float> e : remainingSeconds.entrySet()) {
            if (e.getKey().dimension().equals(level.dimension())) {
                out.add(new Entry(e.getKey().dimension(), e.getKey().chunkX(), e.getKey().chunkZ(), e.getValue()));
            }
        }
        return out;
    }

    public record Entry(ResourceKey<Level> dimension, int chunkX, int chunkZ, float remainingSeconds) {
    }

    private record Key(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
    }
}
