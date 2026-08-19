package com.chunkdevastator.doom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chunkdevastator.ChunkDevastatorConstants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

/**
 * Persistent state: every active (and pending) doom, saved with its remaining
 * ticks so a server restart resumes countdowns (SPEC "countdown survives restart").
 *
 * 26.2 rewrote SavedData to a codec-driven API: a SavedDataType holds the id,
 * a default constructor, the Codec and the DataFixTypes; SavedDataStorage then
 * handles (de)serialization.
 */
public final class ChunkDevastatorData extends SavedData {
    private static final Codec<ChunkDevastatorData> CODEC = Entry.CODEC.listOf()
            .xmap(ChunkDevastatorData::new, data -> data.dooms);

    public static final SavedDataType<ChunkDevastatorData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(ChunkDevastatorConstants.MOD_ID, "data"),
            ChunkDevastatorData::new,
            CODEC,
            DataFixTypes.SAVED_DATA_MAP_DATA);

    private final List<Entry> dooms;

    private ChunkDevastatorData() {
        this.dooms = new ArrayList<>();
    }

    private ChunkDevastatorData(List<Entry> dooms) {
        this.dooms = dooms;
    }

    public static ChunkDevastatorData get(MinecraftServer server) {
        SavedDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(TYPE);
    }

    public List<Entry> getDooms() {
        return dooms;
    }

    public void setDooms(Collection<Entry> entries) {
        dooms.clear();
        dooms.addAll(entries);
        setDirty();
    }

    public record Entry(ResourceKey<Level> dimension, int chunkX, int chunkZ, long remainingTicks) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Entry::dimension),
                Codec.INT.fieldOf("chunkX").forGetter(Entry::chunkX),
                Codec.INT.fieldOf("chunkZ").forGetter(Entry::chunkZ),
                Codec.LONG.fieldOf("remainingTicks").forGetter(Entry::remainingTicks)
        ).apply(instance, Entry::new));
    }
}